package com.quangBE.backend_order_platform.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidException extends RuntimeException{

    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    private int code;

    public InvalidException(String message) {
        super(message);
    }

    public InvalidException(int code, String message) {
        super(message);
        this.code = code;
    }

}
