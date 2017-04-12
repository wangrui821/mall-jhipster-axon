package com.yonyou.mall.service.catalog.event;

import java.math.BigDecimal;

import lombok.Value;

/**
 * Created by Administrator on 2017/3/28.
 */
@Value
public class ProductReserveRollbackedEvent {
    private final String orderCode;

    private final String productCode;

    private final BigDecimal quantity;
}
