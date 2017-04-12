package com.yonyou.mall.service.order.event;

import lombok.Value;

/**
 * Created by Administrator on 2017/3/27.
 */
@Value
public class OrderCreateRollbackedEvent {
    private final String code;
}
