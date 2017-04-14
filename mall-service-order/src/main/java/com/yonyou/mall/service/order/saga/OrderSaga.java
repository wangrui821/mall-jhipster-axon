package com.yonyou.mall.service.order.saga;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yonyou.mall.service.catalog.command.ReserveProductCommand;
import com.yonyou.mall.service.catalog.command.RollbackProductReserveCommand;
import com.yonyou.mall.service.catalog.event.ProductNotEnoughEvent;
import com.yonyou.mall.service.catalog.event.ProductReserveRollbackedEvent;
import com.yonyou.mall.service.catalog.event.ProductReservedEvent;
import com.yonyou.mall.service.order.command.ConfirmOrderCreateCommand;
import com.yonyou.mall.service.order.command.RollbackOrderCreateCommand;
import com.yonyou.mall.service.order.domain.OrderProduct;
import com.yonyou.mall.service.order.event.OrderCreateConfirmedEvent;
import com.yonyou.mall.service.order.event.OrderCreateRollbackedEvent;
import com.yonyou.mall.service.order.event.OrderCreatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Saga class for order, managing the process of order creation.
 *
 * @author WangRui
 */
@Saga
public class OrderSaga {
    private final Logger log = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private CommandGateway commandGateway;

    @JsonProperty
    private String orderCode;

    @JsonProperty
    private Map<String, OrderProduct> toReserve;

    @JsonProperty
    private int toReserveCount;

    @JsonProperty
    private Set<String> toRollback;

    @JsonProperty
    private boolean rollbackRequired;

    @StartSaga
    @SagaEventHandler(associationProperty = "code", keyName = "orderCode")
    public void handle(OrderCreatedEvent event) {
        orderCode = event.getCode();
        toReserve = new HashMap<>();
        event.getOrderProducts().forEach(orderProduct -> {
            toReserve.put(orderProduct.getProductCode(), orderProduct);
        });
        toReserveCount = toReserve.size();
        toRollback = new HashSet<>();
        event.getOrderProducts().forEach(orderProduct -> {
            ReserveProductCommand command = new ReserveProductCommand(orderCode, orderProduct.getProductCode(),
                orderProduct.getQuantity());
            commandGateway.send(command);
        });
    }

    @SagaEventHandler(associationProperty = "orderCode")
    public void handle(ProductNotEnoughEvent event) {
        log.info("Product {} is not enough", event.getProductCode());
        --toReserveCount;
        rollbackRequired = true;
        if (toReserveCount == 0) {
            tryFinishCreateOrder();
        }
    }

    private void tryFinishCreateOrder() {
        if (rollbackRequired) {
            toReserve.forEach((productCode, product) -> {
                if (!product.isReserved()) {
                    return;
                }
                toRollback.add(product.getProductCode());
                commandGateway.send(new RollbackProductReserveCommand(orderCode, productCode, product.getQuantity()));
            });
            if (toRollback.isEmpty()) {
                commandGateway.send(new RollbackOrderCreateCommand(orderCode));
            }
        }
        else {
            commandGateway.send(new ConfirmOrderCreateCommand(orderCode));
        }
    }

    @SagaEventHandler(associationProperty = "orderCode")
    public void handle(ProductReservedEvent event) {
        OrderProduct reservedProduct = toReserve.get(event.getProductCode());
        reservedProduct.setReserved(true);
        --toReserveCount;
        // Q: Will a concurrent issue raise?
        if (toReserveCount == 0) {
            tryFinishCreateOrder();
        }
    }

    @SagaEventHandler(associationProperty = "orderCode")
    public void handle(ProductReserveRollbackedEvent event) {
        toRollback.remove(event.getProductCode());
        if (toRollback.isEmpty()) {
            commandGateway.send(new RollbackOrderCreateCommand(event.getOrderCode()));
        }
    }

    @SagaEventHandler(associationProperty = "code", keyName = "orderCode")
    @EndSaga
    public void handle(OrderCreateConfirmedEvent event) {
        log.info("Creation of order {} is confirmed", event.getCode());
    }

    @SagaEventHandler(associationProperty = "code", keyName = "orderCode")
    @EndSaga
    public void on(OrderCreateRollbackedEvent event) {
        log.info("Creation of order {} is rollbacked", event.getCode());
    }
}
