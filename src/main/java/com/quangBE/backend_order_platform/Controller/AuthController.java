package com.quangBE.backend_order_platform.Controller;

import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public interface AuthController {

    @PostMapping("/register")
    public void  register(
            @RequestBody AuthenticationRegisterDto request
    );

    @PostMapping("/login")
    public void  login(
            @RequestBody AuthenticationRequest request
    );

    @PostMapping("/refresh")
    public void  refresh();

}
