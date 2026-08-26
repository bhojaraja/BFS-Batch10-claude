package com.wipro.productcatalog.repository;

import com.wipro.productcatalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository - Data Access Object for Product entity.
 *
 * This repository provides CRUD operations and custom query methods
 * for the Product entity using Spring Data JPA.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by category.
     *
     * @param category the category name to search for
     * @return a list of products matching the specified category
     */
    List<Product> findByCategory(String category);

    /**
     * Check if a product exists by name.
     *
     * @param name the product name to check
     * @return true if a product with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a product exists by product code.
     *
     * @param productCode the product code to check
     * @return true if a product with the given code exists, false otherwise
     */
    boolean existsByProductCode(String productCode);

    /**
     * Find product by name (case-insensitive partial match).
     *
     * @param name the product name to search for
     * @return a list of products matching the name
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
}
