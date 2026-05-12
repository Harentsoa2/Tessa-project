package com.hei.school.tessaproject.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        this(message, ErrorCode.ACCESS_UNAUTHORIZED);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(HttpStatus.UNAUTHORIZED, message, errorCode);
    }
}
