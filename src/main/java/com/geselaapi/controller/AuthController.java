package com.geselaapi.controller;

import com.geselaapi.model.AuthRequest;
import com.geselaapi.model.AuthResponse;
import com.geselaapi.dto.UserRequestDTO;
import com.geselaapi.model.Customer;
import com.geselaapi.model.User;
import com.geselaapi.repository.CustomerRepository;
import com.geselaapi.service.AuthService;
import com.geselaapi.service.UserService;
import com.geselaapi.utils.ValidationError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final CustomerRepository customerRepository;

    public AuthController(AuthService authService,
                          UserService userService, CustomerRepository customerRepository) {
        this.authService = authService;
        this.userService = userService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody UserRequestDTO request
    ) {
        if (userService.getUserByEmail(request.getEmail()) != null
                || userService.getUserByPhone(request.getPhone()) != null) {
            ValidationError validationError = new ValidationError(
                    "Validation",
                    List.of("Email address or phone number already in use")
            );
            return ResponseEntity.badRequest().body(validationError);
        }

        AuthResponse response = authService.register(request);
        User user = userService.getUserByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        Customer customer = new Customer();
        customer.setUserAccount(user);
        customerRepository.save(customer);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
