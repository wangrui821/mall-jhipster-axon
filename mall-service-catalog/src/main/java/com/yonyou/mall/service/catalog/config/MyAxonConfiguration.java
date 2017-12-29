package com.yonyou.mall.service.catalog.config;

import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.yonyou.mall.service.catalog.aggregate.ProductAggregate;
import com.yonyou.mall.service.catalog.aggregate.ProductCommandHandler;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.model.Repository;
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
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * Custom Axon configuration
 */
@Configuration
public class MyAxonConfiguration {
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
        return new AggregateSnapshotter(eventStore, productAggregateFactory());
    }

    @Bean
    @Scope("prototype")
    public ProductAggregate productAggregate() {
        return new ProductAggregate();
    }

    @Bean
    public AggregateFactory<ProductAggregate> productAggregateFactory() {
        SpringPrototypeAggregateFactory<ProductAggregate> aggregateFactory = new SpringPrototypeAggregateFactory<>();
        aggregateFactory.setPrototypeBeanName("productAggregate");
        return aggregateFactory;
    }

    @Bean
    public Repository<ProductAggregate> productAggregateRepository() {
        EventCountSnapshotTriggerDefinition snapshotTriggerDefinition = new EventCountSnapshotTriggerDefinition(
            snapshotter(), 10);
        EventSourcingRepository<ProductAggregate> repository = new EventSourcingRepository<>(productAggregateFactory(),
            eventStore, snapshotTriggerDefinition);
        return repository;
    }

    @Bean
    public ProductCommandHandler productCommandHandler() {
        return new ProductCommandHandler(productAggregateRepository());
    }


    @Value("${axon.amqp.exchange}")
    private String axonAmqpExchange;

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(axonAmqpExchange).durable(true).build();
    }

    @Bean
    public Queue catalogQueue() {
        return QueueBuilder.durable("catalog").build();
    }

    @Bean
    public Binding catalogQueueBinding() {
        return BindingBuilder.bind(catalogQueue()).to(exchange()).with("#.catalog.#").noargs();
    }


    @Primary
    @Bean
    public CommandRouter mySpringCloudCommandRouter(DiscoveryClient discoveryClient) {
        return new MySpringCloudCommandRouter(discoveryClient, new AnnotationRoutingStrategy());
    }
}
