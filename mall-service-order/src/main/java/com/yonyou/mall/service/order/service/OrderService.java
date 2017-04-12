package com.yonyou.mall.service.order.service;

import com.yonyou.mall.service.order.service.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Order.
 */
public interface OrderService {

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save
     * @return the persisted entity
     */
    OrderDTO save(OrderDTO orderDTO);

    /**
     *  Get all the orders.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OrderDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" order.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    OrderDTO findOne(Long id);

    /**
     *  Get the "code" order.
     *
     *  @param code the code of the entity
     *  @return the entity
     */
    OrderDTO findByCode(String code);

    /**
     *  Delete the "id" order.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the order corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OrderDTO> search(String query, Pageable pageable);
}
