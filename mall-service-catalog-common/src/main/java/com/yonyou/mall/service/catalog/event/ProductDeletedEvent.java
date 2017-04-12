package com.yonyou.mall.service.catalog.event;

import lombok.Value;

/**
 * Created by Administrator on 2017/3/28.
 */
@Value
public class ProductDeletedEvent {
    private final String code;
}
