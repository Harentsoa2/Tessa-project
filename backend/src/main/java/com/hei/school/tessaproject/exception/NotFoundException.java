package com.hei.school.tessaproject.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}
