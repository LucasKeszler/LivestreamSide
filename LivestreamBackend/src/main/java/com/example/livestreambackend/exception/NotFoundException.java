package com.example.livestreambackend.exception;

/**
 * Thrown when something that was asked for by id (or email) does not exist.
 * A controller can turn this into a 404 page.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
