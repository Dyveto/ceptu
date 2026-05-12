package edu.unimagdalena.web.ceptu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelOrderRequest(
                @NotBlank @Size(max = 255) String notes) {
}