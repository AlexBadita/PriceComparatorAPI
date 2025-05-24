package com.example.price_comparator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI priceComparatorOpenAPI() {
        return new OpenAPI().info(new Info()
                                        .title("Price Comparator API")
                                        .description("API for comparing prices across different stores")
                                        .version("v1.0"));
    }
}
