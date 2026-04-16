package com.quangBE.backend_order_platform.Controller.ControllerImpl;

import com.quangBE.backend_order_platform.Controller.AuthController;
import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRequest;
import com.quangBE.backend_order_platform.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public void register(AuthenticationRegisterDto request) {
        authService.register(request);
    }

    @Override
    public ResponseEntity<String> login(AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authService.login(authenticationRequest));
    }

    @Override
    public void refresh() {

    }
}
