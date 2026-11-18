package com.freightpulse.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.freightpulse.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }

        log.warn("Validation failed: {}", details);
        ErrorResponse response = new ErrorResponse("Validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleReadableException(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request body";
        List<String> details = new ArrayList<>();

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String allowed = Arrays.toString(ife.getTargetType().getEnumConstants());
                message = "Invalid value '" + ife.getValue() + "' for field. Allowed values: " + allowed;
            } else {
                message = "Invalid value format: " + ife.getValue();
            }
            details.add(message);
        } else {
            details.add(ex.getMostSpecificCause().getMessage());
        }

        log.warn("Unreadable request: {}", message);
        ErrorResponse response = new ErrorResponse(message, details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DispatchException.class)
    public ResponseEntity<ErrorResponse> handleDispatchException(DispatchException ex) {
        log.warn("Dispatch error: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Internal server error", ex);
        ErrorResponse response = new ErrorResponse("An unexpected error occurred", List.of(ex.getMessage() != null ? ex.getMessage() : "Unknown error"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
