package com.yonyou.mall.service.order.domain;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Created by Administrator on 2017/3/27.
 */
@Data
public class OrderProduct {
    private Long productId;

    private String productCode;

    private String productName;

    private BigDecimal price;

    private BigDecimal quantity;

    private boolean reserved;
}
