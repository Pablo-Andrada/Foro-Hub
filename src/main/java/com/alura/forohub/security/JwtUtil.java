package com.alura.forohub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Clase utilitaria para generar y validar tokens JWT.
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    // Se inyectan las variables de entorno definidas en application.properties con ${}
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationMillis;
    }

    /**
     * Genera un token JWT con el username como sujeto y expiración configurada.
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
     * Obtiene el username del token JWT.
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
     * Valida que el token JWT sea correcto y no expirado.
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token inválido, expirado o corrupto
            return false;
        }
    }
}
