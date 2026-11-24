package com.example.livestreambackend.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String email) {
        super("E-mailen er allerede i brug: " + email);
    }
}
