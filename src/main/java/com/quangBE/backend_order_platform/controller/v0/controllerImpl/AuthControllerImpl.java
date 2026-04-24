package com.quangBE.backend_order_platform.controller.v0.controllerImpl;

import com.nimbusds.jose.JOSEException;
import com.quangBE.backend_order_platform.controller.v0.AuthController;
import com.quangBE.backend_order_platform.dto.request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.dto.request.AuthenticationLoginRequest;
import com.quangBE.backend_order_platform.dto.request.IntrospectRequest;
import com.quangBE.backend_order_platform.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<?> register(AuthenticationRegisterDto request) {
        return ResponseEntity
                .ok()
                .body(authService.register(request));
    }

    @Override
    public ResponseEntity<?> login(AuthenticationLoginRequest authenticationLoginRequest) throws JOSEException {
        return ResponseEntity
                .ok()
                .body(authService.login(authenticationLoginRequest));
    }

    @Override
    public ResponseEntity<?> introspect(IntrospectRequest request) throws ParseException, JOSEException {
        return ResponseEntity
                .ok()
                .body(authService.introspect(request));
    }

    @Override
    public void refresh() {

    }
}
