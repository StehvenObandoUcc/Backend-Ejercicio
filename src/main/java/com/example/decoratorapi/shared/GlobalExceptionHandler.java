package com.example.decoratorapi.shared;

import com.example.decoratorapi.shared.exception.DuplicateResourceException;
import com.example.decoratorapi.shared.exception.InvalidDecoratorException;
import com.example.decoratorapi.shared.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI(), now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(InvalidDecoratorException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDecorator(InvalidDecoratorException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(400, "Bad Request", ex.getMessage(), request.getRequestURI(), now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(409, "Conflict", ex.getMessage(), request.getRequestURI(), now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = "Validation error";
        if (ex.getBindingResult().hasFieldErrors()) {
            message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        ErrorResponse err = new ErrorResponse(400, "Bad Request", message, request.getRequestURI(), now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred", request.getRequestURI(), now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
