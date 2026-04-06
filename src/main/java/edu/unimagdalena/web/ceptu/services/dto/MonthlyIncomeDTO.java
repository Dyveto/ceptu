package edu.unimagdalena.web.ceptu.services.dto;

import java.math.BigDecimal;

public interface MonthlyIncomeDTO {
    int getYear();
    int getMonth();
    BigDecimal getTotal();
}
