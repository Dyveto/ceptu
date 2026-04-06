package edu.unimagdalena.web.ceptu.services.impl;

import edu.unimagdalena.web.ceptu.dto.request.CreateCustomerRequest;
import edu.unimagdalena.web.ceptu.dto.response.CustomerResponse;
import edu.unimagdalena.web.ceptu.entities.Customer;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.mappers.CustomerMapper;
import edu.unimagdalena.web.ceptu.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void createCustomer_ShouldReturnCustomerResponse() {
        CreateCustomerRequest request = new CreateCustomerRequest("Carlos", "Gómez", "carlos@test.com", "1234567890");
        Customer customerToSave = new Customer();
        Customer savedCustomer = new Customer();
        savedCustomer.setId(UUID.randomUUID());
        
        CustomerResponse expectedResponse = new CustomerResponse(
                savedCustomer.getId(), "Carlos", "Gómez", "carlos@test.com", "1234567890", CustomerStatus.ACTIVE, Instant.now()
        );
        when(customerMapper.toEntity(request)).thenReturn(customerToSave);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

        CustomerResponse result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals("carlos@test.com", result.email());
        verify(customerRepository, times(1)).save(customerToSave);
    }

    @Test
    void deleteCustomer_ShouldUpdateStatusToInactive_InsteadOfHardDelete() {
        UUID id = UUID.randomUUID();
        Customer existingCustomer = new Customer();
        existingCustomer.setId(id);
        existingCustomer.setStatus(CustomerStatus.ACTIVE);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        customerService.deleteCustomer(id);

        assertEquals(CustomerStatus.INACTIVE, existingCustomer.getStatus());
        verify(customerRepository, times(1)).save(existingCustomer);
        verify(customerRepository, never()).deleteById(any());
    }
}