package edu.unimagdalena.web.ceptu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotNull UUID categoryId,
        @NotBlank String name,
        @NotBlank String sku,
        @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero") BigDecimal price,
        @NotNull @Min(0) Integer initialStock,
        @NotNull @Min(0) Integer minimumStock
) {}