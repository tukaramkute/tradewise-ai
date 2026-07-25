package com.tradewise.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 3 documentation configuration. The bearer-auth scheme is
 * declared now so the "Authorize" button is ready once JWT lands in Phase 3.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI tradeWiseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TradeWise AI API")
                        .description("AI-powered Trading Journal and Portfolio Analyzer — REST API")
                        .version("v0.1.0")
                        .contact(new Contact().name("TradeWise AI").email("support@tradewise.ai"))
                        .license(new License().name("Proprietary")))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token (enabled in Phase 3)")));
    }
}
