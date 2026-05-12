package edu.unimagdalena.web.ceptu.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String sku,
        BigDecimal price,
        Boolean active,
        CategoryResponse category,
        InventoryResponse inventory,
        Instant createdAt
) {}