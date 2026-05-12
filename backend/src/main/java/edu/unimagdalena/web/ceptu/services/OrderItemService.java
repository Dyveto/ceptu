package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.response.OrderItemResponse;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    
    OrderItemResponse getOrderItemById(UUID id);
    
    List<OrderItemResponse> getItemsByOrderId(UUID orderId);
}