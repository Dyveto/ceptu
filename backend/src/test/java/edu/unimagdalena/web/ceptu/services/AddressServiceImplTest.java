package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CreateAddressRequest;
import edu.unimagdalena.web.ceptu.dto.response.AddressResponse;
import edu.unimagdalena.web.ceptu.entities.Address;
import edu.unimagdalena.web.ceptu.entities.Customer;
import edu.unimagdalena.web.ceptu.entities.Order;
import edu.unimagdalena.web.ceptu.mappers.AddressMapper;
import edu.unimagdalena.web.ceptu.repositories.AddressRepository;
import edu.unimagdalena.web.ceptu.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void createAddress_ShouldReturnAddressResponse() {
        UUID customerId = UUID.randomUUID();
        CreateAddressRequest request = new CreateAddressRequest("Calle 123", "Santa Marta", "Magdalena", "470001", "Colombia");
        
        Customer customer = new Customer();
        customer.setId(customerId);
        
        Address addressToSave = new Address();
        Address savedAddress = new Address();
        
        AddressResponse expectedResponse = new AddressResponse(UUID.randomUUID(), "Calle 123", "Santa Marta", "Magdalena", "470001", "Colombia");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(addressMapper.toEntity(request)).thenReturn(addressToSave);
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(addressMapper.toResponse(savedAddress)).thenReturn(expectedResponse);

        AddressResponse result = addressService.createAddress(customerId, request);

        assertNotNull(result);
        assertEquals("Santa Marta", result.city());
        verify(customerRepository, times(1)).findById(customerId);
        verify(addressRepository, times(1)).save(addressToSave);
    }

    @Test
    void createAddress_WhenCustomerNotFound_ShouldThrowException() {
        UUID customerId = UUID.randomUUID();
        CreateAddressRequest request = new CreateAddressRequest("Calle 123", "Santa Marta", "Magdalena", "470001", "Colombia");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            addressService.createAddress(customerId, request);
        });

        assertEquals("Cliente no encontrado con ID: " + customerId, exception.getMessage());
        verify(addressRepository, never()).save(any(Address.class)); 
    }

    @Test
    void deleteAddress_WhenHasOrders_ShouldThrowException() {
        UUID addressId = UUID.randomUUID();
        Address addressWithOrders = new Address();
        addressWithOrders.setId(addressId);
        
        addressWithOrders.setOrders(List.of(new Order())); 

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(addressWithOrders));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            addressService.deleteAddress(addressId);
        });

        assertEquals("No se puede eliminar la dirección porque tiene órdenes asociadas.", exception.getMessage());
        verify(addressRepository, never()).deleteById(any()); 
    }
}