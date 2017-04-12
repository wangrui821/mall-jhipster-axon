package com.yonyou.mall.service.order.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import com.yonyou.mall.service.order.service.OrderService;
import com.yonyou.mall.service.order.service.api.OrderApi;
import com.yonyou.mall.service.order.service.dto.OrderDTO;
import com.yonyou.mall.service.order.web.rest.util.HeaderUtil;
import com.yonyou.mall.service.order.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Order.
 */
@RestController
@RequestMapping("/api")
public class OrderResource implements OrderApi {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "order";

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST  /orders : Create a new order.
     *
     * @param orderDTO the orderDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orderDTO, or with status 400 (Bad
     * Request) if the order has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Override
    @PostMapping("/orders")
    @Timed
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException {
        log.debug("REST request to save Order : {}", orderDTO);
        if (orderDTO.getId() != null) {
            return ResponseEntity.badRequest()
                                 .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists",
                                     "A new order cannot already have an ID"))
                                 .body(null);
        }
        String code = UUID.randomUUID().toString();
        orderDTO.setCode(code);
        OrderDTO result = orderService.save(orderDTO);
        return ResponseEntity.created(new URI("/api/orders?code=" + result.getCode()))
                             .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getCode()))
                             .body(result);
    }

    /**
     * PUT  /orders : Updates an existing order.
     *
     * @param orderDTO the orderDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orderDTO,
     * or with status 400 (Bad Request) if the orderDTO is not valid,
     * or with status 500 (Internal Server Error) if the orderDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Override
    @PutMapping("/orders")
    @Timed
    public ResponseEntity<OrderDTO> updateOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException {
        log.debug("REST request to update Order : {}", orderDTO);
        if (orderDTO.getId() == null) {
            return createOrder(orderDTO);
        }
        OrderDTO result = orderService.save(orderDTO);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orderDTO.getId().toString()))
                             .body(result);
    }

    /**
     * GET  /orders : get all the orders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
     */
    @Override
    @GetMapping("/orders")
    @Timed
    public ResponseEntity<List<OrderDTO>> getAllOrders(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Orders");
        Page<OrderDTO> page = orderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /orders/:id : get the "id" order.
     *
     * @param id the id of the orderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
     */
    @Override
    @GetMapping("/orders/{id}")
    @Timed
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        OrderDTO orderDTO = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
    }

    /**
     * GET  /orders?code=:code : get the "code" order.
     *
     * @param code the code of the orderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
     */
    @Override
    @GetMapping("/orders?code={code}")
    @Timed
    public ResponseEntity<OrderDTO> getOrderByCode(@PathVariable String code) {
        log.debug("REST request to get Order : {}", code);
        OrderDTO orderDTO = orderService.findByCode(code);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
    }

    /**
     * DELETE  /orders/:id : delete the "id" order.
     *
     * @param id the id of the orderDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Override
    @DeleteMapping("/orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/orders?query=:query : search for the order corresponding
     * to the query.
     *
     * @param query    the query of the order search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/orders")
    @Timed
    public ResponseEntity<List<OrderDTO>> searchOrders(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Orders for query {}", query);
        Page<OrderDTO> page = orderService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
