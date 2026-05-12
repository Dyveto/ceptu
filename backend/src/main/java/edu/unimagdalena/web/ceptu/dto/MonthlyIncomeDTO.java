package edu.unimagdalena.web.ceptu.dto;

import java.math.BigDecimal;

public interface MonthlyIncomeDTO {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotal();
}
