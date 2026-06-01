package edu.unimagdalena.web.ceptu.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
        @NotBlank String street,
        @NotBlank String city,
        String state,
        String zipCode,
        @NotBlank String country
) {}