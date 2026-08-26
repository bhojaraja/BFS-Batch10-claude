package com.wipro.productcatalog.service;

import com.wipro.productcatalog.dto.ProductRequestDTO;
import com.wipro.productcatalog.entity.Product;

import java.util.List;

/**
 * ProductService - Business logic interface for product management.
 *
 * This service interface defines all business operations for managing products
 * in the catalog. Implementation classes will provide the actual logic for
 * CRUD operations and search functionality.
 */
public interface ProductService {

    /**
     * Add a new product to the catalog.
     *
     * @param request the product request containing product details
     * @return the created product with generated ID
     * @throws DuplicateProductException if product code already exists
     */
    Product addProduct(ProductRequestDTO request);

    /**
     * Retrieve all products from the catalog.
     *
     * @return a list of all products in the catalog
     */
    List<Product> getAllProducts();

    /**
     * Retrieve a product by its ID.
     *
     * @param id the product ID
     * @return the product if found
     * @throws ProductNotFoundException if product with given ID does not exist
     */
    Product getProductById(Long id);

    /**
     * Update an existing product's information.
     *
     * @param id the product ID to update
     * @param request the updated product request details
     * @return the updated product
     * @throws ProductNotFoundException if product with given ID does not exist
     */
    Product updateProduct(Long id, ProductRequestDTO request);

    /**
     * Delete a product from the catalog.
     *
     * @param id the product ID to delete
     * @throws ProductNotFoundException if product with given ID does not exist
     */
    void deleteProduct(Long id);

    /**
     * Retrieve all products in a specific category.
     *
     * @param category the category name to search for
     * @return a list of products in the specified category
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Search products by name (case-insensitive partial match).
     *
     * @param name the search term to match in product names
     * @return a list of products matching the search term
     */
    List<Product> searchProductByName(String name);
}
