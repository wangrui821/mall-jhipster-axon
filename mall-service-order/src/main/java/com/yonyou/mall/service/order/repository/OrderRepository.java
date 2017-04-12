package com.yonyou.mall.service.order.repository;

import com.yonyou.mall.service.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByCode(String code);
}
