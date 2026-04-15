package com.quangBE.backend_order_platform.Service.ServiceImpl;

import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRegisterDto;
import com.quangBE.backend_order_platform.Dto.Request.AuthenticationRequest;
import com.quangBE.backend_order_platform.Entity.User;
import com.quangBE.backend_order_platform.Repository.UserRepository;
import com.quangBE.backend_order_platform.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private final UserRepository userRepository;

    @Override
    public void login(AuthenticationRequest authenticationRequest) {

        if ( authenticationRequest.getUsername().equals(userRepository.findByUserName(authenticationRequest.getUsername()).getUserName())) {
            if(
                    passwordEncoder.matches(
                            authenticationRequest.getPassword(),
                            userRepository.findByUserName(authenticationRequest.getUsername()).getPasswordHash()
                    )
            ) {
                log.info("Password is TRUE");
            } else {
                log.error("Password is FALSE");
            }
        } else {
            log.error("Username not exist");
        }
    }

    @Override
    public void register(AuthenticationRegisterDto request) {
        User user = User.builder()
                .userName(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        log.info("TẠO USER SUCCESSFULLY");
        userRepository.save(user);
    }

}
