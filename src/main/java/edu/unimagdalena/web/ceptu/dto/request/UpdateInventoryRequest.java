package edu.unimagdalena.web.ceptu.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateInventoryRequest(
        @NotNull @Min(0) Integer availableStock,
        @NotNull @Min(0) Integer minimumStock
) {}