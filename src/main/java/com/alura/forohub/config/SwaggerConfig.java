// Archivo: src/main/java/com/alura/forohub/config/SwaggerConfig.java
// Tipo: code/java
// Propósito: Definición única y mínima del bean OpenAPI para springdoc.
// NOTA: Asegurate de eliminar o comentar cualquier otra clase que defina otro bean OpenAPI
// (por ejemplo OpenApiConfig) para evitar ambigüedad de beans al arrancar la app.

package com.alura.forohub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuración simple para OpenAPI / Swagger UI.
 * - Define un único bean OpenAPI marcado @Primary para evitar conflictos.
 * - Si más adelante querés añadir seguridad para la UI, lo hacemos aquí.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    @Primary
    public OpenAPI foroHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ForoHub API")
                        .version("v1")
                        .description("Documentación OpenAPI generada por springdoc")
                );
    }
}
