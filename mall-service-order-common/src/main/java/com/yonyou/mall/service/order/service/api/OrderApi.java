package com.yonyou.mall.service.order.service.api;

import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;

import com.yonyou.mall.service.order.service.dto.OrderDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Administrator on 2017/4/10.
 */
public interface OrderApi {
    ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException;

    ResponseEntity<OrderDTO> updateOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException;

    ResponseEntity<List<OrderDTO>> getAllOrders(@ApiParam Pageable pageable);

    ResponseEntity<OrderDTO> getOrder(@PathVariable Long id);

    ResponseEntity<Void> deleteOrder(@PathVariable Long id);
}
