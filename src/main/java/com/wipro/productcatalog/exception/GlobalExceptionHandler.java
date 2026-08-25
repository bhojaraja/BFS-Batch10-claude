package com.wipro.productcatalog.exception;

import com.wipro.productcatalog.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * GlobalExceptionHandler - Centralized exception handling for the application.
 *
 * This class handles all exceptions thrown during API request processing
 * and returns appropriate HTTP status codes with consistent error responses.
 * It uses @ControllerAdvice to apply exception handlers globally across all controllers.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ProductNotFoundException - Returns 404 Not Found.
     *
     * This exception is thrown when attempting to access a product that does not exist.
     *
     * @param ex the ProductNotFoundException
     * @param request the current web request
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex, WebRequest request) {

        log.warn("ProductNotFoundException occurred: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle DuplicateProductException - Returns 409 Conflict.
     *
     * This exception is thrown when attempting to create a product with
     * a duplicate code or other unique identifier.
     *
     * @param ex the DuplicateProductException
     * @param request the current web request
     * @return ResponseEntity with 409 status and error details
     */
    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(
            DuplicateProductException ex, WebRequest request) {

        log.warn("DuplicateProductException occurred: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle MethodArgumentNotValidException - Returns 400 Bad Request.
     *
     * This exception is thrown when request validation fails
     * (e.g., @Valid annotation on DTO fields fails).
     *
     * @param ex the MethodArgumentNotValidException
     * @param request the current web request
     * @return ResponseEntity with 400 status and validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Validation error occurred");

        String message = "Validation failed: " +
                ex.getBindingResult().getFieldErrors().size() +
                " field(s) failed validation";

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException - Returns 400 Bad Request.
     *
     * This exception is thrown when business rule validation fails
     * (e.g., price must be greater than 0).
     *
     * @param ex the IllegalArgumentException
     * @param request the current web request
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("IllegalArgumentException occurred: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle all generic exceptions - Returns 500 Internal Server Error.
     *
     * This is a catch-all handler for any unexpected exceptions
     * that are not explicitly handled by other methods.
     *
     * @param ex the generic Exception
     * @param request the current web request
     * @return ResponseEntity with 500 status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("An unexpected error occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred. Please try again later.")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
