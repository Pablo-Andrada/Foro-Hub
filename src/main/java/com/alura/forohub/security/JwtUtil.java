package com.alura.forohub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;

/**
 * Clase utilitaria para gestionar JWT (generar, validar y extraer datos).
 * Corregida para evitar errores de Base64 y compatible con Spring Security.
 */
@Component
public class JwtUtil {

    // Clave secreta para firmar JWT (en producción, usar env variable o properties)
    private static final String SECRET_STRING = "MiSecretoSuperLargoQueDebeSerAlMenos256bits1234567890";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Duración del token: 10 horas
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 10;

    /**
     * Genera un token JWT para un username dado.
     */
    public String generarToken(String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el username (subject) del token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Valida que el token corresponda al usuario y que no esté expirado.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Verifica si el token expiró.
     */
    public Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extrae todos los claims del token.
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
