package com.yonyou.mall.service.catalog.command;

import java.math.BigDecimal;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by Administrator on 2017/3/27.
 */
@Value
public class ReserveProductCommand {
    @TargetAggregateIdentifier
    private final String orderCode;

    private final String productCode;

    private final BigDecimal quantity;
}
