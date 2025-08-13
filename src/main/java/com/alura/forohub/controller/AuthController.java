package com.alura.forohub.controller;

import com.alura.forohub.model.Usuario;
import com.alura.forohub.repository.UsuarioRepository;
import com.alura.forohub.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador de autenticación:
 * - POST /api/auth/login  -> devuelve JWT si credenciales válidas
 * - POST /api/auth/register -> crea usuario (simple) para pruebas
 *
 * Implementación imperativa para evitar problemas de inferencia de tipos
 * al combinar ramas que devuelven ResponseEntity con diferentes genéricos.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // DTOs internos
    public static record LoginRequest(
            @NotBlank(message = "username obligatorio") String username,
            @NotBlank(message = "password obligatorio") String password
    ) {}

    public static record JwtResponse(String token) {}

    public static record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String nombre,
            @NotBlank @Email String email
    ) {}

    /**
     * Login: validación imperativa para devolver siempre ResponseEntity<JwtResponse>.
     * En caso de credenciales inválidas se usa ResponseEntity.<JwtResponse>status(...).build()
     * para que el tipo genérico coincida con el método.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByUsername(request.username());

        if (optionalUsuario.isEmpty()) {
            // Forzamos el tipo genérico a JwtResponse explícitamente
            return ResponseEntity.<JwtResponse>status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = optionalUsuario.get();

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            return ResponseEntity.<JwtResponse>status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generarToken(usuario.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * Registro simple (solo para desarrollo / pruebas).
     * - Valida que username/email no existan.
     * - Hashea la contraseña con BCrypt.
     *
     * Devuelve 201 Created si se crea correctamente.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest req) {
        if (usuarioRepository.existsByUsername(req.username())) {
            return ResponseEntity.badRequest().body("El username ya existe");
        }
        if (usuarioRepository.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        Usuario u = new Usuario();
        u.setUsername(req.username());
        u.setNombre(req.nombre());
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setActivo(true);
        u.setRol("ROLE_USER");
        usuarioRepository.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
