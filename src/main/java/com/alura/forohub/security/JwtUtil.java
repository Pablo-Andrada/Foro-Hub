package com.alura.forohub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilitario para generar y validar JWT.
 *
 * Cambios importantes:
 * - Soporta secret en Base64 o raw.
 * - Valida longitud mínima (32 bytes) necesaria para HS256.
 * - Lanza IllegalArgumentException con mensaje claro si la configuración es inválida.
 *
 * NOTA: No incluir secretos en el repo. Define JWT_SECRET en las variables de entorno.
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMillis) { // default 1 día si no se pasa
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("La propiedad jwt.secret no está configurada. Define la variable de entorno JWT_SECRET.");
        }

        byte[] keyBytes;
        try {
            // Intentamos decodificar como Base64 (si el secreto fue generado así)
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            // Si falla, usamos los bytes "raw" del string
            keyBytes = secret.getBytes();
        }

        if (keyBytes.length < 32) { // 32 bytes = 256 bits -> mínimo recomendado para HS256
            throw new IllegalArgumentException("jwt.secret demasiado corto. Se requieren al menos 32 bytes (por ejemplo: openssl rand -base64 32).");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    /**
     * Genera un token JWT con el username como subject.
     */
    public String generarToken(String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene el username (subject) desde el token.
     * Lanza excepción si el token no es parseable.
     */
    public String obtenerUsernameDeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida que el token sea correcto y no esté expirado.
     * Devuelve true si válido, false en cualquier error (firma inválida, expirado, corrupto).
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
