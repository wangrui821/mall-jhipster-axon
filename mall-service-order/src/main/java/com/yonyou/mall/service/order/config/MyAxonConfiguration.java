package com.yonyou.mall.service.order.config;

import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.rabbitmq.client.Channel;
import com.yonyou.mall.service.order.aggregate.OrderAggregate;
import com.yonyou.mall.service.order.aggregate.OrderCommandHandler;
import com.yonyou.mall.service.order.saga.OrderSaga;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.axonframework.eventhandling.saga.AbstractSagaManager;
import org.axonframework.eventhandling.saga.AnnotatedSagaManager;
import org.axonframework.eventhandling.saga.ResourceInjector;
import org.axonframework.eventhandling.saga.SagaRepository;
import org.axonframework.eventhandling.saga.repository.AnnotatedSagaRepository;
import org.axonframework.eventhandling.saga.repository.SagaStore;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.MongoTemplate;
import org.axonframework.mongo.eventhandling.saga.repository.MongoSagaStore;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoFactory;
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean;
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory;
import org.axonframework.spring.saga.SpringResourceInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * Created by Administrator on 2017/3/7.
 */
@Configuration
public class MyAxonConfiguration {
    private final Logger log = LoggerFactory.getLogger(MyAxonConfiguration.class);

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Value("${spring.data.mongodb.domain-events-collection-name}")
    private String domainEventsCollectionName;

    @Value("${spring.data.mongodb.snapshot-events-collection-name}")
    private String snapshotEventsCollectionName;

    @Value("${spring.data.mongodb.sagas-collection-name}")
    private String sagasCollectionName;

    @Value("${spring.data.mongodb.tracking-tokens-collection-name}")
    private String trackingTokensCollectionName;

    @Bean
    public Serializer axonSerializer() {
        return new JacksonSerializer();
    }

    @Bean
    public MongoClient mongoClient() {
        MongoFactory mongoFactory = new MongoFactory();
        mongoFactory.setMongoAddresses(Arrays.asList(new ServerAddress(mongoHost, mongoPort)));
        return mongoFactory.createMongo();
    }

    @Bean(name = "axonMongoTemplate")
    public MongoTemplate axonMongoTemplate() {
        MongoTemplate mongoTemplate = new DefaultMongoTemplate(mongoClient(), mongoDatabase).withDomainEventsCollection(
            domainEventsCollectionName).withSnapshotCollection(snapshotEventsCollectionName).withSagasCollection(
            sagasCollectionName).withTrackingTokenCollection(trackingTokensCollectionName);
        return mongoTemplate;
    }

    @Bean
    public EventStorageEngine eventStorageEngine() {
        return new MongoEventStorageEngine(axonSerializer(), null, axonMongoTemplate(),
            new DocumentPerEventStorageStrategy());
    }

    @Bean
    public SagaStore sagaStore() {
        return new MongoSagaStore(axonMongoTemplate(), axonSerializer());
    }


    @Autowired
    private EventStore eventStore;

    @Bean
    public SpringAggregateSnapshotterFactoryBean springAggregateSnapshotterFactoryBean() {
        return new SpringAggregateSnapshotterFactoryBean();
    }

    public Snapshotter snapshotter() {
        return new AggregateSnapshotter(eventStore, orderAggregateFactory());
    }

    @Bean
    @Scope("prototype")
    public OrderAggregate orderAggregate() {
        return new OrderAggregate();
    }

    @Bean
    public AggregateFactory<OrderAggregate> orderAggregateFactory() {
        SpringPrototypeAggregateFactory<OrderAggregate> aggregateFactory = new SpringPrototypeAggregateFactory<>();
        aggregateFactory.setPrototypeBeanName("orderAggregate");
        return aggregateFactory;
    }

    @Bean
    public Repository<OrderAggregate> orderAggregateRepository() {
        EventCountSnapshotTriggerDefinition snapshotTriggerDefinition = new EventCountSnapshotTriggerDefinition(
            snapshotter(), 10);
        EventSourcingRepository<OrderAggregate> repository = new EventSourcingRepository<>(orderAggregateFactory(),
            eventStore, snapshotTriggerDefinition);
        return repository;
    }

    @Bean
    public OrderCommandHandler orderCommandHandler() {
        return new OrderCommandHandler(orderAggregateRepository());
    }


    @Bean
    public ResourceInjector resourceInjector() {
        return new SpringResourceInjector();
    }

    @Bean
    public SagaRepository<OrderSaga> orderSagaRepository() {
        return new AnnotatedSagaRepository<>(OrderSaga.class, sagaStore(), resourceInjector());
    }

    @Bean
    public AbstractSagaManager<OrderSaga> orderSagaManager() {
        return new AnnotatedSagaManager<>(OrderSaga.class, orderSagaRepository());
    }

    @Bean
    // Very important: Make OrderSaga subscribe event from catalog service
    public EventProcessor orderSagaCatalogEventProcessor() {
        SubscribingEventProcessor eventProcessor = new SubscribingEventProcessor("orderSagaCatalogEventProcessor",
            orderSagaManager(), catalogQueueMessageSource(axonSerializer()));
        eventProcessor.start();
        return eventProcessor;
    }

    @Bean
    public SpringAMQPMessageSource catalogQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(serializer) {
            @RabbitListener(queues = "catalog")
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                log.debug("Message received from catalog service: {}.", message);
                super.onMessage(message, channel);
            }
        };
    }


    @Primary
    @Bean
    public CommandRouter mySpringCloudCommandRouter(DiscoveryClient discoveryClient) {
        return new MySpringCloudCommandRouter(discoveryClient, new AnnotationRoutingStrategy());
    }
}
