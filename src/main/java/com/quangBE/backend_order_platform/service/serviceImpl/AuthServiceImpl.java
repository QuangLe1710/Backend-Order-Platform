package com.quangBE.backend_order_platform.service.serviceImpl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.quangBE.backend_order_platform.controller.base.ApiResponse;
import com.quangBE.backend_order_platform.dto.request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.dto.request.AuthenticationLoginRequest;
import com.quangBE.backend_order_platform.dto.request.IntrospectRequest;
import com.quangBE.backend_order_platform.dto.response.AuthenticationResponse;
import com.quangBE.backend_order_platform.dto.response.IntrospectResponse;
import com.quangBE.backend_order_platform.entity.User;
import com.quangBE.backend_order_platform.exception.InvalidException;
import com.quangBE.backend_order_platform.exception.ResourceNotFoundException;
import com.quangBE.backend_order_platform.repository.UserRepository;
import com.quangBE.backend_order_platform.service.AuthService;
import com.quangBE.backend_order_platform.utils.constants.MessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SECRET_KEY;

    private final UserRepository userRepository;

    @Override
    public ApiResponse<?> login(AuthenticationLoginRequest authenticationLoginRequest) throws JOSEException {
        User user = userRepository.findByUserName(authenticationLoginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER.USERNAME_NOT_FOUND.message(),
                        MessageConstants.USER.USERNAME_NOT_FOUND.code()
                        ));

        boolean authenticated = passwordEncoder.matches(
                authenticationLoginRequest.getPassword(),
                user.getPasswordHash()
        );

        if ( authenticated )
        {
            log.info("Password is TRUE");
            var token = generateToken(user.getUserName());
            return ApiResponse.builder()
                    .code(MessageConstants.AUTH.LOGIN_SUCCESSFUL.code())
                    .message(MessageConstants.AUTH.LOGIN_SUCCESSFUL.message())
                    .data(
                            AuthenticationResponse.builder()
                                    .token(token)
                                    .authenticated(true)
                                    .build()
                    )
                    .build();
        }
        else
        {
            throw new InvalidException(
                    MessageConstants.USER.USERNAME_OR_PASSWORD_NOT_TRUE.code(),
                    MessageConstants.USER.USERNAME_OR_PASSWORD_NOT_TRUE.message()
            );
        }
    }

    @Override
    public ApiResponse<?> register(AuthenticationRegisterDto request) {
        User user = User.builder()
                .userName(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        log.info("TẠO USER SUCCESSFULLY");
        userRepository.save(user);
        return ApiResponse.builder()
                .code(MessageConstants.AUTH.LOGIN_SUCCESSFUL.code())
                .message(MessageConstants.AUTH.LOGIN_SUCCESSFUL.message())
                .build();
    }

    @Override
    public ApiResponse<IntrospectResponse> introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();

        JWSVerifier jwsVerifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if ( signedJWT.verify(jwsVerifier) && expiryTime.after(new Date()) ) {
            return ApiResponse.<IntrospectResponse>builder()
                    .code(MessageConstants.AUTH.INTROSPECT_SUCCESSFUL.code())
                    .message(MessageConstants.AUTH.INTROSPECT_SUCCESSFUL.message())
                    .data(IntrospectResponse.builder()
                            .valid(true)
                            .build())
                    .build();
        }

        return ApiResponse.<IntrospectResponse>builder()
                .code(MessageConstants.AUTH.INTROSPECT_FAIL.code())
                .message(MessageConstants.AUTH.INTROSPECT_FAIL.message())
                .data(IntrospectResponse.builder()
                        .valid(false)
                        .build())
                .build();

    }

    private String generateToken(String username) throws JOSEException {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                                        .subject(username)
                                        .issuer("quang.com")
                                        .issueTime(new Date())
                                        .expirationTime(new Date(
                                                Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                                        ))
                                        .claim("userId" , "USER001")
                                                                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(
                header,
                payload
        );

//        log.info("===>>>  WHEN UNSIGNED : " + jwsObject.serialize());

        jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));

        log.info("===>>>  WHEN SIGNED : " + jwsObject.serialize());

        return jwsObject.serialize();
    }

}
