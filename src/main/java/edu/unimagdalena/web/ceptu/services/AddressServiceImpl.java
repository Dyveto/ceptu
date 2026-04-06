package edu.unimagdalena.web.ceptu.services.impl;

import edu.unimagdalena.web.ceptu.dto.request.CreateAddressRequest;
import edu.unimagdalena.web.ceptu.dto.response.AddressResponse;
import edu.unimagdalena.web.ceptu.entities.Address;
import edu.unimagdalena.web.ceptu.entities.Customer;
import edu.unimagdalena.web.ceptu.mappers.AddressMapper;
import edu.unimagdalena.web.ceptu.repositories.AddressRepository;
import edu.unimagdalena.web.ceptu.repositories.CustomerRepository;
import edu.unimagdalena.web.ceptu.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse createAddress(UUID customerId, CreateAddressRequest request) {
        // Validar que el cliente exista
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + customerId));

        // Mapear los datos de la dirección
        Address address = addressMapper.toEntity(request);
        
        // Asignar el cliente a la dirección
        address.setCustomer(customer);

        // Guardar en la base de datos
        Address savedAddress = addressRepository.save(address);
        
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + id));
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByCustomerId(UUID customerId) {
        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        return addresses.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID id, CreateAddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + id));

        // Actualizamos los campos
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        address.setCountry(request.country());

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + id));
        
        // Evitar borrar si hay órdenes usando esta dirección
        if (address.getOrders() != null && !address.getOrders().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la dirección porque tiene órdenes asociadas.");
        }
        
        addressRepository.deleteById(id);
    }
}