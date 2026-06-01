package edu.unimagdalena.web.ceptu.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderItemRequest(
        @NotNull UUID productId,
        @NotNull @Min(value = 1, message = "La cantidad debe ser mayor que cero") Integer quantity
) {}