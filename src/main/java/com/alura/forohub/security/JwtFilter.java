package com.alura.forohub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alura.forohub.security.CustomUserDetailsService;
import com.alura.forohub.security.JwtUtil;

import java.io.IOException;

/**
 * Filtro que se ejecuta una vez por request (OncePerRequestFilter)
 * para validar el JWT en endpoints protegidos.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 1 Obtener encabezado Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2 Validar formato "Bearer TOKEN"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remover "Bearer "
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("Error al extraer usuario del token: " + e.getMessage());
            }
        }

        // 3 Si obtuvimos usuario y no hay autenticación previa en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar datos del usuario desde BD usando tu CustomUserDetailsService
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Validar token con los datos del usuario
            if (jwtUtil.validateToken(token, userDetails)) {

                // Crear objeto de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // Asociar detalles de la request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Guardar autenticación en el contexto
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4️⃣ Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}
