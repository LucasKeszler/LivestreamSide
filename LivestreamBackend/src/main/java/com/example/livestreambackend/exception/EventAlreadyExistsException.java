package com.example.livestreambackend.exception;

public class EventAlreadyExistsException extends RuntimeException {

    /** Hvilket felt der er i konflikt, så controlleren kan vise fejlen ved det rigtige felt. */
    public enum Field { YEAR, TITLE }

    private final Field field;

    public EventAlreadyExistsException(Field field, String message) {
        super(message);
        this.field = field;
    }

    public Field getField() {
        return field;
    }
}
