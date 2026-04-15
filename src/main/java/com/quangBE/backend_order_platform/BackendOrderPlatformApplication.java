package com.quangBE.backend_order_platform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class BackendOrderPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendOrderPlatformApplication.class, args);
	}

}
