package com.quangBE.backend_order_platform;

import com.quangBE.backend_order_platform.Exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Slf4j
@SpringBootApplication
public class BackendOrderPlatformApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BackendOrderPlatformApplication.class, args);
        GlobalExceptionHandler globalExceptionHandler = context.getBean(GlobalExceptionHandler.class);
        System.out.println( " GlobalExceptionHandler " + globalExceptionHandler);
	}

}
