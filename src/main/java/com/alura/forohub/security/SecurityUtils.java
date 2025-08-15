package com.alura.forohub.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility estático para interactuar con Spring Security desde el código
 * de servicios (sin acoplar demasiado la lógica).
 *
 * Provee:
 *  - obtener el username actual (si hay autenticación)
 *  - comprobar si el usuario tiene un role/authority concreto
 *
 * Uso:
 *  Optional<String> maybeUser = SecurityUtils.getCurrentUsername();
 *  boolean isAdmin = SecurityUtils.hasRole("ROLE_ADMIN");
 */
public final class SecurityUtils {

    private SecurityUtils() { /* util class */ }

    /**
     * Devuelve la Authentication actual (envuelta en Optional si existe).
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Devuelve el username del principal (si existe).
     */
    public static Optional<String> getCurrentUsername() {
        return getAuthentication()
                .map(Authentication::getName);
    }

    /**
     * Comprueba si el usuario actual tiene la autoridad dada.
     * - roleOrAuthority debe ser el string tal cual aparece en GrantedAuthority,
     *   ej: "ROLE_ADMIN" o "ROLE_USER".
     */
    public static boolean hasRole(String roleOrAuthority) {
        return getAuthentication()
                .map(auth -> auth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(a -> a.equals(roleOrAuthority)))
                .orElse(false);
    }
}
