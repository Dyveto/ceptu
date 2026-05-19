package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.dto.request.CreateAddressRequest;
import edu.unimagdalena.web.ceptu.dto.response.AddressResponse;
import edu.unimagdalena.web.ceptu.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // POST /api/v1/customers/{customerId}/addresses
    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable UUID customerId,
                                                         @Valid @RequestBody CreateAddressRequest request) {
        AddressResponse response = addressService.createAddress(customerId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/v1/customers/{customerId}/addresses
    @GetMapping("/customers/{customerId}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddressesByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(addressService.getAddressesByCustomerId(customerId));
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable UUID id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable UUID id,
                                                         @Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}