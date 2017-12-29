package com.yonyou.mall.service.catalog.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.netflix.appinfo.ApplicationInfoManager;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.distributed.RoutingStrategy;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.axonframework.springcloud.commandhandling.SpringCloudCommandRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 重写Axon的SpringCloudCommandRouter，解决自定义元数据在启动后丢失的问题。
 *
 * @author Jacky Wang
 * @date 2017-12-29 14:44:31
 */
public class MySpringCloudCommandRouter extends SpringCloudCommandRouter {
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    private static final String LOAD_FACTOR = "loadFactor";
    private static final String SERIALIZED_COMMAND_FILTER = "serializedCommandFilter";
    private static final String SERIALIZED_COMMAND_FILTER_CLASS_NAME = "serializedCommandFilterClassName";

    @Deprecated
    public MySpringCloudCommandRouter(DiscoveryClient discoveryClient, RoutingStrategy routingStrategy,
        Serializer serializer) {
        super(discoveryClient, routingStrategy, serializer);
    }

    public MySpringCloudCommandRouter(DiscoveryClient discoveryClient, RoutingStrategy routingStrategy) {
        super(discoveryClient, routingStrategy);
    }

    public MySpringCloudCommandRouter(DiscoveryClient discoveryClient, RoutingStrategy routingStrategy,
        Predicate<ServiceInstance> serviceInstanceFilter) {
        super(discoveryClient, routingStrategy, serviceInstanceFilter);
    }

    @Override
    public void updateMembership(int loadFactor, Predicate<? super CommandMessage<?>> commandFilter) {
        super.updateMembership(loadFactor, commandFilter);

        // 将自定义元数据放在服务实例中
        SerializedObject<String> serializedCommandFilter = serializer.serialize(commandFilter, String.class);
        Map<String, String> customMetadataMap = new HashMap<>(3);
        customMetadataMap.put(LOAD_FACTOR, Integer.toString(loadFactor));
        customMetadataMap.put(SERIALIZED_COMMAND_FILTER, serializedCommandFilter.getData());
        customMetadataMap.put(SERIALIZED_COMMAND_FILTER_CLASS_NAME, serializedCommandFilter.getType().getName());
        applicationInfoManager.registerAppMetadata(customMetadataMap);
    }
}
