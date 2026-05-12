package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CreateAddressRequest;
import edu.unimagdalena.web.ceptu.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse createAddress(UUID customerId, CreateAddressRequest request);
    AddressResponse getAddressById(UUID id);
    List<AddressResponse> getAddressesByCustomerId(UUID customerId);
    AddressResponse updateAddress(UUID id, CreateAddressRequest request);
    void deleteAddress(UUID id);
}