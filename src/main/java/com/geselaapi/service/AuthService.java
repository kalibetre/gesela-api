package com.geselaapi.service;

import com.geselaapi.dto.UserResponseDTO;
import com.geselaapi.model.AuthRequest;
import com.geselaapi.model.AuthResponse;
import com.geselaapi.dto.UserRequestDTO;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;
import com.geselaapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public AuthResponse register(UserRequestDTO userData) {
        User user = new User();
        user.setName(userData.getName());
        user.setEmail(userData.getEmail());
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setPhone(userData.getPhone());
        user.setRole(UserRole.DEFAULT);
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user.getUuid());
        return new AuthResponse(jwtToken, UserResponseDTO.from(user));
    }

    public AuthResponse login(AuthRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user.getUuid());
        return new AuthResponse(jwtToken, UserResponseDTO.from(user));
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
}
