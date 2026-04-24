package com.quangBE.backend_order_platform.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    private int code;


    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, int code) {
        super(message);
        this.code = code;
    }
}
