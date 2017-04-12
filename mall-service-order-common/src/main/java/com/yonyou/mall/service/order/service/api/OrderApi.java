package com.yonyou.mall.service.order.service.api;

import java.net.URISyntaxException;
import java.util.List;

import com.yonyou.mall.service.order.service.dto.OrderDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * Created by Administrator on 2017/4/10.
 */
public interface OrderApi {
    ResponseEntity<OrderDTO> createOrder(OrderDTO orderDTO) throws URISyntaxException;

    ResponseEntity<OrderDTO> updateOrder( OrderDTO orderDTO) throws URISyntaxException;

    ResponseEntity<List<OrderDTO>> getAllOrders(Pageable pageable);

    ResponseEntity<OrderDTO> getOrder(Long id);

    ResponseEntity<OrderDTO> getOrderByCode(String code);

    ResponseEntity<Void> deleteOrder(Long id);
}
