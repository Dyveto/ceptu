package edu.unimagdalena.web.ceptu.dto.response;

import java.util.UUID;

public record LowStockProductResponse(
        UUID productId,
        String productName,
        String sku,
        Integer availableStock,
        Integer minimumStock
) {}