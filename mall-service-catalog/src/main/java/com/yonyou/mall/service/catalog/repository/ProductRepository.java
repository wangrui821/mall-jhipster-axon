package com.yonyou.mall.service.catalog.repository;

import com.yonyou.mall.service.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Product entity.
 */
@SuppressWarnings("unused")
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByCode(String code);
}
