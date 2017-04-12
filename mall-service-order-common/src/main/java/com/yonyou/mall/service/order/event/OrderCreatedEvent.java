package com.yonyou.mall.service.order.event;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import com.yonyou.mall.service.order.domain.OrderProduct;
import com.yonyou.mall.service.order.domain.enumeration.OrderState;
import lombok.Value;

/**
 * The event which is invoked when an order is created.
 *
 * @author WangRui
 */
@Value
public class OrderCreatedEvent {
    private final String code;

    private final BigDecimal totalAmount;

    private final ZonedDateTime timeCreated;

    private final OrderState state;

    private final List<OrderProduct> orderProducts;
}
