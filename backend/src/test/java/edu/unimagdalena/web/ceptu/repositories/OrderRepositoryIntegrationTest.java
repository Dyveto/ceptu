package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.dto.TopCustomerDTO;
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
        // 1. Creamos el cliente
        testCustomer = Customer.builder()
                .firstName("Ana")
                .lastName("García")
                .email("ana.garcia@test.com")
                .status(CustomerStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
        entityManager.persist(testCustomer);

        // 2. Creamos la dirección (¡Lo que faltaba!)
        testAddress = Address.builder()
                .customer(testCustomer)
                .street("Carrera 15 # 22-10")
                .city("Santa Marta")
                .country("Colombia")
                .build();
        entityManager.persist(testAddress);

        // 3. Creamos los pedidos y les asignamos la dirección
        Order order1 = Order.builder()
                .customer(testCustomer)
                .address(testAddress) // Asignamos la dirección
                .total(new BigDecimal("150000.00"))
                .status(OrderStatus.PAID)
                .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .build();

        Order order2 = Order.builder()
                .customer(testCustomer)
                .address(testAddress) // Asignamos la dirección
                .total(new BigDecimal("50000.00"))
                .status(OrderStatus.DELIVERED)
                .createdAt(Instant.now())
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar pedidos aplicando filtros combinados (estado y rangos de total)")
    void findByFilters_ShouldReturnMatchingOrders() {
        PageRequest page = PageRequest.of(0, 10);

        // Filtramos por estado DELIVERED y un total máximo de 60000
        Page<Order> result = orderRepository.findByFilters(
                null, OrderStatus.DELIVERED, null, null, null, new BigDecimal("60000.00"), page
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(new BigDecimal("50000.00"), result.getContent().get(0).getTotal());
    }

    @Test
    @DisplayName("Debe retornar el top de clientes excluyendo órdenes canceladas")
    void findTopCustomersByBilling_ShouldReturnTopCustomers() {
        PageRequest limit = PageRequest.of(0, 5);

        List<TopCustomerDTO> topCustomers = orderRepository.findTopCustomersByBilling(limit);

        assertFalse(topCustomers.isEmpty());
        // El total gastado por Ana debería ser 150k + 50k = 200k
        assertEquals(new BigDecimal("200000.00"), topCustomers.get(0).getTotalSpent());
        assertEquals("Ana", topCustomers.get(0).getCustomer().getFirstName());
    }
}