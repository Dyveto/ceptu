package edu.unimagdalena.web.ceptu.dto.response;

import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        Integer availableStock,
        Integer minimumStock,
        Instant updatedAt
) {}