package edu.unimagdalena.web.ceptu.services.dto;

import edu.unimagdalena.web.ceptu.entities.Customer;

import java.math.BigDecimal;

public interface TopCustomerDTO {
    Customer getCustomer();
    BigDecimal getTotalSpent();
}
