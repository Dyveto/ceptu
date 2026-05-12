package edu.unimagdalena.web.ceptu.dto.response;

import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        String customerFullName,
        UUID addressId,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {}