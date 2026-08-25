package com.wipro.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * ProductResponseDTO - Data Transfer Object for API responses.
 *
 * This DTO is used to return product information in API responses.
 * It contains only the necessary fields to be sent back to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product response payload")
public class ProductResponseDTO {

    @Schema(description = "Product unique identifier", example = "1")
    private Long id;

    @Schema(description = "Unique product code identifier", example = "PROD-001")
    private String productCode;

    @Schema(description = "Product name", example = "Laptop")
    private String name;

    @Schema(description = "Product description", example = "High performance laptop with 16GB RAM")
    private String description;

    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Product stock quantity", example = "50")
    private Integer stockQuantity;
}
