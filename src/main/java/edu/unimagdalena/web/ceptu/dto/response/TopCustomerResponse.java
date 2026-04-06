package edu.unimagdalena.web.ceptu.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TopCustomerResponse(
        UUID customerId,
        String fullName,
        String email,
        BigDecimal totalSpent
) {}