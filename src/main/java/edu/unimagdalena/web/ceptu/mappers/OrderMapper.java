package edu.unimagdalena.web.ceptu.mappers;

import edu.unimagdalena.web.ceptu.dto.response.OrderItemResponse;
import edu.unimagdalena.web.ceptu.dto.response.OrderResponse;
import edu.unimagdalena.web.ceptu.entities.Order;
import edu.unimagdalena.web.ceptu.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Mapeo Principal de la Orden
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerFullName", expression = "java(order.getCustomer().getFirstName() + ' ' + order.getCustomer().getLastName())")
    @Mapping(target = "addressId", source = "address.id")
    @Mapping(target = "items", source = "orderItems")
    OrderResponse toResponse(Order order);

    // Sub-mapeo para los Items de la orden
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toItemResponse(OrderItem orderItem);
}