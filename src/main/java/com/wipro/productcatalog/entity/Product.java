package com.wipro.productcatalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Entity representing a product in the catalog.
 *
 * This entity maps to the 'products' table in the database and contains
 * all product-related information including pricing and stock details.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_code", columnList = "product_code", unique = true),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stockQuantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer version;

    @PrePersist
    protected void onCreate() {
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (version == null) {
            version = 0;
        }
    }
}
