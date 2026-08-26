package com.wipro.productcatalog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.productcatalog.dto.ProductRequestDTO;
import com.wipro.productcatalog.dto.ProductResponseDTO;
import com.wipro.productcatalog.entity.Product;
import com.wipro.productcatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductControllerIntegrationTest
 *
 * Comprehensive integration tests for Product Catalog Management System.
 * Tests the complete flow from Controller → Service → Repository → MySQL Database.
 *
 * Uses:
 * - @SpringBootTest for full application context
 * - @AutoConfigureMockMvc for MockMvc setup
 * - Testcontainers MySQL for isolated database testing
 * - JUnit 5 for test framework
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Product Controller Integration Tests")
public class ProductControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("productcatalog_test")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private ProductRequestDTO validProductRequest;
    private String baseUrl = "/productcatalog/products";

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        validProductRequest = ProductRequestDTO.builder()
                .productCode("LAPTOP-001")
                .name("Dell XPS 13")
                .description("13-inch FHD laptop with Intel i7")
                .category("Electronics")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .build();
    }

    // =====================================================================
    // TEST SCENARIO 1: Create Product
    // =====================================================================

    @Test
    @DisplayName("Should create product successfully with valid data")
    void testCreateProductSuccess() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(validProductRequest);

        // Act
        MvcResult result = mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.productCode").value("LAPTOP-001"))
                .andExpect(jsonPath("$.name").value("Dell XPS 13"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andReturn();

        // Assert - Verify database persistence
        String responseBody = result.getResponse().getContentAsString();
        ProductResponseDTO response = objectMapper.readValue(responseBody, ProductResponseDTO.class);

        Product savedProduct = productRepository.findById(response.getId()).orElse(null);
        assertNotNull(savedProduct, "Product should be persisted in database");
        assertEquals("LAPTOP-001", savedProduct.getProductCode());
        assertEquals("Dell XPS 13", savedProduct.getName());
        assertEquals(new BigDecimal("1299.99"), savedProduct.getPrice());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when product name is empty")
    void testCreateProductWithEmptyName() throws Exception {
        // Arrange
        validProductRequest.setName("");
        String requestBody = objectMapper.writeValueAsString(validProductRequest);

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

        // Verify no product created in database
        assertEquals(0, productRepository.count());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when price is negative")
    void testCreateProductWithNegativePrice() throws Exception {
        // Arrange
        validProductRequest.setPrice(new BigDecimal("-100.00"));
        String requestBody = objectMapper.writeValueAsString(validProductRequest);

        // Act & Assert
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // Verify no product created
        assertEquals(0, productRepository.count());
    }

    @Test
    @DisplayName("Should return 409 Conflict when product code already exists")
    void testCreateProductWithDuplicateCode() throws Exception {
        // Arrange - Create first product
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isCreated());

        // Act & Assert - Try to create second product with same code
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").containsString("already exists"));

        // Verify only one product in database
        assertEquals(1, productRepository.count());
    }

    // =====================================================================
    // TEST SCENARIO 2: Get Product By Id
    // =====================================================================

    @Test
    @DisplayName("Should retrieve product by ID successfully")
    void testGetProductByIdSuccess() throws Exception {
        // Arrange - Create product
        Product savedProduct = productRepository.save(Product.builder()
                .productCode("PHONE-001")
                .name("iPhone 15 Pro")
                .description("Latest iPhone model")
                .category("Electronics")
                .price(new BigDecimal("999.99"))
                .stockQuantity(20)
                .build());

        // Act & Assert
        mockMvc.perform(get(baseUrl + "/{id}", savedProduct.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.productCode").value("PHONE-001"))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    @DisplayName("Should return 404 Not Found when product ID does not exist")
    void testGetProductByIdNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get(baseUrl + "/{id}", 999L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").containsString("not found"));
    }

    @Test
    @DisplayName("Should retrieve product and verify database is the source of truth")
    void testGetProductVerifyDatabasePersistence() throws Exception {
        // Arrange - Create product
        Product createdProduct = productRepository.save(Product.builder()
                .productCode("TABLET-001")
                .name("iPad Pro")
                .description("12.9-inch iPad")
                .category("Electronics")
                .price(new BigDecimal("1199.99"))
                .stockQuantity(15)
                .build());

        // Act
        MvcResult result = mockMvc.perform(get(baseUrl + "/{id}", createdProduct.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert - Verify response matches database record
        String responseBody = result.getResponse().getContentAsString();
        ProductResponseDTO response = objectMapper.readValue(responseBody, ProductResponseDTO.class);

        Product dbProduct = productRepository.findById(createdProduct.getId()).orElseThrow();
        assertEquals(response.getProductCode(), dbProduct.getProductCode());
        assertEquals(response.getName(), dbProduct.getName());
        assertEquals(response.getPrice(), dbProduct.getPrice());
        assertEquals(response.getStockQuantity(), dbProduct.getStockQuantity());
    }

    // =====================================================================
    // TEST SCENARIO 3: Get All Products
    // =====================================================================

    @Test
    @DisplayName("Should retrieve all products successfully")
    void testGetAllProductsSuccess() throws Exception {
        // Arrange - Create multiple products
        productRepository.save(Product.builder()
                .productCode("PROD-001")
                .name("Product 1")
                .category("Electronics")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .build());

        productRepository.save(Product.builder()
                .productCode("PROD-002")
                .name("Product 2")
                .category("Books")
                .price(new BigDecimal("50.00"))
                .stockQuantity(10)
                .build());

        // Act & Assert
        mockMvc.perform(get(baseUrl)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productCode").value("PROD-001"))
                .andExpect(jsonPath("$[1].productCode").value("PROD-002"));
    }

    @Test
    @DisplayName("Should return empty array when no products exist")
    void testGetAllProductsEmpty() throws Exception {
        // Act & Assert
        mockMvc.perform(get(baseUrl)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should retrieve all products and verify database count")
    void testGetAllProductsVerifyCount() throws Exception {
        // Arrange - Create 3 products
        for (int i = 1; i <= 3; i++) {
            productRepository.save(Product.builder()
                    .productCode("PROD-00" + i)
                    .name("Product " + i)
                    .category("Electronics")
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(5)
                    .build());
        }

        // Act & Assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // Verify database count
        assertEquals(3, productRepository.count());
    }

    // =====================================================================
    // TEST SCENARIO 4: Update Product
    // =====================================================================

    @Test
    @DisplayName("Should update product successfully with new data")
    void testUpdateProductSuccess() throws Exception {
        // Arrange - Create initial product
        Product initialProduct = productRepository.save(Product.builder()
                .productCode("LAPTOP-002")
                .name("Old Laptop")
                .description("Old description")
                .category("Electronics")
                .price(new BigDecimal("800.00"))
                .stockQuantity(5)
                .build());

        // Create update request
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .productCode("LAPTOP-002")
                .name("New Laptop Pro Max")
                .description("Updated description")
                .category("Computers")
                .price(new BigDecimal("1500.00"))
                .stockQuantity(20)
                .build();

        // Act
        mockMvc.perform(put(baseUrl + "/{id}", initialProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(initialProduct.getId()))
                .andExpect(jsonPath("$.name").value("New Laptop Pro Max"))
                .andExpect(jsonPath("$.price").value(1500.00))
                .andExpect(jsonPath("$.stockQuantity").value(20));

        // Assert - Verify database persistence
        Product updatedProduct = productRepository.findById(initialProduct.getId()).orElseThrow();
        assertEquals("New Laptop Pro Max", updatedProduct.getName());
        assertEquals(new BigDecimal("1500.00"), updatedProduct.getPrice());
        assertEquals(20, updatedProduct.getStockQuantity());
        assertEquals("Computers", updatedProduct.getCategory());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void testUpdateProductNotFound() throws Exception {
        // Arrange
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .productCode("LAPTOP-002")
                .name("Updated Laptop")
                .category("Electronics")
                .price(new BigDecimal("1500.00"))
                .stockQuantity(20)
                .build();

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 400 when updating product with invalid price")
    void testUpdateProductWithInvalidPrice() throws Exception {
        // Arrange - Create initial product
        Product product = productRepository.save(Product.builder()
                .productCode("PHONE-002")
                .name("Test Phone")
                .category("Electronics")
                .price(new BigDecimal("500.00"))
                .stockQuantity(10)
                .build());

        // Create update with invalid price
        ProductRequestDTO invalidUpdate = ProductRequestDTO.builder()
                .productCode("PHONE-002")
                .name("Test Phone")
                .category("Electronics")
                .price(new BigDecimal("0.00"))
                .stockQuantity(10)
                .build();

        // Act & Assert
        mockMvc.perform(put(baseUrl + "/{id}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());

        // Verify product was NOT updated in database
        Product dbProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(new BigDecimal("500.00"), dbProduct.getPrice());
    }

    // =====================================================================
    // TEST SCENARIO 5: Delete Product
    // =====================================================================

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProductSuccess() throws Exception {
        // Arrange - Create product
        Product product = productRepository.save(Product.builder()
                .productCode("DELETE-TEST")
                .name("To Delete")
                .category("Electronics")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .build());

        Long productId = product.getId();
        assertTrue(productRepository.existsById(productId));

        // Act
        mockMvc.perform(delete(baseUrl + "/{id}", productId))
                .andExpect(status().isNoContent());

        // Assert - Verify deletion from database
        assertFalse(productRepository.existsById(productId));
        assertEquals(0, productRepository.count());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent product")
    void testDeleteProductNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(baseUrl + "/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should delete product and verify it no longer exists in database")
    void testDeleteProductVerifyDatabaseState() throws Exception {
        // Arrange - Create multiple products
        Product product1 = productRepository.save(Product.builder()
                .productCode("PROD-DELETE-1")
                .name("Product 1")
                .category("Electronics")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .build());

        Product product2 = productRepository.save(Product.builder()
                .productCode("PROD-DELETE-2")
                .name("Product 2")
                .category("Books")
                .price(new BigDecimal("50.00"))
                .stockQuantity(10)
                .build());

        assertEquals(2, productRepository.count());

        // Act - Delete first product
        mockMvc.perform(delete(baseUrl + "/{id}", product1.getId()))
                .andExpect(status().isNoContent());

        // Assert - Verify database state
        assertEquals(1, productRepository.count());
        assertFalse(productRepository.existsById(product1.getId()));
        assertTrue(productRepository.existsById(product2.getId()));

        // Verify remaining product is correct
        Product remaining = productRepository.findById(product2.getId()).orElseThrow();
        assertEquals("PROD-DELETE-2", remaining.getProductCode());
    }

    // =====================================================================
    // TEST SCENARIO 6: Search Product By Category
    // =====================================================================

    @Test
    @DisplayName("Should retrieve all products in specified category")
    void testSearchProductByCategory() throws Exception {
        // Arrange - Create products in different categories
        productRepository.save(Product.builder()
                .productCode("LAPTOP-DELL")
                .name("Dell Laptop")
                .category("Electronics")
                .price(new BigDecimal("1000.00"))
                .stockQuantity(5)
                .build());

        productRepository.save(Product.builder()
                .productCode("LAPTOP-HP")
                .name("HP Laptop")
                .category("Electronics")
                .price(new BigDecimal("800.00"))
                .stockQuantity(3)
                .build());

        productRepository.save(Product.builder()
                .productCode("BOOK-JAVA")
                .name("Java Programming")
                .category("Books")
                .price(new BigDecimal("50.00"))
                .stockQuantity(20)
                .build());

        // Act & Assert - Get Electronics
        mockMvc.perform(get(baseUrl + "/category/{category}", "Electronics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].category", hasItems("Electronics", "Electronics")))
                .andExpect(jsonPath("$[*].productCode", hasItems("LAPTOP-DELL", "LAPTOP-HP")));
    }

    @Test
    @DisplayName("Should return empty list when category has no products")
    void testSearchProductByCategoryEmpty() throws Exception {
        // Arrange - Create product in Electronics
        productRepository.save(Product.builder()
                .productCode("PROD-ELEC")
                .name("Electronic Product")
                .category("Electronics")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .build());

        // Act & Assert - Search for Furniture
        mockMvc.perform(get(baseUrl + "/category/{category}", "Furniture")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should search by category and verify database filtering")
    void testSearchProductByCategoryVerifyFiltering() throws Exception {
        // Arrange - Create diverse products
        for (int i = 1; i <= 5; i++) {
            productRepository.save(Product.builder()
                    .productCode("ELEC-" + i)
                    .name("Electronic " + i)
                    .category("Electronics")
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(i)
                    .build());
        }

        for (int i = 1; i <= 3; i++) {
            productRepository.save(Product.builder()
                    .productCode("BOOK-" + i)
                    .name("Book " + i)
                    .category("Books")
                    .price(new BigDecimal("30.00"))
                    .stockQuantity(i * 2)
                    .build());
        }

        // Act
        MvcResult result = mockMvc.perform(get(baseUrl + "/category/{category}", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andReturn();

        // Assert - Verify all returned products are Electronics
        String responseBody = result.getResponse().getContentAsString();
        ProductResponseDTO[] products = objectMapper.readValue(responseBody, ProductResponseDTO[].class);

        for (ProductResponseDTO product : products) {
            assertEquals("Electronics", product.getCategory(), "All products should be in Electronics category");
        }
    }

    // =====================================================================
    // TEST SCENARIO 7: Search Product By Name
    // =====================================================================

    @Test
    @DisplayName("Should search products by name containing search term")
    void testSearchProductByName() throws Exception {
        // Arrange - Create products with similar names
        productRepository.save(Product.builder()
                .productCode("LAPTOP-DELL")
                .name("Dell XPS 13 Laptop")
                .category("Electronics")
                .price(new BigDecimal("1000.00"))
                .stockQuantity(5)
                .build());

        productRepository.save(Product.builder()
                .productCode("LAPTOP-HP")
                .name("HP Pavilion Laptop")
                .category("Electronics")
                .price(new BigDecimal("800.00"))
                .stockQuantity(3)
                .build());

        productRepository.save(Product.builder()
                .productCode("TABLET-APPLE")
                .name("Apple iPad Tablet")
                .category("Electronics")
                .price(new BigDecimal("600.00"))
                .stockQuantity(7)
                .build());

        // Act & Assert - Search for "Laptop"
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "Laptop")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name", hasItems(
                        containsString("Laptop"),
                        containsString("Laptop")
                )));
    }

    @Test
    @DisplayName("Should perform case-insensitive search by name")
    void testSearchProductByNameCaseInsensitive() throws Exception {
        // Arrange
        productRepository.save(Product.builder()
                .productCode("PROD-TEST")
                .name("Premium Laptop Computer")
                .category("Electronics")
                .price(new BigDecimal("1000.00"))
                .stockQuantity(5)
                .build());

        // Act & Assert - Search with lowercase
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "laptop")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Premium Laptop Computer"));

        // Act & Assert - Search with uppercase
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "LAPTOP")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Act & Assert - Search with mixed case
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "LaP")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Should return empty list when no products match name search")
    void testSearchProductByNameNoMatch() throws Exception {
        // Arrange
        productRepository.save(Product.builder()
                .productCode("PROD-DELL")
                .name("Dell Laptop")
                .category("Electronics")
                .price(new BigDecimal("1000.00"))
                .stockQuantity(5)
                .build());

        // Act & Assert
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "NonExistentProduct")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =====================================================================
    // INTEGRATION TESTS: Complete Flow Scenarios
    // =====================================================================

    @Test
    @DisplayName("Complete flow: Create, Retrieve, Update, Delete product")
    void testCompleteProductLifecycle() throws Exception {
        // Step 1: Create Product
        String createRequest = objectMapper.writeValueAsString(validProductRequest);
        MvcResult createResult = mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponseDTO createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductResponseDTO.class
        );
        Long productId = createdProduct.getId();

        // Verify in database
        Product dbProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("LAPTOP-001", dbProduct.getProductCode());

        // Step 2: Retrieve Product
        mockMvc.perform(get(baseUrl + "/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Dell XPS 13"));

        // Step 3: Update Product
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .productCode("LAPTOP-001")
                .name("Updated Dell XPS 13")
                .description("Updated description")
                .category("Computers")
                .price(new BigDecimal("1499.99"))
                .stockQuantity(15)
                .build();

        mockMvc.perform(put(baseUrl + "/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Dell XPS 13"));

        // Verify in database
        Product updatedDbProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("Updated Dell XPS 13", updatedDbProduct.getName());
        assertEquals(new BigDecimal("1499.99"), updatedDbProduct.getPrice());

        // Step 4: Delete Product
        mockMvc.perform(delete(baseUrl + "/{id}", productId))
                .andExpect(status().isNoContent());

        // Verify deletion in database
        assertFalse(productRepository.existsById(productId));
    }

    @Test
    @DisplayName("Complete flow: Create multiple products, search by category and name")
    void testMultipleProductsSearchFlow() throws Exception {
        // Create products
        Product product1 = productRepository.save(Product.builder()
                .productCode("LAPTOP-DELL")
                .name("Dell XPS 15 Laptop")
                .category("Electronics")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .build());

        Product product2 = productRepository.save(Product.builder()
                .productCode("LAPTOP-HP")
                .name("HP EliteBook Laptop")
                .category("Electronics")
                .price(new BigDecimal("999.99"))
                .stockQuantity(8)
                .build());

        Product product3 = productRepository.save(Product.builder()
                .productCode("BOOK-JAVA")
                .name("Java Programming Guide")
                .category("Books")
                .price(new BigDecimal("45.99"))
                .stockQuantity(50)
                .build());

        // Search by Category - Electronics
        mockMvc.perform(get(baseUrl + "/category/{category}", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Search by Category - Books
        mockMvc.perform(get(baseUrl + "/category/{category}", "Books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Search by Name - "Laptop"
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Search by Name - "Java"
        mockMvc.perform(get(baseUrl + "/search")
                .param("name", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Verify database state
        assertEquals(3, productRepository.count());
    }
}
