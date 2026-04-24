package com.quangBE.backend_order_platform;

import com.nimbusds.jose.proc.SecurityContext;
import com.quangBE.backend_order_platform.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.utils.SpringSecurityUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
public class BackendOrderPlatformApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BackendOrderPlatformApplication.class, args);
        GlobalExceptionHandler globalExceptionHandler = context.getBean(GlobalExceptionHandler.class);
        System.out.println( " GlobalExceptionHandler " + globalExceptionHandler);
	}

}
