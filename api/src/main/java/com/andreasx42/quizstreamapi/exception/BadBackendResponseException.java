package com.andreasx42.quizstreamapi.exception;

public class BadBackendResponseException extends RuntimeException {

    public BadBackendResponseException(String message, Class<?> entity) {
        super(String.format("Error in %s during backend call: %s",
                entity.getSimpleName()
                        .toLowerCase(), message));
    }

}
