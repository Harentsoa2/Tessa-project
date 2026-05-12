package com.hei.school.tessaproject.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        this(message, ErrorCode.VALIDATION_ERROR);
    }

    public BadRequestException(String message, ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }
}
