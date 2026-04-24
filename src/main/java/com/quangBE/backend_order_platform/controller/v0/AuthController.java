package com.quangBE.backend_order_platform.controller.v0;

import com.nimbusds.jose.JOSEException;
import com.quangBE.backend_order_platform.dto.request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.dto.request.AuthenticationLoginRequest;
import com.quangBE.backend_order_platform.dto.request.IntrospectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;

public interface AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody AuthenticationRegisterDto request
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationLoginRequest request
    ) throws JOSEException;

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect (
            @RequestBody IntrospectRequest request
    ) throws ParseException, JOSEException;

    @PostMapping("/refresh")
    public void  refresh();

}
