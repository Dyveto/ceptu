package edu.unimagdalena.web.ceptu.dto.response;

import java.util.UUID;

public record BestSellingProductResponse(
        UUID productId,
        String productName,
        String sku,
        Long totalSold
) {}