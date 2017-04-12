package com.yonyou.mall.service.order.aggregate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yonyou.mall.service.order.domain.OrderProduct;
import com.yonyou.mall.service.order.domain.enumeration.OrderState;
import com.yonyou.mall.service.order.event.OrderCanceledEvent;
import com.yonyou.mall.service.order.event.OrderCreateConfirmedEvent;
import com.yonyou.mall.service.order.event.OrderCreateRollbackedEvent;
import com.yonyou.mall.service.order.event.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateMember;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

/**
 * Order aggregate class. Various types of events are applied here, event sourcing are also handled here.
 *
 * @author WangRui
 */
@Aggregate
@NoArgsConstructor
public class OrderAggregate {
    @AggregateIdentifier
    @JsonProperty
    private String code;

    @JsonProperty
    private BigDecimal totalAmount;

    @JsonProperty
    private ZonedDateTime timeCreated;

    @JsonProperty
    private OrderState state;

    @AggregateMember
    protected List<OrderProduct> orderItems = new ArrayList<>();

    public OrderAggregate(String code, BigDecimal totalAmount, ZonedDateTime timeCreated, OrderState state,
        List<OrderProduct> orderItems) {
        apply(new OrderCreatedEvent(code, totalAmount, timeCreated, state, orderItems));
    }

    public void rollbackCreate() {
        // Marks this aggregate as deleted, instructing a repository to remove that aggregate at an appropriate time.
        markDeleted();

        apply(new OrderCreateRollbackedEvent(code));
    }

    public void confirmCreate() {
        apply(new OrderCreateConfirmedEvent(code));
    }

    public void cancel() {
        apply(new OrderCanceledEvent(code));
    }

    // 此处也可以使用@EventHandler
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        code = event.getCode();
        totalAmount = event.getTotalAmount();
        timeCreated = event.getTimeCreated();
        state = event.getState();
        orderItems = event.getOrderProducts();
    }
}
