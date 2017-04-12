package com.yonyou.mall.service.order.event;

import lombok.Value;

/**
 * The event which is invoked when an order is canceled.
 *
 * @author WangRui
 */
@Value
public class OrderCanceledEvent {
    private final String code;
}
