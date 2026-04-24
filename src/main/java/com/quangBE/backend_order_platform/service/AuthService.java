package com.quangBE.backend_order_platform.service;

import com.nimbusds.jose.JOSEException;
import com.quangBE.backend_order_platform.controller.base.ApiResponse;
import com.quangBE.backend_order_platform.dto.request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.dto.request.AuthenticationLoginRequest;
import com.quangBE.backend_order_platform.dto.request.IntrospectRequest;
import com.quangBE.backend_order_platform.dto.response.IntrospectResponse;

import java.text.ParseException;

public interface AuthService {

    ApiResponse<?> login(AuthenticationLoginRequest authenticationLoginRequest) throws JOSEException;

    ApiResponse<?> register(AuthenticationRegisterDto request);

    ApiResponse<IntrospectResponse> introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

}
