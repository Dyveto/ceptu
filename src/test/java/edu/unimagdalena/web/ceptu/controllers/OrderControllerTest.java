package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateOrderItemRequest;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderRequest;
import edu.unimagdalena.web.ceptu.dto.response.OrderResponse;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

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

        OrderResponse expectedResponse = new OrderResponse(
                UUID.randomUUID(),              // UUID id
                UUID.randomUUID(),              // UUID customerId
                "Ana García",                   // String customerFullName
                UUID.randomUUID(),              // UUID addressId
                OrderStatus.CREATED,            // OrderStatus status
                new BigDecimal("150.00"),   // BigDecimal total
                List.of(),                      // List<OrderItemResponse> items
                Instant.now(),                  // Instant createdAt
                Instant.now()                   // Instant updatedAt
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
                orderId,                        // UUID id
                UUID.randomUUID(),              // UUID customerId
                "Ana García",                   // String customerFullName
                UUID.randomUUID(),              // UUID addressId
                OrderStatus.SHIPPED,            // OrderStatus status
                new BigDecimal("150.00"),   // BigDecimal total
                List.of(),                      // List<OrderItemResponse> items
                Instant.now(),                  // Instant createdAt
                Instant.now()                   // Instant updatedAt
        );

        when(orderService.shipOrder(orderId)).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/orders/{id}/ship", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }
}