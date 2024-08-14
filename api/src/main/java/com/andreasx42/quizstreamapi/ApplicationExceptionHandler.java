package com.andreasx42.quizstreamapi;

import com.andreasx42.quizstreamapi.exception.DuplicateEntityException;
import com.andreasx42.quizstreamapi.exception.EntityNotFoundException;
import com.andreasx42.quizstreamapi.exception.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return handleErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateEntityException ex, WebRequest request) {
        return handleErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        return handleErrorResponse("Data Integrity Violation: we cannot process your request.",
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleDeniedAccess(AccessDeniedException ex, WebRequest request) {
        return handleErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }


    /*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      org.springframework.http.HttpHeaders headers,
                                                                      HttpStatus status,
                                                                      WebRequest request) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + " - " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return handleErrorResponse(errorMessage, HttpStatus.BAD_REQUEST, request);
    }*/

    private ResponseEntity<ErrorResponse> handleErrorResponse(String message, HttpStatus status, WebRequest request) {
        String requestURI = ((ServletWebRequest) request).getRequest()
                .getRequestURI();

        ErrorResponse response = new ErrorResponse(requestURI, List.of(message), status.value(), LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }

}
