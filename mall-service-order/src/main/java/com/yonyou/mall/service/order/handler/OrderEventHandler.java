package com.yonyou.mall.service.order.handler;

import com.yonyou.mall.service.order.domain.Order;
import com.yonyou.mall.service.order.domain.OrderItem;
import com.yonyou.mall.service.order.domain.enumeration.OrderState;
import com.yonyou.mall.service.order.event.OrderCanceledEvent;
import com.yonyou.mall.service.order.event.OrderCreateConfirmedEvent;
import com.yonyou.mall.service.order.event.OrderCreateRollbackedEvent;
import com.yonyou.mall.service.order.event.OrderCreatedEvent;
import com.yonyou.mall.service.order.repository.OrderItemRepository;
import com.yonyou.mall.service.order.repository.OrderRepository;
import com.yonyou.mall.service.order.repository.search.OrderSearchRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Order event handler class. Events are handled here. For instance, using JPA Repository to save entity.
 *
 * @author WangRui
 */
@Component
public class OrderEventHandler {
    private final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

    private final OrderRepository orderRepository;

    private final OrderSearchRepository orderSearchRepository;

    private final OrderItemRepository orderItemRepository;

    public OrderEventHandler(OrderRepository orderRepository, OrderSearchRepository orderSearchRepository,
        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderSearchRepository = orderSearchRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @EventHandler
    public void handle(OrderCreatedEvent event) {
        // Save order entity
        Order order = new Order();
        order.setCode(event.getCode());
        order.setTotalAmount(event.getTotalAmount());
        order.setTimeCreated(event.getTimeCreated());
        order.setState(event.getState());
        order = orderRepository.save(order);
        orderSearchRepository.save(order);

        // Save order item entities
        final Order finalOrder = order;
        event.getOrderProducts().forEach(orgProduct -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(finalOrder);
            orderItem.setProductId(orgProduct.getProductId());
            orderItem.setProductCode(orgProduct.getProductCode());
            orderItem.setProductName(orgProduct.getProductName());
            orderItem.setPrice(orgProduct.getPrice());
            orderItem.setQuantity(orgProduct.getQuantity());
            orderItemRepository.save(orderItem);
        });
    }

    @EventHandler
    public void handle(OrderCreateConfirmedEvent event) {
        Order order = orderRepository.findByCode(event.getCode());
        if (order == null) {
            log.error("Cannot find order with code {}", event.getCode());
            return;
        }
        order.setState(OrderState.PENDING_PAY);
        orderRepository.save(order);
        orderSearchRepository.save(order);
    }

    @EventHandler
    public void handle(OrderCreateRollbackedEvent event) {
        Order order = orderRepository.findByCode(event.getCode());
        if (order == null) {
            log.error("Cannot find order with code {}", event.getCode());
            return;
        }
        orderItemRepository.deleteInBatch(order.getOrderItems());
        orderRepository.delete(order);
        orderSearchRepository.delete(order);
    }

    @EventHandler
    public void handle(OrderCanceledEvent event) {
        Order order = orderRepository.findByCode(event.getCode());
        if (order == null) {
            log.error("Cannot find order with code {}", event.getCode());
            return;
        }
        order.setState(OrderState.CANCELED);
        orderRepository.save(order);
        orderSearchRepository.save(order);
    }
}
