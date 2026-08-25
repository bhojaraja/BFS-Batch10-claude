package com.wipro.productcatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenApiConfig - Configuration for OpenAPI/Swagger documentation.
 *
 * This configuration class sets up the OpenAPI specification for the Product Catalog API,
 * including API metadata, contact information, license details, and server information.
 * The Swagger UI will be available at: http://localhost:8080/productcatalog/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define OpenAPI specification for the Product Catalog API.
     *
     * @return OpenAPI specification with API info, contact, license, and servers
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog Management System API")
                        .version("1.0.0")
                        .description("REST API for managing product catalog. " +
                                "Provides CRUD operations and search functionality for products.")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact()
                                .name("Product Catalog Team")
                                .url("https://wipro.com")
                                .email("productcatalog@wipro.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/productcatalog")
                                .description("Development Server"),
                        new Server()
                                .url("https://api-staging.example.com/productcatalog")
                                .description("Staging Server"),
                        new Server()
                                .url("https://api.example.com/productcatalog")
                                .description("Production Server")
                ));
    }
}
