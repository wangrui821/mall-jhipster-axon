package com.yonyou.mall.service.order.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by Administrator on 2017/3/27.
 */
@Value
public class RollbackOrderCreateCommand {
    @TargetAggregateIdentifier
    private final String code;
}
