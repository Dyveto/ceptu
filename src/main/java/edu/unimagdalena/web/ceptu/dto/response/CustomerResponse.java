package edu.unimagdalena.web.ceptu.dto.response;

import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        CustomerStatus status,
        Instant createdAt
) {}