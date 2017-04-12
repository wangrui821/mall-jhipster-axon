package com.yonyou.mall.service.catalog.event;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class ProductUpdatedEvent {
    private final String code;

    private final String name;

    private final BigDecimal price;

    private final BigDecimal inventory;

    private final String description;
}
