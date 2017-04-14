package com.yonyou.mall.service.catalog.handler;

import com.yonyou.mall.service.catalog.domain.Product;
import com.yonyou.mall.service.catalog.event.ProductCreatedEvent;
import com.yonyou.mall.service.catalog.event.ProductDeletedEvent;
import com.yonyou.mall.service.catalog.event.ProductReserveRollbackedEvent;
import com.yonyou.mall.service.catalog.event.ProductReservedEvent;
import com.yonyou.mall.service.catalog.event.ProductUpdatedEvent;
import com.yonyou.mall.service.catalog.repository.ProductRepository;
import com.yonyou.mall.service.catalog.repository.search.ProductSearchRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Product event handler class. Product events are handled here. For instance, using JPA Repository to save entity.
 *
 * @author WangRui
 */
@Component
@SuppressWarnings("UnusedDeclaration")
public class ProductEventHandler {
    private final Logger log = LoggerFactory.getLogger(ProductEventHandler.class);

    private final ProductRepository productRepository;

    private final ProductSearchRepository productSearchRepository;

    public ProductEventHandler(ProductRepository productRepository, ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
    }

    @EventHandler
    public void handle(ProductCreatedEvent event) {
        Product product = new Product();
        product.setCode(event.getCode());
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setInventory(event.getInventory());
        product.setDescription(event.getDescription());
        productRepository.save(product);
        productSearchRepository.save(product);

        log.info("Product {} is created", product);
    }

    @EventHandler
    public void handle(ProductUpdatedEvent event) {
        Product product = productRepository.findByCode(event.getCode());
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setInventory(event.getInventory());
        product.setDescription(event.getDescription());
        productRepository.save(product);
        productSearchRepository.save(product);

        log.info("Product {} is updated", product);
    }

    @EventHandler
    public void handle(ProductDeletedEvent event) {
        Product product = productRepository.findByCode(event.getCode());
        if (product == null) {
            log.error("Cannot find product with code {}", event.getCode());
            return;
        }

        productRepository.delete(product);
        productSearchRepository.delete(product);

        log.info("Product {} is deleted", product);
    }

    @EventHandler
    public void handle(ProductReservedEvent event) {
        Product product = productRepository.findByCode(event.getProductCode());
        if (product == null) {
            log.error("Cannot find product with code {}", event.getProductCode());
            return;
        }

        product.setInventory(product.getInventory().subtract(event.getQuantity()));
        productRepository.save(product);
        productSearchRepository.save(product);
    }

    @EventHandler
    public void handle(ProductReserveRollbackedEvent event) {
        Product product = productRepository.findByCode(event.getProductCode());
        if (product == null) {
            log.error("Cannot find product with code {}", event.getProductCode());
            return;
        }

        product.setInventory(product.getInventory().add(event.getQuantity()));
        productRepository.save(product);
        productSearchRepository.save(product);
    }
}
