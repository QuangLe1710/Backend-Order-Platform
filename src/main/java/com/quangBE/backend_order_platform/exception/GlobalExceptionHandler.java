package com.quangBE.backend_order_platform.exception;

import com.quangBE.backend_order_platform.controller.base.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    ResponseEntity<?> handlingResourceNotFoundException( ResourceNotFoundException ex) {
        log.info("ResourceNotFoundException handled: {}", ex.getMessage());
        return ResponseEntity
                .ok()
                .body(
                        ApiResponse.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = InvalidException.class)
    ResponseEntity<?> handlingInvalidException( InvalidException ex) {
        log.info("InvalidException handed : {}" , ex.getMessage());
        return ResponseEntity
                .ok()
                .body(
                        ApiResponse.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .build()
                );
    }

}
