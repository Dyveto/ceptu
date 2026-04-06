package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.response.OrderItemResponse;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    
    // Obtener un ítem específico por su ID
    OrderItemResponse getOrderItemById(UUID id);
    
    // Obtener todos los ítems que pertenecen a una orden específica
    List<OrderItemResponse> getItemsByOrderId(UUID orderId);
}