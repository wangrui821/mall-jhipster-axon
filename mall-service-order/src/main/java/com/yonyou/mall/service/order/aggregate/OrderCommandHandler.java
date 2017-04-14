package com.yonyou.mall.service.order.aggregate;

import com.yonyou.mall.service.order.command.CancelOrderCommand;
import com.yonyou.mall.service.order.command.ConfirmOrderCreateCommand;
import com.yonyou.mall.service.order.command.CreateOrderCommand;
import com.yonyou.mall.service.order.command.RollbackOrderCreateCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * Order command handler, different types of order command are handled here.
 *
 * @author WangRui
 */
@Component
@SuppressWarnings("UnusedDeclaration")
public class OrderCommandHandler {
    private final Repository<OrderAggregate> repository;

    public OrderCommandHandler(Repository<OrderAggregate> repository) {
        this.repository = repository;
    }

    @CommandHandler
    public void handle(CreateOrderCommand command) throws Exception {
        repository.newInstance(
            () -> new OrderAggregate(command.getCode(), command.getTotalAmount(), command.getTimeCreated(),
                command.getState(), command.getOrderProducts()));
    }

    @CommandHandler
    public void handle(RollbackOrderCreateCommand command) {
        Aggregate<OrderAggregate> orderAggregate = repository.load(command.getCode());
        orderAggregate.execute(c -> c.rollbackCreate());
    }

    @CommandHandler
    public void handle(ConfirmOrderCreateCommand command) {
        Aggregate<OrderAggregate> aggregate = repository.load(command.getCode());
        aggregate.execute(c -> c.confirmCreate());
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        Aggregate<OrderAggregate> orderAggregate = repository.load(command.getCode());
        orderAggregate.execute(c -> c.cancel());
    }
}
