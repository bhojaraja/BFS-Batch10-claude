package com.wipro.productcatalog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ErrorResponse - Standard error response for API errors.
 *
 * This DTO is used to return consistent error information to clients
 * when exceptions occur during API request processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response for failed API requests")
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    @Schema(description = "Timestamp when error occurred", example = "2026-08-25T10:30:45")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    @Schema(description = "HTTP status code", example = "404")
    private Integer status;

    /**
     * Error message describing what went wrong.
     */
    @Schema(description = "Error message describing what went wrong", example = "Product not found with ID: 5")
    private String message;
}
