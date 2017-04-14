package com.yonyou.mall.service.order.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yonyou.mall.service.order.command.CreateOrderCommand;
import com.yonyou.mall.service.order.domain.Order;
import com.yonyou.mall.service.order.domain.OrderProduct;
import com.yonyou.mall.service.order.repository.OrderRepository;
import com.yonyou.mall.service.order.repository.search.OrderSearchRepository;
import com.yonyou.mall.service.order.service.OrderService;
import com.yonyou.mall.service.order.service.dto.OrderDTO;
import com.yonyou.mall.service.order.service.mapper.OrderMapper;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Order.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderSearchRepository orderSearchRepository;

    @Autowired
    private CommandGateway commandGateway;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderSearchRepository orderSearchRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderSearchRepository = orderSearchRepository;
    }

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.orderDTOToOrder(orderDTO);
//        order = orderRepository.save(order);
//        orderSearchRepository.save(order);

        // Send command to command bus
        if (null == order.getId()) {
            // create
            orderDTO.setTimeCreated(ZonedDateTime.now());

            List<OrderProduct> orderProducts = new ArrayList<>();
            orderDTO.getOrderItems().forEach(orderItem -> {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProductId(orderItem.getProductId());
                orderProduct.setProductCode(orderItem.getProductCode());
                orderProduct.setProductName(orderItem.getProductName());
                orderProduct.setPrice(orderItem.getPrice());
                orderProduct.setQuantity(orderItem.getQuantity());
                orderProduct.setReserved(false);
                orderProducts.add(orderProduct);
            });
            CreateOrderCommand command = new CreateOrderCommand(orderDTO.getCode(), orderDTO.getTotalAmount(),
                orderDTO.getTimeCreated(), orderDTO.getState(), orderProducts);
            commandGateway.sendAndWait(command);
        }

        OrderDTO result = orderMapper.orderToOrderDTO(order);
        return result;
    }

    /**
     *  Get all the orders.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        Page<Order> result = orderRepository.findAll(pageable);
        return result.map(order -> orderMapper.orderToOrderDTO(order));
    }

    /**
     *  Get one order by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public OrderDTO findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        Order order = orderRepository.findOne(id);
        OrderDTO orderDTO = orderMapper.orderToOrderDTO(order);
        return orderDTO;
    }

    @Override
    public OrderDTO findByCode(String code) {
        log.debug("Request to get Order : {}", code);
        Order order = orderRepository.findByCode(code);
        OrderDTO orderDTO = orderMapper.orderToOrderDTO(order);
        return orderDTO;
    }

    /**
     *  Delete the  order by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.delete(id);
        orderSearchRepository.delete(id);
    }

    /**
     * Search for the order corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Orders for query {}", query);
        Page<Order> result = orderSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(order -> orderMapper.orderToOrderDTO(order));
    }
}
