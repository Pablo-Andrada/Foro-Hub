package com.alura.forohub.controller;

import com.alura.forohub.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador básico para autenticación y obtención de token JWT.
 * Este ejemplo no usa base de datos para usuarios, solo valida usuario fijo "user" y password "password".
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // DTO interno para login
    public static record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) { }

    // DTO para la respuesta del token
    public static record JwtResponse(String token) { }

    /**
     * Endpoint POST /api/auth/login para obtener token JWT.
     * Aquí validamos usuario "user" y password "password" hardcoded para ejemplo.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        if ("user".equals(loginRequest.username()) && "password".equals(loginRequest.password())) {
            String token = jwtUtil.generarToken(loginRequest.username());
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
