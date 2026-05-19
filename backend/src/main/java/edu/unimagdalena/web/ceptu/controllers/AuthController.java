package edu.unimagdalena.web.ceptu.controllers;

import edu.unimagdalena.web.ceptu.entities.User;
import edu.unimagdalena.web.ceptu.entities.enums.Role;
import edu.unimagdalena.web.ceptu.dto.request.RegisterRequest;
import edu.unimagdalena.web.ceptu.dto.request.AuthRequest;
import edu.unimagdalena.web.ceptu.dto.response.AuthResponse;
import edu.unimagdalena.web.ceptu.security.jwt.JwtService;
import edu.unimagdalena.web.ceptu.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByEmailIgnoreCase(req.email())) {
            return ResponseEntity.badRequest().build();
        }

        var roles = Optional.ofNullable(req.roles()).filter(r -> !r.isEmpty())
                .orElseGet(() -> Set.of(Role.ROLE_USER));

        String finalPassword = req.password();
        if (finalPassword != null && !finalPassword.isEmpty()) {
            finalPassword = encoder.encode(finalPassword);
        }

        var user = User.builder()
                .email(req.email())
                .password(finalPassword)
                .roles(roles)
                .build();

        users.save(user);

        var principal = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(roles.stream().map(Enum::name).toArray(String[]::new))
                .build();

        var token = jwt.generateToken(principal, Map.of("roles", roles));
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwt.getExpirationSeconds()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var user = users.findByEmailIgnoreCase(req.email()).orElseThrow();
        var principal = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
        var token = jwt.generateToken(principal, Map.of("roles", user.getRoles()));
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwt.getExpirationSeconds()));
    }
}