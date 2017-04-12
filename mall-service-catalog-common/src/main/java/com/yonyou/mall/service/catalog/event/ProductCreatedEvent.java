package com.yonyou.mall.service.catalog.event;

import java.math.BigDecimal;

import lombok.Value;

/**
 * Created by Administrator on 2017/3/28.
 */
@Value
public class ProductCreatedEvent {
    private final String code;

    private final String name;

    private final BigDecimal price;

    private final BigDecimal inventory;

    private final String description;
}
