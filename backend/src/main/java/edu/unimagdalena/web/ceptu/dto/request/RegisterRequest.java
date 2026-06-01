package edu.unimagdalena.web.ceptu.dto.request;

import edu.unimagdalena.web.ceptu.entities.enums.Role;
import jakarta.validation.constraints.*;

import java.util.Set;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotNull String password,
        Set<Role> roles
) {}