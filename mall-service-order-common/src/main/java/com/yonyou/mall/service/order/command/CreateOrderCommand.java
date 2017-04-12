package com.yonyou.mall.service.order.command;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import com.yonyou.mall.service.order.domain.OrderProduct;
import com.yonyou.mall.service.order.domain.enumeration.OrderState;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * The command to create an order.
 *
 * @author WangRui
 */
@Value
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private final String code;

    private final BigDecimal totalAmount;

    private final ZonedDateTime timeCreated;

    private final OrderState state;

    private final List<OrderProduct> orderProducts;
}
