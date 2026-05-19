package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateOrderItemRequest;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderRequest;
import edu.unimagdalena.web.ceptu.dto.response.OrderResponse;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.services.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.beans.factory.annotation.Autowired;
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
    @DisplayName("Debería registrar un pedido y retornar 201 Created")
    void createOrder_WhenValidRequest_ShouldReturn201Created() throws Exception {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(UUID.randomUUID(), 2);
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(itemRequest));

        OrderResponse expectedResponse = new OrderResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ana García",
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("150.00"),
                List.of(),
                Instant.now(),
                Instant.now()
        );

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(expectedResponse);

        // 🏆 CORREGIDO: Ruta sin /v1
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("Debería disparar un rollback y retornar 500 si ocurre una excepción de stock insuficiente en el pago")
    void payOrder_WhenInsufficientStock_ShouldReturn400Or500() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderService.payOrder(orderId)).thenThrow(new RuntimeException("Stock insuficiente para procesar el pago"));

        // 🏆 CORREGIDO: Ruta sin /v1
        mockMvc.perform(put("/api/orders/{id}/pay", orderId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Debería despachar el pedido correctamente y cambiar a estado SHIPPED")
    void shipOrder_WhenValidState_ShouldReturn200Ok() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderResponse expectedResponse = new OrderResponse(
                orderId,
                UUID.randomUUID(),
                "Ana García",
                UUID.randomUUID(),
                OrderStatus.SHIPPED,
                new BigDecimal("150.00"),
                List.of(),
                Instant.now(),
                Instant.now()
        );

        when(orderService.shipOrder(orderId)).thenReturn(expectedResponse);

        // 🏆 CORREGIDO: Ruta sin /v1
        mockMvc.perform(put("/api/orders/{id}/ship", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }
}