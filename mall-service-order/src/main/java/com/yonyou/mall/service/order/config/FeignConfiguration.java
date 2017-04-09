package com.yonyou.mall.service.order.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.yonyou.mall.service.order")
public class FeignConfiguration {

}
