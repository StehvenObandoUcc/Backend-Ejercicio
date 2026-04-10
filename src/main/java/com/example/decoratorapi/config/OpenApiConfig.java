package com.example.decoratorapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "X-API-KEY";

        return new OpenAPI()
                .info(new Info().title("Decorator Pattern API").version("1.0.0"))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("X-API-KEY")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
