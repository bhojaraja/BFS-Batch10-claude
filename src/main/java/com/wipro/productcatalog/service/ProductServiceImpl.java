package com.wipro.productcatalog.service;

import com.wipro.productcatalog.dto.ProductRequestDTO;
import com.wipro.productcatalog.entity.Product;
import com.wipro.productcatalog.exception.DuplicateProductException;
import com.wipro.productcatalog.exception.ProductNotFoundException;
import com.wipro.productcatalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductServiceImpl - Implementation of ProductService interface.
 *
 * This service class implements all business logic for product management,
 * including validation of business rules, error handling, and database operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Add a new product to the catalog.
     *
     * Business Rules:
     * - Product name is mandatory and not blank
     * - Price must be greater than 0
     * - Stock quantity must be 0 or greater
     * - Product code must be unique
     *
     * @param request the product request containing product details
     * @return the created product with generated ID
     * @throws DuplicateProductException if product code already exists
     */
    @Transactional
    @Override
    public Product addProduct(ProductRequestDTO request) {
        log.info("Adding new product with code: {}", request.getProductCode());

        validateProductRequest(request);

        if (productRepository.existsByProductCode(request.getProductCode())) {
            log.warn("Product code already exists: {}", request.getProductCode());
            throw new DuplicateProductException(
                    "Product code already exists: " + request.getProductCode());
        }

        Product product = Product.builder()
                .productCode(request.getProductCode())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return savedProduct;
    }

    /**
     * Retrieve all products from the catalog.
     *
     * @return a list of all products in the catalog
     */
    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Retrieved {} products", products.size());
        return products;
    }

    /**
     * Retrieve a product by its ID.
     *
     * @param id the product ID
     * @return the product if found
     * @throws ProductNotFoundException if product with given ID does not exist
     */
    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ProductNotFoundException(
                            "Product not found with ID: " + id);
                });

        log.info("Product found with ID: {}", id);
        return product;
    }

    /**
     * Update an existing product's information.
     *
     * Business Rules:
     * - Product name is mandatory and not blank
     * - Price must be greater than 0
     * - Stock quantity must be 0 or greater
     * - Product must exist before updating
     *
     * @param id the product ID to update
     * @param request the updated product request details
     * @return the updated product
     * @throws ProductNotFoundException if product with given ID does not exist
     */
    @Transactional
    @Override
    public Product updateProduct(Long id, ProductRequestDTO request) {
        log.info("Updating product with ID: {}", id);

        validateProductRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update - Product not found with ID: {}", id);
                    return new ProductNotFoundException(
                            "Cannot update - Product not found with ID: " + id);
                });

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);

        return updatedProduct;
    }

    /**
     * Delete a product from the catalog.
     *
     * @param id the product ID to delete
     * @throws ProductNotFoundException if product with given ID does not exist
     */
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete - Product not found with ID: {}", id);
                    return new ProductNotFoundException(
                            "Cannot delete - Product not found with ID: " + id);
                });

        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    /**
     * Retrieve all products in a specific category.
     *
     * @param category the category name to search for
     * @return a list of products in the specified category
     */
    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByCategory(String category) {
        log.debug("Fetching products by category: {}", category);
        List<Product> products = productRepository.findByCategory(category);
        log.info("Retrieved {} products in category: {}", products.size(), category);
        return products;
    }

    /**
     * Validate product request against business rules.
     *
     * Validates:
     * - Product name is not blank (mandatory)
     * - Price is greater than 0 (must be positive)
     * - Stock quantity is 0 or more (cannot be negative)
     *
     * @param request the product request to validate
     * @throws IllegalArgumentException if any validation rule is violated
     */
    private void validateProductRequest(ProductRequestDTO request) {
        log.debug("Validating product request");

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("Validation failed: Product name is mandatory");
            throw new IllegalArgumentException("Product name is mandatory");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: Price must be greater than 0");
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        if (request.getStockQuantity() == null || request.getStockQuantity() < 0) {
            log.warn("Validation failed: Stock quantity cannot be negative");
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            log.warn("Validation failed: Category is mandatory");
            throw new IllegalArgumentException("Category is mandatory");
        }

        log.debug("Product request validation passed");
    }
}
