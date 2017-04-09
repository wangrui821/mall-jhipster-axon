package com.yonyou.mall.service.catalog.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.yonyou.mall.service.catalog")
public class FeignConfiguration {

}
