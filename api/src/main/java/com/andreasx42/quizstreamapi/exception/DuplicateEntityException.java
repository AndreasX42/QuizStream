package com.andreasx42.quizstreamapi.exception;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String fieldName, String fieldValue, Class<?> entity) {
        super(String.format("The %s with %s '%s' does already exist.",
                entity.getSimpleName()
                        .toLowerCase(), fieldName, fieldValue));
    }

}
