package com.backend.tasks.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String entityName, Long identity) {
        super(entityName + "[" + identity + "] is not found");
    }
}
