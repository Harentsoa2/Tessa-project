package com.hei.school.tessaproject.exception;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    ResponseEntity<Map<String, Object>> handleAppException(AppException exception) {
        return ResponseEntity.status(exception.getStatus()).body(Map.of(
                "message", exception.getMessage(),
                "errorCode", exception.getErrorCode().name()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::validationError)
                .toList();
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Validation failed",
                "errors", errors,
                "errorCode", ErrorCode.VALIDATION_ERROR.name()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", exception.getMessage(),
                "errorCode", ErrorCode.VALIDATION_ERROR.name()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Internal Server Error",
                "error", exception.getMessage() == null ? "Unknown error occurred" : exception.getMessage(),
                "errorCode", ErrorCode.INTERNAL_SERVER_ERROR.name()));
    }

    private Map<String, String> validationError(FieldError error) {
        return Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage());
    }
}
