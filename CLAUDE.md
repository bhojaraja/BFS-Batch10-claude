# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Product Catalog Management System is a Spring Boot REST API for managing product inventory. It provides CRUD operations and search capabilities for products organized by category. Uses an in-memory H2 database with Spring Data JPA for persistence.

## Technology Stack

- **Framework**: Spring Boot 3.3.0
- **Java Version**: 17
- **Build Tool**: Maven
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA with Hibernate
- **Testing**: JUnit 5, Mockito
- **Additional**: Lombok for boilerplate reduction

## Architecture

The codebase follows a **layered architecture**:

1. **Controller Layer** (`ProductController`, etc.) - REST endpoints, request validation/mapping
2. **Service Layer** (`ProductService`, etc.) - Business logic, validation, exception handling
3. **Repository Layer** - Spring Data JPA repositories for data access
4. **Entity/DTO Layer** - Domain entities and Data Transfer Objects for API contracts
5. **Database Layer** - H2 in-memory database with automatic schema updates via Hibernate

Current package structure:
- `com.wipro.productcatalog` - Main Spring Boot application
- `com.wipro.productcatalog.entity` - JPA entities (Product)
- `com.wipro.productcatalog.dto` - Request/Response DTOs
- `com.wipro.productcatalog.repository` - Spring Data JPA repositories
- Service, Controller, Exception handler layers (if implemented)

## Key Files

- **pom.xml** - Maven configuration with all dependencies
- **application.properties** - Spring Boot configuration (port 8080, context path `/productcatalog`, H2 console enabled at `/h2-console`)
- **ProductCatalogApplication.java** - Entry point
- **Product.java** - Main entity with fields: id, name, description, category, price, stockQuantity
- **SYSTEM_DESIGN.md** - Detailed architecture diagrams and component interactions
- **REQUIREMENTS.md** - Functional and business requirements
- **USER_STORIES_AND_ACCEPTANCE_CRITERIA.md** - User stories with acceptance criteria

## Common Commands

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run all tests
mvn test

# Run a specific test
mvn test -Dtest=ProductServiceTest

# Run with DEBUG logging
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.wipro.productcatalog=DEBUG"

# Access H2 console (when app is running)
# Navigate to: http://localhost:8080/productcatalog/h2-console
```

## Key Features

1. **Add Product** - Create new products with mandatory fields (name, category, price)
2. **View Products** - List all products with pagination and sorting support
3. **Search by ID** - Find a product by unique identifier
4. **Search by Category** - List all products in a category
5. **Update Product** - Modify existing product details (partial updates supported)
6. **Delete Product** - Remove products from catalog

## Core Product Requirements

**Required Fields**: name, category, price (must validate before creation/update)
**Validations**:
- Price must be positive decimal
- Stock quantity must be non-negative integer
- Product ID must be unique

## API Base Path

All REST endpoints are under: `http://localhost:8080/productcatalog/`

## Database

H2 in-memory database configuration:
- JDBC URL: `jdbc:h2:mem:productcatalogdb`
- Schema auto-updates enabled via `spring.jpa.hibernate.ddl-auto=update`
- Console accessible at `/h2-console` for debugging during development

## Design Patterns Used

- **Repository Pattern** - Spring Data JPA repositories abstract data access
- **DTO Pattern** - Separate DTOs for API contracts (ProductRequestDTO, ProductResponseDTO)
- **Service Layer Pattern** - Business logic isolated in service layer
- **Exception Handling** - Centralized exception handling (likely global exception handler)

## Testing Notes

Tests use JUnit 5 and Mockito. When adding new functionality:
- Unit test services with mocked repositories
- Integration tests should use embedded H2 database
- Mock external dependencies, but keep database interactions as integration tests
