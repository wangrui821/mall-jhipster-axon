package com.yonyou.mall.service.catalog.aggregate;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yonyou.mall.service.catalog.event.ProductCreatedEvent;
import com.yonyou.mall.service.catalog.event.ProductDeletedEvent;
import com.yonyou.mall.service.catalog.event.ProductNotEnoughEvent;
import com.yonyou.mall.service.catalog.event.ProductReserveRollbackedEvent;
import com.yonyou.mall.service.catalog.event.ProductReservedEvent;
import com.yonyou.mall.service.catalog.event.ProductUpdatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

/**
 * Created by Administrator on 2017/3/28.
 */
@Aggregate
@NoArgsConstructor
public class ProductAggregate {
    private final Logger log = LoggerFactory.getLogger(ProductAggregate.class);

    @AggregateIdentifier
    @JsonProperty
    private String code;

    @JsonProperty
    private String name;

    @JsonProperty
    private BigDecimal price;

    @JsonProperty
    private BigDecimal inventory;

    @JsonProperty
    private String description;

    public ProductAggregate(String code, String name, BigDecimal price, BigDecimal quantity, String description) {
        apply(new ProductCreatedEvent(code, name, price, quantity, description));
    }

    public void update(String code, String name, BigDecimal price, BigDecimal quantity, String description) {
        apply(new ProductUpdatedEvent(code, name, price, quantity, description));
    }

    public void delete(String code) {
        // Marks this aggregate as deleted, instructing a repository to remove that aggregate at an appropriate time.
        markDeleted();

        apply(new ProductDeletedEvent(code));
    }

    public void reserve(String orderCode, BigDecimal quantity) {
        if (inventory.compareTo(quantity) >= 0) {
            apply(new ProductReservedEvent(orderCode, code, quantity));
        }
        else {
            apply(new ProductNotEnoughEvent(orderCode, code));
        }
    }

    public void rollbackReserve(String orderCode, BigDecimal quantity) {
        apply(new ProductReserveRollbackedEvent(orderCode, code, quantity));
    }

    // 此处也可以使用@EventSourcingHandler
    @EventSourcingHandler
    public void handle(ProductCreatedEvent event) {
        code = event.getCode();
        name = event.getName();
        price = event.getPrice();
        inventory = event.getInventory();
        description = event.getDescription();

        log.info("Product {} will be created", event);
    }

    // 此处也可以使用@EventSourcingHandler
    @EventSourcingHandler
    public void handle(ProductUpdatedEvent event) {
        code = event.getCode();
        name = event.getName();
        price = event.getPrice();
        inventory = event.getInventory();
        description = event.getDescription();

        log.info("Product {} will be updated", event);
    }

    // 此处也可以使用@EventSourcingHandler
    @EventSourcingHandler
    public void handle(ProductDeletedEvent event) {
        code = event.getCode();

        log.info("Product {} will be deleted", event);
    }

    // 此处也可以使用@EventHandler
    @EventSourcingHandler
    public void handle(ProductReservedEvent event) {
        code = event.getProductCode();
        BigDecimal oldInventory = inventory;
        inventory = inventory.subtract(event.getQuantity());

        log.info("Inventory of product {} will be reserved from {} to {}", code, oldInventory, inventory);
    }

    // 此处也可以使用@EventHandler
    @EventSourcingHandler
    public void handle(ProductReserveRollbackedEvent event) {
        inventory = inventory.add(event.getQuantity());

        log.info("Inventory of product {} will be rollbacked to  {}", event.getProductCode(), inventory);
    }
}
