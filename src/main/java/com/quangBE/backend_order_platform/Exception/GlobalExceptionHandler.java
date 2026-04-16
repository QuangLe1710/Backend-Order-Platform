package com.quangBE.backend_order_platform.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<String> handlingRuntimeException( RuntimeException exception) {
        log.info("RUNTIME EXCEPTION đã đi vào đây");
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

}
