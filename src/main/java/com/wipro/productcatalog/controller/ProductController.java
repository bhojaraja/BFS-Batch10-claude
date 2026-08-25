package com.wipro.productcatalog.controller;

import com.wipro.productcatalog.dto.ProductRequestDTO;
import com.wipro.productcatalog.dto.ProductResponseDTO;
import com.wipro.productcatalog.dto.ErrorResponse;
import com.wipro.productcatalog.entity.Product;
import com.wipro.productcatalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductController - REST API controller for product management.
 *
 * This controller handles all HTTP requests related to products.
 * It acts as the entry point for the API and delegates all business logic
 * to the ProductService layer.
 *
 * Base URL: /products
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "API for managing product catalog")
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product.
     *
     * HTTP Method: POST
     * Endpoint: POST /products
     * Status: 201 Created
     */
    @PostMapping
    @Operation(summary = "Create a new product",
            description = "Add a new product to the catalog with validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or duplicate product code",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Product code already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request) {

        log.info("Creating new product with code: {}", request.getProductCode());

        Product product = productService.addProduct(request);
        ProductResponseDTO response = mapToResponseDTO(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve all products from the catalog.
     *
     * HTTP Method: GET
     * Endpoint: GET /products
     * Status: 200 OK
     *
     * @return ResponseEntity with list of all products and 200 status
     *
     * Example Response (200):
     * [
     *   {
     *     "id": 1,
     *     "name": "Laptop",
     *     "category": "Electronics",
     *     "price": 999.99,
     *     "stockQuantity": 50
     *   },
     *   {
     *     "id": 2,
     *     "name": "Phone",
     *     "category": "Electronics",
     *     "price": 499.99,
     *     "stockQuantity": 100
     *   }
     * ]
     *
     * Possible Errors:
     * - 500 Internal Server Error: Database error
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {

        log.info("Fetching all products");

        List<Product> products = productService.getAllProducts();
        List<ProductResponseDTO> response = products.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a specific product by its ID.
     *
     * HTTP Method: GET
     * Endpoint: GET /products/{id}
     * Status: 200 OK
     *
     * @param id the product ID
     * @return ResponseEntity with product details and 200 status
     *
     * Example Request:
     * GET /products/1
     *
     * Example Response (200):
     * {
     *   "id": 1,
     *   "name": "Laptop",
     *   "description": "High performance laptop",
     *   "category": "Electronics",
     *   "price": 999.99,
     *   "stockQuantity": 50
     * }
     *
     * Possible Errors:
     * - 404 Not Found: Product with given ID does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {

        log.info("Fetching product with ID: {}", id);

        Product product = productService.getProductById(id);
        ProductResponseDTO response = mapToResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing product.
     *
     * HTTP Method: PUT
     * Endpoint: PUT /products/{id}
     * Status: 200 OK
     *
     * @param id the product ID to update
     * @param request the product request with updated details
     * @return ResponseEntity with updated product and 200 status
     *
     * Example Request:
     * PUT /products/1
     * {
     *   "productCode": "PROD-001",
     *   "name": "Laptop Pro",
     *   "description": "Ultra-high performance laptop",
     *   "category": "Electronics",
     *   "price": 1299.99,
     *   "stockQuantity": 45
     * }
     *
     * Example Response (200):
     * {
     *   "id": 1,
     *   "name": "Laptop Pro",
     *   "description": "Ultra-high performance laptop",
     *   "category": "Electronics",
     *   "price": 1299.99,
     *   "stockQuantity": 45
     * }
     *
     * Possible Errors:
     * - 400 Bad Request: Validation failed (@Valid)
     * - 404 Not Found: Product with given ID does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {

        log.info("Updating product with ID: {}", id);

        Product product = productService.updateProduct(id, request);
        ProductResponseDTO response = mapToResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a product from the catalog.
     *
     * HTTP Method: DELETE
     * Endpoint: DELETE /products/{id}
     * Status: 204 No Content
     *
     * @param id the product ID to delete
     * @return ResponseEntity with 204 status (no body)
     *
     * Example Request:
     * DELETE /products/1
     *
     * Example Response (204):
     * (No Content)
     *
     * Possible Errors:
     * - 404 Not Found: Product with given ID does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        log.info("Deleting product with ID: {}", id);

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve all products in a specific category.
     *
     * HTTP Method: GET
     * Endpoint: GET /products/category/{category}
     * Status: 200 OK
     *
     * @param category the category name to search for
     * @return ResponseEntity with list of products in the category and 200 status
     *
     * Example Request:
     * GET /products/category/Electronics
     *
     * Example Response (200):
     * [
     *   {
     *     "id": 1,
     *     "name": "Laptop",
     *     "category": "Electronics",
     *     "price": 999.99,
     *     "stockQuantity": 50
     *   },
     *   {
     *     "id": 2,
     *     "name": "Phone",
     *     "category": "Electronics",
     *     "price": 499.99,
     *     "stockQuantity": 100
     *   }
     * ]
     *
     * Possible Errors:
     * - 500 Internal Server Error: Database error
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @PathVariable String category) {

        log.info("Fetching products by category: {}", category);

        List<Product> products = productService.getProductsByCategory(category);
        List<ProductResponseDTO> response = products.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to convert Product entity to ProductResponseDTO.
     *
     * @param product the product entity
     * @return ProductResponseDTO
     */
    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
