package com.wipro.productcatalog.service;

import com.wipro.productcatalog.dto.ProductRequestDTO;
import com.wipro.productcatalog.entity.Product;
import com.wipro.productcatalog.exception.DuplicateProductException;
import com.wipro.productcatalog.exception.ProductNotFoundException;
import com.wipro.productcatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Test Cases")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO productRequestDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequestDTO = ProductRequestDTO.builder()
                .productCode("PROD-001")
                .name("Test Laptop")
                .description("Test laptop description")
                .category("Electronics")
                .price(new BigDecimal("999.99"))
                .stockQuantity(10)
                .build();

        product = Product.builder()
                .id(1L)
                .productCode("PROD-001")
                .name("Test Laptop")
                .description("Test laptop description")
                .category("Electronics")
                .price(new BigDecimal("999.99"))
                .stockQuantity(10)
                .build();
    }

    @Test
    @DisplayName("Should successfully add a new product")
    void testAddProductSuccess() {
        // Arrange
        when(productRepository.existsByProductCode(productRequestDTO.getProductCode()))
                .thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        Product result = productService.addProduct(productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("PROD-001", result.getProductCode());
        assertEquals("Test Laptop", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        assertEquals(10, result.getStockQuantity());
        verify(productRepository, times(1)).existsByProductCode(productRequestDTO.getProductCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw DuplicateProductException when product code already exists")
    void testAddProductDuplicateCode() {
        // Arrange
        when(productRepository.existsByProductCode(productRequestDTO.getProductCode()))
                .thenReturn(true);

        // Act & Assert
        DuplicateProductException exception = assertThrows(
                DuplicateProductException.class,
                () -> productService.addProduct(productRequestDTO)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(productRepository, times(1)).existsByProductCode(productRequestDTO.getProductCode());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product name is blank")
    void testAddProductInvalidName() {
        // Arrange
        productRequestDTO.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(productRequestDTO)
        );

        assertTrue(exception.getMessage().contains("name") || exception.getMessage().contains("blank"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when price is zero or negative")
    void testAddProductInvalidPrice() {
        // Arrange
        productRequestDTO.setPrice(new BigDecimal("-10.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(productRequestDTO)
        );

        assertTrue(exception.getMessage().contains("Price") || exception.getMessage().contains("price"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when stock quantity is negative")
    void testAddProductInvalidStockQuantity() {
        // Arrange
        productRequestDTO.setStockQuantity(-5);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(productRequestDTO)
        );

        assertTrue(exception.getMessage().contains("Stock") || exception.getMessage().contains("stock"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should successfully retrieve a product by ID")
    void testGetProductByIdSuccess() {
        // Arrange
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PROD-001", result.getProductCode());
        assertEquals("Test Laptop", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product ID not found")
    void testGetProductByIdNotFound() {
        // Arrange
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should successfully retrieve all products")
    void testGetAllProductsSuccess() {
        // Arrange
        Product product2 = Product.builder()
                .id(2L)
                .productCode("PROD-002")
                .name("Test Phone")
                .description("Test phone description")
                .category("Electronics")
                .price(new BigDecimal("599.99"))
                .stockQuantity(20)
                .build();

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll())
                .thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PROD-001", result.get(0).getProductCode());
        assertEquals("PROD-002", result.get(1).getProductCode());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testGetAllProductsEmpty() {
        // Arrange
        when(productRepository.findAll())
                .thenReturn(Arrays.asList());

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should successfully update a product")
    void testUpdateProductSuccess() {
        // Arrange
        ProductRequestDTO updateDTO = ProductRequestDTO.builder()
                .productCode("PROD-001")
                .name("Updated Laptop")
                .description("Updated description")
                .category("Computers")
                .price(new BigDecimal("1199.99"))
                .stockQuantity(15)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .productCode("PROD-001")
                .name("Updated Laptop")
                .description("Updated description")
                .category("Computers")
                .price(new BigDecimal("1199.99"))
                .stockQuantity(15)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Laptop", result.getName());
        assertEquals(new BigDecimal("1199.99"), result.getPrice());
        assertEquals(15, result.getStockQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void testUpdateProductNotFound() {
        // Arrange
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(999L, productRequestDTO)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with invalid price")
    void testUpdateProductInvalidPrice() {
        // Arrange
        ProductRequestDTO invalidDTO = ProductRequestDTO.builder()
                .productCode("PROD-001")
                .name("Test Laptop")
                .description("Test laptop description")
                .category("Electronics")
                .price(new BigDecimal("0.00"))
                .stockQuantity(10)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, invalidDTO)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should successfully delete a product")
    void testDeleteProductSuccess() {
        // Arrange
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void testDeleteProductNotFound() {
        // Arrange
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Should successfully retrieve products by category")
    void testGetProductsByCategorySuccess() {
        // Arrange
        Product product2 = Product.builder()
                .id(2L)
                .productCode("PROD-003")
                .name("Another Laptop")
                .description("Another laptop description")
                .category("Electronics")
                .price(new BigDecimal("899.99"))
                .stockQuantity(5)
                .build();

        List<Product> electronics = Arrays.asList(product, product2);
        when(productRepository.findByCategory("Electronics"))
                .thenReturn(electronics);

        // Act
        List<Product> result = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> "Electronics".equals(p.getCategory())));
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    @DisplayName("Should return empty list when no products found in category")
    void testGetProductsByCategoryEmpty() {
        // Arrange
        when(productRepository.findByCategory("NonExistent"))
                .thenReturn(Arrays.asList());

        // Act
        List<Product> result = productService.getProductsByCategory("NonExistent");

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findByCategory("NonExistent");
    }
}
