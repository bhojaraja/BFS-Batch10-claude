package com.wipro.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * ProductRequestDTO - Data Transfer Object for creating/updating products.
 *
 * This DTO is used to receive product information from API requests.
 * It includes validation annotations to ensure data integrity before
 * processing by the service layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product request payload for creating/updating products")
public class ProductRequestDTO {

    @Schema(description = "Unique product code identifier", example = "PROD-001", minLength = 1, maxLength = 50)
    @NotBlank(message = "Product code is required")
    @Size(min = 1, max = 50, message = "Product code must be between 1 and 50 characters")
    private String productCode;

    @Schema(description = "Product name", example = "Laptop", minLength = 1, maxLength = 255)
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    private String name;

    @Schema(description = "Product description", example = "High performance laptop with 16GB RAM", maxLength = 1000)
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Schema(description = "Product category", example = "Electronics", minLength = 1, maxLength = 100)
    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    private String category;

    @Schema(description = "Product price", example = "999.99", minimum = "0.01", maximum = "999999.99")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price must not exceed 999999.99")
    private BigDecimal price;

    @Schema(description = "Product stock quantity", example = "50", minimum = "0")
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be 0 or more")
    @Max(value = 2147483647, message = "Stock quantity exceeds maximum allowed value")
    private Integer stockQuantity;
}
