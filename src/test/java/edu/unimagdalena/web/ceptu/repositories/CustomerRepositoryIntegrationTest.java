package edu.unimagdalena.web.ceptu.repositories;

import edu.unimagdalena.web.ceptu.entities.Customer;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .firstName("Laura")
                .lastName("Martínez")
                .email("laura.martinez@unimagdalena.edu.co")
                .phone("3001234567")
                .status(CustomerStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        entityManager.persist(testCustomer);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar un cliente existente por su correo electrónico")
    void findByEmail_ShouldReturnCustomer() {
        Optional<Customer> foundCustomer = customerRepository.findByEmail("laura.martinez@unimagdalena.edu.co");

        assertTrue(foundCustomer.isPresent());
        assertEquals("Laura", foundCustomer.get().getFirstName());
        assertEquals(CustomerStatus.ACTIVE, foundCustomer.get().getStatus());
    }

    @Test
    @DisplayName("Debe retornar Optional vacío si el correo no existe")
    void findByEmail_WhenEmailDoesNotExist_ShouldReturnEmpty() {
        Optional<Customer> foundCustomer = customerRepository.findByEmail("no.existe@correo.com");

        assertTrue(foundCustomer.isEmpty());
    }
}