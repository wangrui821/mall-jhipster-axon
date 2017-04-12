package com.yonyou.mall.service.catalog.event;

import lombok.Value;

/**
 * Created by Administrator on 2017/3/31.
 */
@Value
public class ProductNotEnoughEvent {
    private final String orderCode;

    private final String productCode;
}
