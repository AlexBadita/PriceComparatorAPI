package com.example.price_comparator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the OpenAPI (Swagger) documentation
 * for the Price Comparator API.
 *
 * Defines metadata such as the API title, description, and version.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI bean with custom API information.
     *
     * @return an OpenAPI instance configured with the API metadata
     */
    @Bean
    public OpenAPI priceComparatorOpenAPI() {
        return new OpenAPI().info(new Info()
                                        .title("Price Comparator API")
                                        .description("API for comparing prices across different stores")
                                        .version("v1.0"));
    }
}
