package com.personel.auth.server.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    private final String message;
    private final String field;

    public ResourceAlreadyExistsException(String message, String field) {
        super(message);
        this.field = field;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }
}
