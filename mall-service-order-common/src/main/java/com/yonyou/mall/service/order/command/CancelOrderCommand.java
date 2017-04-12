package com.yonyou.mall.service.order.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * The command to cancel an order.
 *
 * @author WangRui
 */
@Value
public class CancelOrderCommand {
    @TargetAggregateIdentifier
    private final String code;
}
