package com.yonyou.mall.service.catalog.command;

import java.math.BigDecimal;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class UpdateProductCommand {
    @TargetAggregateIdentifier
    private final String code;

    private final String name;

    private final BigDecimal price;

    private final BigDecimal inventory;

    private final String description;
}
