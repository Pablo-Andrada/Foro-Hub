package com.alura.forohub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para generación y validación de JWT.
 *
 * - Lee la clave desde `jwt.secret` (o variable de entorno).
 * - Exige al menos 32 bytes (HS256).
 * - Provee métodos en español y alias en inglés para compatibilidad con distintas partes del código.
 *
 * Nota: Si la app lanza IllegalStateException al iniciar, fijate que la propiedad `jwt.secret`
 * esté definida (application.properties o env var JWT_SECRET).
 */
@Component
public class JwtUtil {

    private final Key signingKey;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMillis) {

        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("La propiedad 'jwt.secret' no está definida. Definir JWT_SECRET en environment o en application.properties.");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("La clave JWT es demasiado corta. Se requieren al menos 32 bytes/characters para HS256.");
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    // -----------------------
    // GENERACIÓN DE TOKENS
    // -----------------------

    /**
     * Genera un token (método en español).
     */
    public String generarToken(String username) {
        return createToken(Map.of(), username);
    }

    /**
     * Alias en inglés por compatibilidad.
     */
    public String generateToken(String username) {
        return generarToken(username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // -----------------------
    // EXTRACTORES DE CLAIMS
    // -----------------------

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    // -----------------------
    // VALIDACIÓN DE TOKENS
    // -----------------------

    /**
     * Valida token comparando username con userDetails y expiración.
     * Método en español.
     */
    public boolean validarToken(String token, UserDetails userDetails) {
        if (token == null || token.isBlank()) return false;
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Alias en inglés para compatibilidad con JwtFilter que use validateToken(...).
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        return validarToken(token, userDetails);
    }

    /**
     * Validar token sin UserDetails (solo comprobar formato y firma).
     * Método en español.
     */
    public boolean validarToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Alias en inglés.
     */
    public boolean validateToken(String token) {
        return validarToken(token);
    }
}
