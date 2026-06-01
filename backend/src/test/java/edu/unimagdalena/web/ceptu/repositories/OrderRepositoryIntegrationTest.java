package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.dto.response.TopCustomerResponse;
import edu.unimagdalena.web.ceptu.entities.Address;
import edu.unimagdalena.web.ceptu.entities.Customer;
import edu.unimagdalena.web.ceptu.entities.Order;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer testCustomer;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .firstName("Ana")
                .lastName("García")
                .email("ana.garcia@test.com")
                .status(CustomerStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
        entityManager.persist(testCustomer);

        testAddress = Address.builder()
                .customer(testCustomer)
                .street("Carrera 15 # 22-10")
                .city("Santa Marta")
                .country("Colombia")
                .build();
        entityManager.persist(testAddress);

        Order order1 = Order.builder()
                .customer(testCustomer)
                .address(testAddress)
                .total(new BigDecimal("150000.00"))
                .status(OrderStatus.PAID)
                .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .build();

        Order order2 = Order.builder()
                .customer(testCustomer)
                .address(testAddress)
                .total(new BigDecimal("50000.00"))
                .status(OrderStatus.DELIVERED)
                .createdAt(Instant.now())
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Debe encontrar pedidos aplicando filtros combinados (estado y rangos de total)")
    void findByFilters_ShouldReturnMatchingOrders() {
        PageRequest page = PageRequest.of(0, 10);

        Page<Order> result = orderRepository.findByFilters(
                null, OrderStatus.DELIVERED, null, null, null, new BigDecimal("60000.00"), page
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(0, new BigDecimal("50000.00").compareTo(result.getContent().get(0).getTotal()));
    }

    @Test
    @DisplayName("Debe retornar el top de clientes excluyendo órdenes canceladas")
    void findTopCustomersByBilling_ShouldReturnTopCustomers() {
        PageRequest limit = PageRequest.of(0, 5);

        List<TopCustomerResponse> topCustomers = orderRepository.findTopCustomersByBilling(limit);

        assertFalse(topCustomers.isEmpty());
        
        assertEquals(0, new BigDecimal("200000.00").compareTo(topCustomers.get(0).totalSpent()));
        assertEquals("Ana García", topCustomers.get(0).fullName());
    }
}