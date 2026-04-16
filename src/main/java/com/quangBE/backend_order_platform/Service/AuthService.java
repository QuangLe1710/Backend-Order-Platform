package com.quangBE.backend_order_platform.Service;

import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    String login(AuthenticationRequest authenticationRequest);

    void register(AuthenticationRegisterDto request);

}
