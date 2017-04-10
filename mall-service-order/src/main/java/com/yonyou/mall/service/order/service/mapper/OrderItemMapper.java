package com.yonyou.mall.service.order.service.mapper;

import com.yonyou.mall.service.order.domain.*;
import com.yonyou.mall.service.order.service.dto.OrderItemDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity OrderItem and its DTO OrderItemDTO.
 */
@Mapper(componentModel = "spring", uses = {OrderMapper.class, })
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.code", target = "orderCode")
    OrderItemDTO orderItemToOrderItemDTO(OrderItem orderItem);

    List<OrderItemDTO> orderItemsToOrderItemDTOs(List<OrderItem> orderItems);

    @Mapping(source = "orderId", target = "order")
    OrderItem orderItemDTOToOrderItem(OrderItemDTO orderItemDTO);

    List<OrderItem> orderItemDTOsToOrderItems(List<OrderItemDTO> orderItemDTOs);
    /**
     * generating the fromId for all mappers if the databaseType is sql, as the class has relationship to it might need it, instead of
     * creating a new attribute to know if the entity has any relationship from some other entity
     *
     * @param id id of the entity
     * @return the entity instance
     */
     
    default OrderItem orderItemFromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        return orderItem;
    }
    

}
