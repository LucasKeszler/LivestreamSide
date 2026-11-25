package com.example.livestreambackend.exception;

/**
 * Thrown when an account tries to buy something it already has access to,
 * for example a video it has bought before, or a video covered by its year pass.
 */
public class AlreadyPurchasedException extends RuntimeException {

    public AlreadyPurchasedException(String message) {
        super(message);
    }
}
