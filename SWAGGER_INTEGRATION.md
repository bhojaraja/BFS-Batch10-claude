# Swagger Integration - Product Catalog API

## Overview

This document describes the Swagger/OpenAPI integration for the Product Catalog Management System. The API documentation is automatically generated using Springdoc OpenAPI and displayed through Swagger UI.

---

## Installation & Setup

### 1. Maven Dependency

The following dependency has been added to `pom.xml`:

```xml
<!-- Springdoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

### 2. Configuration

Configuration has been added in `application.properties`:

```properties
# Springdoc OpenAPI (Swagger UI) Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.filter=false
springdoc.swagger-ui.show-extensions=true
springdoc.swagger-ui.doc-expansion=list
```

### 3. OpenAPI Configuration Class

File: `src/main/java/com/wipro/productcatalog/config/OpenApiConfig.java`

This class defines:
- **API Title**: "Product Catalog Management System API"
- **Version**: 1.0.0
- **Description**: REST API for managing product catalog with CRUD operations and search functionality
- **Contact**: Product Catalog Team (productcatalog@wipro.com)
- **License**: Apache 2.0
- **Servers**:
  - Development: http://localhost:8080/productcatalog
  - Staging: https://api-staging.example.com/productcatalog
  - Production: https://api.example.com/productcatalog

---

## Accessing Swagger UI

### URLs

| Resource | URL |
|----------|-----|
| **Swagger UI** | http://localhost:8080/productcatalog/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/productcatalog/v3/api-docs |
| **OpenAPI YAML** | http://localhost:8080/productcatalog/v3/api-docs.yaml |
| **H2 Console** | http://localhost:8080/productcatalog/h2-console |

### Access Steps

1. Start the Spring Boot application
2. Open browser and navigate to: `http://localhost:8080/productcatalog/swagger-ui.html`
3. Swagger UI will display all API endpoints with documentation

---

## Annotations Used

### Class-Level Annotations

#### @Tag
Marks a controller as a documented API resource group.

```java
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "API for managing product catalog")
public class ProductController { }
```

### Method-Level Annotations

#### @Operation
Documents the operation (endpoint) with summary and description.

```java
@PostMapping
@Operation(summary = "Create a new product",
        description = "Add a new product to the catalog with validation")
public ResponseEntity<ProductResponseDTO> createProduct(...) { }
```

#### @ApiResponses & @ApiResponse
Documents all possible HTTP responses.

```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(responseCode = "409", description = "Product code already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
```

#### @Parameter
Documents method parameters.

```java
@GetMapping("/{id}")
@Operation(summary = "Get product by ID")
public ResponseEntity<ProductResponseDTO> getProductById(
        @Parameter(description = "Product ID", example = "1")
        @PathVariable Long id) { }
```

#### @Schema
Adds metadata to DTO fields for OpenAPI documentation.

```java
@Schema(description = "Product unique identifier", example = "1")
private Long id;

@Schema(description = "Product price", example = "999.99", minimum = "0.01", maximum = "999999.99")
@NotNull(message = "Price is required")
@DecimalMin(value = "0.01")
@DecimalMax(value = "999999.99")
private BigDecimal price;
```

### Content Annotation

Documents response body schema:

```java
@ApiResponse(responseCode = "201",
        content = @Content(schema = @Schema(implementation = ProductResponseDTO.class)))
```

---

## DTOs Documentation

### ProductRequestDTO

```yaml
ProductRequestDTO:
  type: object
  description: Product request payload for creating/updating products
  properties:
    productCode:
      type: string
      description: Unique product code identifier
      example: PROD-001
      minLength: 1
      maxLength: 50
    name:
      type: string
      description: Product name
      example: Laptop
      minLength: 1
      maxLength: 255
    description:
      type: string
      description: Product description
      example: High performance laptop with 16GB RAM
      maxLength: 1000
    category:
      type: string
      description: Product category
      example: Electronics
      minLength: 1
      maxLength: 100
    price:
      type: number
      format: decimal
      description: Product price
      example: 999.99
      minimum: 0.01
      maximum: 999999.99
    stockQuantity:
      type: integer
      description: Product stock quantity
      example: 50
      minimum: 0
```

