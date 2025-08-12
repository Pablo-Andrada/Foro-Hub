package com.alura.forohub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Configuración de seguridad con Spring Security y JWT.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // No usamos cookies o sesiones, solo JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // No usamos csrf porque es una API REST (si usas navegador, revisa)
                .csrf(csrf -> csrf.disable())
                // Configuramos las rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (login, registro, etc)
                        .requestMatchers("/api/auth/**").permitAll()
                        // El resto requiere autenticación
                        .anyRequest().authenticated()
                )
                // Agregar nuestro filtro JWT antes del filtro de autenticación de usuario
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Para inyectar AuthenticationManager si necesitás luego (por ej login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
