package com.alura.forohub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Configuración de seguridad central.
 *
 * - Agrega el filtro JWT (jwtFilter).
 * - Permite acceso público a la documentación OpenAPI/Swagger.
 * - Mantiene 401 para no autenticados y 403 para accesos denegados.
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
                // Stateless: usamos JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // API REST: deshabilitamos CSRF (si solo consumís desde cliente tipo SPA/API)
                .csrf(csrf -> csrf.disable())
                // Endpoints públicos y protegidos
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos para autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Swagger / OpenAPI (hacer públicos para poder testear la UI)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Opcional: permitir acceso a recursos estáticos y root
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/webjars/**", "/swagger-ui/**").permitAll()
                        // Resto requiere autenticación
                        .anyRequest().authenticated()
                )
                // Manejo explícito de errores: 401 para no autenticado, 403 si falta permiso
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                )
                // Agregar filtro JWT antes del filtro de autenticación por usuario
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Para inyectar AuthenticationManager si se necesita (login programático, tests, etc.)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // BCrypt PasswordEncoder para la app
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