### ProductResponseDTO

```yaml
ProductResponseDTO:
  type: object
  description: Product response payload
  properties:
    id:
      type: integer
      format: int64
      description: Product unique identifier
      example: 1
    productCode:
      type: string
      description: Unique product code identifier
      example: PROD-001
    name:
      type: string
      description: Product name
      example: Laptop
    description:
      type: string
      description: Product description
      example: High performance laptop with 16GB RAM
    category:
      type: string
      description: Product category
      example: Electronics
    price:
      type: number
      format: decimal
      description: Product price
      example: 999.99
    stockQuantity:
      type: integer
      description: Product stock quantity
      example: 50
```

### ErrorResponse

```yaml
ErrorResponse:
  type: object
  description: Error response for failed API requests
  properties:
    timestamp:
      type: string
      format: date-time
      description: Timestamp when error occurred
      example: 2026-08-25T10:30:45
    status:
      type: integer
      description: HTTP status code
      example: 404
    message:
      type: string
      description: Error message describing what went wrong
      example: Product not found with ID: 5
```

---

## API Endpoints Documentation

### 1. POST /products - Create Product

```yaml
Summary: Create a new product
Description: Add a new product to the catalog with validation

Request Body:
  Required: true
  Content-Type: application/json
  Schema: ProductRequestDTO

Responses:
  201 Created:
    Description: Product created successfully
    Schema: ProductResponseDTO
  
  400 Bad Request:
    Description: Validation failed or invalid input
    Schema: ErrorResponse
  
  409 Conflict:
    Description: Product code already exists
    Schema: ErrorResponse
```

### 2. GET /products - Get All Products

```yaml
Summary: Retrieve all products
Description: Get a list of all products in the catalog

Parameters: None

Responses:
  200 OK:
    Description: Request successful
    Schema: Array of ProductResponseDTO
  
  500 Internal Server Error:
    Description: Database or server error
    Schema: ErrorResponse
```

### 3. GET /products/{id} - Get Product by ID

```yaml
Summary: Get product by ID
Description: Retrieve a specific product by its unique identifier

Parameters:
  id:
    Type: integer (int64)
    Required: true
    Description: Product ID
    Example: 1

Responses:
  200 OK:
    Description: Product found
    Schema: ProductResponseDTO
  
  404 Not Found:
    Description: Product with given ID does not exist
    Schema: ErrorResponse
```

### 4. PUT /products/{id} - Update Product

```yaml
Summary: Update product
Description: Modify an existing product's information

Parameters:
  id:
    Type: integer (int64)
    Required: true
    Description: Product ID to update
    Example: 1

Request Body:
  Required: true
  Content-Type: application/json
  Schema: ProductRequestDTO

Responses:
  200 OK:
    Description: Product updated successfully
    Schema: ProductResponseDTO
  
  400 Bad Request:
    Description: Validation failed
    Schema: ErrorResponse
  
  404 Not Found:
    Description: Product not found
    Schema: ErrorResponse
```

### 5. DELETE /products/{id} - Delete Product

```yaml
Summary: Delete product
Description: Remove a product from the catalog

Parameters:
  id:
    Type: integer (int64)
    Required: true
    Description: Product ID to delete
    Example: 1

Responses:
  204 No Content:
    Description: Product deleted successfully
  
  404 Not Found:
    Description: Product not found
    Schema: ErrorResponse
```

### 6. GET /products/category/{category} - Get Products by Category

```yaml
Summary: Get products by category
Description: Retrieve all products in a specific category

Parameters:
  category:
    Type: string
    Required: true
    Description: Category name to search for
    Example: Electronics

Responses:
  200 OK:
    Description: Products found
    Schema: Array of ProductResponseDTO
  
  500 Internal Server Error:
    Description: Database or server error
    Schema: ErrorResponse
```

