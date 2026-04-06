package edu.unimagdalena.web.ceptu.dto;

import java.math.BigDecimal;

public interface MonthlyIncomeDTO {
    int getYear();
    int getMonth();
    BigDecimal getTotal();
}
