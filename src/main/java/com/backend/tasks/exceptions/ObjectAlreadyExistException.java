package com.backend.tasks.exceptions;

public class ObjectAlreadyExistException extends RuntimeException {

    public ObjectAlreadyExistException(String entityName, String identity) {
        super(entityName + " [" + identity + "] already exist");
    }
}
