package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateOrderItemRequest;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderRequest;
import edu.unimagdalena.web.ceptu.dto.response.OrderResponse;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import edu.unimagdalena.web.ceptu.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrder_WhenValidRequest_ShouldReturn201Created() throws Exception {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(UUID.randomUUID(), 2);
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(itemRequest));

        // Aquí enviamos los 9 parámetros exactamente en el orden que los declaraste en tu DTO
        OrderResponse expectedResponse = new OrderResponse(
                UUID.randomUUID(),          // 1. UUID id
                UUID.randomUUID(),          // 2. UUID customerId
                "Ana García",               // 3. String customerFullName
                UUID.randomUUID(),          // 4. UUID addressId
                OrderStatus.CREATED,        // 5. OrderStatus status
                new BigDecimal("150.00"),   // 6. BigDecimal total
                List.of(),                  // 7. List<OrderItemResponse> items
                Instant.now(),              // 8. Instant createdAt
                Instant.now()               // 9. Instant updatedAt
        );

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void payOrder_WhenInsufficientStock_ShouldReturn400Or500() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderService.payOrder(orderId)).thenThrow(new RuntimeException("Stock insuficiente para procesar el pago"));

        mockMvc.perform(put("/api/v1/orders/{id}/pay", orderId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shipOrder_WhenValidState_ShouldReturn200Ok() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Mismo orden exacto que arriba
        OrderResponse expectedResponse = new OrderResponse(
                orderId,                    // 1. UUID id
                UUID.randomUUID(),          // 2. UUID customerId
                "Ana García",               // 3. String customerFullName
                UUID.randomUUID(),          // 4. UUID addressId
                OrderStatus.SHIPPED,        // 5. OrderStatus status
                new BigDecimal("150.00"),   // 6. BigDecimal total
                List.of(),                  // 7. List<OrderItemResponse> items
                Instant.now(),              // 8. Instant createdAt
                Instant.now()               // 9. Instant updatedAt
        );

        when(orderService.shipOrder(orderId)).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/orders/{id}/ship", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }
}