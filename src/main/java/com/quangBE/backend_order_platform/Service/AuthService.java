package com.quangBE.backend_order_platform.Service;

import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRequest;

public interface AuthService {

    void login(AuthenticationRequest authenticationRequest);

    void register(AuthenticationRegisterDto request);

}
