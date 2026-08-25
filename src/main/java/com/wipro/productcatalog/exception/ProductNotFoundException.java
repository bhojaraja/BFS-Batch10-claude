package com.wipro.productcatalog.exception;

/**
 * ProductNotFoundException - Exception thrown when a product is not found.
 *
 * This exception is thrown when attempting to retrieve, update, or delete
 * a product that does not exist in the database.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Constructor with error message.
     *
     * @param message the error message describing why the product was not found
     */
    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause.
     *
     * @param message the error message describing why the product was not found
     * @param cause the root cause of this exception
     */
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
