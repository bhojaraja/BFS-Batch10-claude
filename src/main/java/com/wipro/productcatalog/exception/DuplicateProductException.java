package com.wipro.productcatalog.exception;

/**
 * DuplicateProductException - Exception thrown when attempting to create a duplicate product.
 *
 * This exception is thrown when attempting to create a product with a code, name,
 * or other unique identifier that already exists in the database.
 */
public class DuplicateProductException extends RuntimeException {

    /**
     * Constructor with error message.
     *
     * @param message the error message describing the duplicate product information
     */
    public DuplicateProductException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause.
     *
     * @param message the error message describing the duplicate product information
     * @param cause the root cause of this exception
     */
    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
