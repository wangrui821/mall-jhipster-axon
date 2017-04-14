package com.yonyou.mall.service.catalog.aggregate;

import com.yonyou.mall.service.catalog.command.CreateProductCommand;
import com.yonyou.mall.service.catalog.command.DeleteProductCommand;
import com.yonyou.mall.service.catalog.command.ReserveProductCommand;
import com.yonyou.mall.service.catalog.command.RollbackProductReserveCommand;
import com.yonyou.mall.service.catalog.command.UpdateProductCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * Product command handler, all types of product commands are handled here.
 *
 * @author WangRui
 */
@Component
@SuppressWarnings("UnusedDeclaration")
public class ProductCommandHandler {
    private final Repository<ProductAggregate> repository;

    public ProductCommandHandler(Repository<ProductAggregate> repository) {
        this.repository = repository;
    }

    @CommandHandler
    public void handle(CreateProductCommand command) throws Exception {
        repository.newInstance(
            () -> new ProductAggregate(command.getCode(), command.getName(), command.getPrice(), command.getInventory(),
                command.getDescription()));
    }

    @CommandHandler
    public void handle(UpdateProductCommand command) {
        Aggregate<ProductAggregate> productAggregate = repository.load(command.getCode());
        productAggregate.execute(
            a -> a.update(command.getCode(), command.getName(), command.getPrice(), command.getInventory(),
                command.getDescription()));
    }

    @CommandHandler
    public void handle(DeleteProductCommand command) {
        Aggregate<ProductAggregate> productAggregate = repository.load(command.getCode());
        productAggregate.execute(a -> a.delete(command.getCode()));
    }

    @CommandHandler
    public void handle(ReserveProductCommand command) {
        Aggregate<ProductAggregate> productAggregate = repository.load(command.getProductCode());
        productAggregate.execute(a -> a.reserve(command.getOrderCode(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(RollbackProductReserveCommand command) {
        Aggregate<ProductAggregate> aggregate = repository.load(command.getProductCode());
        aggregate.execute(
            aggregateRoot -> aggregateRoot.rollbackReserve(command.getOrderCode(), command.getQuantity()));
    }
}
