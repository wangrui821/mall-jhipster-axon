package com.yonyou.mall.service.catalog.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by Administrator on 2017/3/29.
 */
@Value
public class DeleteProductCommand {
    @TargetAggregateIdentifier
    private final String code;
}
