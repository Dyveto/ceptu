package edu.unimagdalena.web.ceptu.dto.response;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {}