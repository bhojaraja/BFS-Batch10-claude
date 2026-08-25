package com.wipro.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Product Catalog Management System application.
 *
 * This Spring Boot application provides REST APIs for managing product catalog,
 * including operations to create, read, update, and delete products.
 */
@SpringBootApplication
public class ProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }

}