---

## Swagger UI Features

### 1. Try It Out

Each endpoint has a "Try it out" button that allows you to:
- Enter request parameters
- Modify request body
- Execute the request
- View response and response headers

### 2. Response Examples

Swagger UI displays:
- HTTP status code
- Response body (formatted JSON)
- Response headers
- Response time (if enabled)

### 3. Schema Explorer

Click on schema names to expand and view:
- Field descriptions
- Field types and formats
- Constraints (min/max, pattern, etc.)
- Example values

### 4. Search and Filter

- Search for specific endpoints
- Filter by tags
- Sort by method or alphabetically

---

## Exporting API Documentation

### OpenAPI JSON

Download the complete OpenAPI specification in JSON format:

```
http://localhost:8080/productcatalog/v3/api-docs
```

### OpenAPI YAML

Download the OpenAPI specification in YAML format:

```
http://localhost:8080/productcatalog/v3/api-docs.yaml
```

### Usage in External Tools

Use the downloaded files with:
- **Postman**: Import OpenAPI JSON to generate collections
- **Insomnia**: Import OpenAPI file to test endpoints
- **API Gateway**: Deploy API definition to cloud platforms
- **API Documentation Generators**: Generate static documentation

---

## Configuration Customization

### Customize API Info

Edit `OpenApiConfig.java`:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Your API Title")
            .version("1.0.0")
            .description("Your API Description")
            // ... more customization
```

### Change Swagger UI Path

Edit `application.properties`:

```properties
springdoc.swagger-ui.path=/api-docs-ui.html
```

### Enable/Disable Swagger UI

Edit `application.properties`:

```properties
# Disable in production if desired
springdoc.swagger-ui.enabled=true
```

### Show Request Duration

Edit `application.properties`:

```properties
springdoc.swagger-ui.display-request-duration=true
```

---

## Best Practices

### 1. Keep Documentation Updated

- Update `@Operation` when endpoint behavior changes
- Update `@Schema` annotations when DTO fields change
- Add examples to make documentation clearer

### 2. Use Consistent Descriptions

- Use clear, concise descriptions
- Include units and constraints
- Provide realistic examples

### 3. Document Error Cases

- Document all possible error responses
- Include error schema with fields explained
- Show example error messages

### 4. Organize with Tags

- Group related endpoints with `@Tag`
- Use consistent tag names
- Sort tags alphabetically

### 5. Example Values

```java
@Schema(description = "Product price", example = "999.99")
```

Provide realistic examples that help API users understand expected values.

---

## Common Issues & Solutions

### Issue: Swagger UI shows "Loading..."

**Solution**: Ensure OpenApiConfig is in a scanned package and `@Configuration` is present.

### Issue: Endpoints not showing in Swagger UI

**Solution**: Verify controller is marked with `@RestController` and `@RequestMapping`.

### Issue: Schema not showing in response

**Solution**: Add `@Schema` annotation to DTO fields and ensure `@Content` in `@ApiResponse`.

### Issue: Localhost not accessible

**Solution**: Check if application is running on correct port (default 8080).

---

## Testing Swagger Documentation

### 1. Via Browser

1. Navigate to `http://localhost:8080/productcatalog/swagger-ui.html`
2. Expand each endpoint
3. Click "Try it out"
4. Enter test data
5. Click "Execute"
6. Verify response

### 2. Via cURL

```bash
# Get OpenAPI JSON
curl http://localhost:8080/productcatalog/v3/api-docs

# Get OpenAPI YAML
curl http://localhost:8080/productcatalog/v3/api-docs.yaml
```

### 3. Via Postman

1. Open Postman
2. Click "Import"
3. Paste URL: `http://localhost:8080/productcatalog/v3/api-docs`
4. Select "OpenAPI 3.0"
5. Import

---

## References

- [Springdoc OpenAPI Documentation](https://springdoc.org)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.0)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

---

**Version**: 1.0.0  
**Last Updated**: 2026-08-25  
**Status**: Complete
