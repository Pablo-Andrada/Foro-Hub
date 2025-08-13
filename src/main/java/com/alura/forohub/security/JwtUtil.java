package com.alura.forohub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Clase utilitaria para gestionar JWT (generar, validar y extraer datos).
 * Contiene métodos compatibles con el código actual y alias para evitar errores.
 */
@Component
public class JwtUtil {

    // Clave secreta para firmar el JWT (en un proyecto real, va en application.properties o variables de entorno)
    private final String SECRET_KEY = "clave_secreta_super_segura";

    // Duración del token (10 horas)
    private final long JWT_EXPIRATION = 1000 * 60 * 60 * 10;

    /**
     * Extrae el nombre de usuario (username) desde el token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración desde el token JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un "claim" específico usando una función.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims (datos) del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token expiró.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT con un usuario específico (método original en inglés).
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Alias de generateToken, para compatibilidad con el código que llama a "generarToken".
     */
    public String generarToken(String username) {
        return generateToken(username);
    }

    /**
     * Genera el token usando claims y el usuario.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Valida que el token sea correcto para el usuario y no esté vencido.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
