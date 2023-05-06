package com.geselaapi.controller;

import com.geselaapi.dto.UserResponseDTO;
import com.geselaapi.dto.UserUpdateDTO;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;
import com.geselaapi.service.AuthService;
import com.geselaapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserUpdateDTO userUpdate) {
        User user = userService.getAuthenticatedUser();
        User userToUpdate = userService.getUserByUuid(id);

        if (user == null || userToUpdate == null) {
            return ResponseEntity.notFound().build();
        } else if (user.getRole() != UserRole.ADMIN || user.getUuid() != id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (userUpdate.getName() != null)
            userToUpdate.setName(userUpdate.getName());
        if (userUpdate.getEmail() != null)
            userToUpdate.setName(userUpdate.getEmail());
        if (userUpdate.getPhone() != null)
            userToUpdate.setName(userUpdate.getPhone());
        if (userUpdate.getPassword() != null) {
            authService.changePassword(userToUpdate.getEmail(), userUpdate.getPassword());
        }
        userService.save(userToUpdate);
        return ResponseEntity.ok(UserResponseDTO.from(userToUpdate));
    }

    @PostMapping("/{id}/change-role")
    public ResponseEntity<?> updateUserRole(@PathVariable UUID id, @RequestBody UserRole role) {
        User user = userService.getAuthenticatedUser();
        User userToUpdate = userService.getUserByUuid(id);

        if (user == null || userToUpdate == null) {
            return ResponseEntity.notFound().build();
        } else if (user.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userToUpdate.setRole(role);
        userService.save(userToUpdate);
        return ResponseEntity.ok(UserResponseDTO.from(userToUpdate));
    }
}
