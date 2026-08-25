package com.wipro.productcatalog.repository;

import com.wipro.productcatalog.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository Test Cases")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        product1 = Product.builder()
                .productCode("LAPTOP-001")
                .name("Dell XPS 13")
                .description("13-inch FHD laptop")
                .category("Electronics")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .build();

        product2 = Product.builder()
                .productCode("LAPTOP-002")
                .name("HP Pavilion 15")
                .description("15-inch HD laptop")
                .category("Electronics")
                .price(new BigDecimal("599.99"))
                .stockQuantity(20)
                .build();

        product3 = Product.builder()
                .productCode("BOOK-001")
                .name("Spring Boot Guide")
                .description("Complete Spring Boot guide")
                .category("Books")
                .price(new BigDecimal("49.99"))
                .stockQuantity(50)
                .build();
    }

    @Test
    @DisplayName("Should save a product successfully")
    void testSaveProduct() {
        // Act
        Product savedProduct = productRepository.save(product1);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("LAPTOP-001", savedProduct.getProductCode());
        assertEquals("Dell XPS 13", savedProduct.getName());
        assertEquals("Electronics", savedProduct.getCategory());
    }

    @Test
    @DisplayName("Should find product by ID")
    void testFindById() {
        // Arrange
        Product savedProduct = productRepository.save(product1);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals("LAPTOP-001", foundProduct.get().getProductCode());
        assertEquals("Dell XPS 13", foundProduct.get().getName());
    }

    @Test
    @DisplayName("Should return empty Optional when product not found by ID")
    void testFindByIdNotFound() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should find all products")
    void testFindAll() {
        // Arrange
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertNotNull(products);
        assertEquals(3, products.size());
    }

    @Test
    @DisplayName("Should find products by category - Electronics")
    void testFindByCategory() {
        // Arrange
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // Act
        List<Product> electronics = productRepository.findByCategory("Electronics");

        // Assert
        assertNotNull(electronics);
        assertEquals(2, electronics.size());
        assertTrue(electronics.stream().allMatch(p -> "Electronics".equals(p.getCategory())));
        assertTrue(electronics.stream().anyMatch(p -> "LAPTOP-001".equals(p.getProductCode())));
        assertTrue(electronics.stream().anyMatch(p -> "LAPTOP-002".equals(p.getProductCode())));
    }

    @Test
    @DisplayName("Should find products by category - Books")
    void testFindByCategoryBooks() {
        // Arrange
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // Act
        List<Product> books = productRepository.findByCategory("Books");

        // Assert
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Books", books.get(0).getCategory());
        assertEquals("BOOK-001", books.get(0).getProductCode());
    }

    @Test
    @DisplayName("Should return empty list when no products found for category")
    void testFindByCategoryEmpty() {
        // Arrange
        productRepository.save(product1);
        productRepository.save(product2);

        // Act
        List<Product> nonExistentCategory = productRepository.findByCategory("Furniture");

        // Assert
        assertNotNull(nonExistentCategory);
        assertEquals(0, nonExistentCategory.size());
    }

    @Test
    @DisplayName("Should check if product exists by product code - true case")
    void testExistsByProductCodeTrue() {
        // Arrange
        productRepository.save(product1);

        // Act
        boolean exists = productRepository.existsByProductCode("LAPTOP-001");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should check if product exists by product code - false case")
    void testExistsByProductCodeFalse() {
        // Arrange
        productRepository.save(product1);

        // Act
        boolean exists = productRepository.existsByProductCode("NONEXISTENT-001");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should check if product exists by product code - after deletion")
    void testExistsByProductCodeAfterDeletion() {
        // Arrange
        Product saved = productRepository.save(product1);
        assertTrue(productRepository.existsByProductCode("LAPTOP-001"));

        // Act
        productRepository.delete(saved);

        // Assert
        assertFalse(productRepository.existsByProductCode("LAPTOP-001"));
    }

    @Test
    @DisplayName("Should check if product exists by name - true case")
    void testExistsByNameTrue() {
        // Arrange
        productRepository.save(product1);

        // Act
        boolean exists = productRepository.existsByName("Dell XPS 13");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should check if product exists by name - false case")
    void testExistsByNameFalse() {
        // Arrange
        productRepository.save(product1);

        // Act
        boolean exists = productRepository.existsByName("NonExistent Product");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void testDeleteById() {
        // Arrange
        Product saved = productRepository.save(product1);
        Long id = saved.getId();
        assertTrue(productRepository.existsById(id));

        // Act
        productRepository.deleteById(id);

        // Assert
        assertFalse(productRepository.existsById(id));
    }

    @Test
    @DisplayName("Should update a product")
    void testUpdateProduct() {
        // Arrange
        Product saved = productRepository.save(product1);
        saved.setName("Updated Dell XPS 13");
        saved.setPrice(new BigDecimal("1399.99"));
        saved.setStockQuantity(15);

        // Act
        Product updated = productRepository.save(saved);

        // Assert
        assertEquals("Updated Dell XPS 13", updated.getName());
        assertEquals(new BigDecimal("1399.99"), updated.getPrice());
        assertEquals(15, updated.getStockQuantity());
    }

    @Test
    @DisplayName("Should count all products")
    void testCount() {
        // Arrange
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // Act
        long count = productRepository.count();

        // Assert
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should return zero count when no products exist")
    void testCountEmpty() {
        // Act
        long count = productRepository.count();

        // Assert
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should find multiple products with same category")
    void testFindByCategoryMultiple() {
        // Arrange
        Product electronics1 = Product.builder()
                .productCode("MOUSE-001")
                .name("Wireless Mouse")
                .description("Wireless mouse")
                .category("Electronics")
                .price(new BigDecimal("29.99"))
                .stockQuantity(100)
                .build();

        Product electronics2 = Product.builder()
                .productCode("KEYBOARD-001")
                .name("Mechanical Keyboard")
                .description("Mechanical keyboard")
                .category("Electronics")
                .price(new BigDecimal("79.99"))
                .stockQuantity(50)
                .build();

        productRepository.save(electronics1);
        productRepository.save(electronics2);

        // Act
        List<Product> electronics = productRepository.findByCategory("Electronics");

        // Assert
        assertNotNull(electronics);
        assertEquals(2, electronics.size());
        assertTrue(electronics.stream()
                .allMatch(p -> "Electronics".equals(p.getCategory())));
    }

    @Test
    @DisplayName("Should check product code uniqueness")
    void testProductCodeUniqueness() {
        // Arrange
        productRepository.save(product1);

        // Act
        boolean codeExists = productRepository.existsByProductCode("LAPTOP-001");

        // Assert
        assertTrue(codeExists);
    }

    @Test
    @DisplayName("Should verify products have different categories")
    void testProductDifferentCategories() {
        // Arrange
        productRepository.save(product1);
        productRepository.save(product3);

        // Act
        List<Product> electronics = productRepository.findByCategory("Electronics");
        List<Product> books = productRepository.findByCategory("Books");

        // Assert
        assertEquals(1, electronics.size());
        assertEquals(1, books.size());
        assertNotEquals(electronics.get(0).getCategory(), books.get(0).getCategory());
    }
}
