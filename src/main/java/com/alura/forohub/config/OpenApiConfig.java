package com.alura.forohub.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración OpenAPI / Swagger UI.
 *
 * Acceder a la UI: http://localhost:8080/swagger-ui/index.html
 *
 * No modifica ninguna seguridad por defecto; si querés que Swagger esté
 * disponible solo en profile dev, movemos esto a @Profile("dev").
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foroHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ForoHub API")
                        .version("v1")
                        .description("API REST para ForoHub — tópicos y respuestas. Autenticación JWT incluida.")
                        .contact(new Contact().name("Pablo Andrada").email("pablo@example.com"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                );
    }
}
