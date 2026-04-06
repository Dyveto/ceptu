package edu.unimagdalena.web.ceptu.services.impl;

import edu.unimagdalena.web.ceptu.dto.response.OrderItemResponse;
import edu.unimagdalena.web.ceptu.entities.OrderItem;
import edu.unimagdalena.web.ceptu.entities.Product;
import edu.unimagdalena.web.ceptu.mappers.OrderItemMapper;
import edu.unimagdalena.web.ceptu.repositories.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    void getOrderItemById_ShouldReturnOrderItemResponse() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Monitor 4K");
        
        OrderItem orderItem = new OrderItem();
        orderItem.setId(itemId);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setSubtotal(new BigDecimal("600.00"));
        
        OrderItemResponse expectedResponse = new OrderItemResponse(
                itemId, product.getId(), "Monitor 4K", 2, new BigDecimal("300.00"), new BigDecimal("600.00")
        );

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toResponse(orderItem)).thenReturn(expectedResponse);

        // Act
        OrderItemResponse result = orderItemService.getOrderItemById(itemId);

        // Assert
        assertNotNull(result);
        assertEquals("Monitor 4K", result.productName());
        assertEquals(2, result.quantity());
        verify(orderItemRepository, times(1)).findById(itemId);
    }

    @Test
    void getOrderItemById_WhenNotFound_ShouldThrowException() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderItemService.getOrderItemById(itemId);
        });

        assertEquals("Ítem de orden no encontrado con ID: " + itemId, exception.getMessage());
        verify(orderItemMapper, never()).toResponse(any());
    }

    @Test
    void getItemsByOrderId_ShouldReturnListOfResponses() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        
        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        List<OrderItem> items = List.of(item1, item2);
        
        OrderItemResponse response1 = new OrderItemResponse(UUID.randomUUID(), UUID.randomUUID(), "Prod 1", 1, BigDecimal.TEN, BigDecimal.TEN);
        OrderItemResponse response2 = new OrderItemResponse(UUID.randomUUID(), UUID.randomUUID(), "Prod 2", 2, BigDecimal.TEN, new BigDecimal("20.00"));

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(items);
        
        // Configuramos el mapper para que devuelva respuestas distintas según el ítem (simulación básica)
        when(orderItemMapper.toResponse(item1)).thenReturn(response1);
        when(orderItemMapper.toResponse(item2)).thenReturn(response2);

        // Act
        List<OrderItemResponse> results = orderItemService.getItemsByOrderId(orderId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(orderItemRepository, times(1)).findByOrderId(orderId);
        verify(orderItemMapper, times(2)).toResponse(any(OrderItem.class));
    }
}