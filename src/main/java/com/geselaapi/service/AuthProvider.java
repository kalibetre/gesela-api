package com.geselaapi.service;

import com.geselaapi.service.UserService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthProvider extends DaoAuthenticationProvider {

    public AuthProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(userService);
        this.setPasswordEncoder(passwordEncoder);
    }
}
