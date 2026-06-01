package edu.unimagdalena.web.ceptu.dto.response;

import java.math.BigDecimal;

public record MonthlyIncomeResponse(
        int year,
        int month,
        BigDecimal total
) {}