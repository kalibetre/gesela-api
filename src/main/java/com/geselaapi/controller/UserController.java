package com.geselaapi.controller;

import com.geselaapi.dto.UserResponseDTO;
import com.geselaapi.dto.UserUpdateDTO;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;
import com.geselaapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUser() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdate) {
        User user = userService.getAuthenticatedUser();
        try {
            if (userUpdate.getName() != null)
                user.setName(userUpdate.getName());
            if (userUpdate.getEmail() != null)
                user.setEmail(userUpdate.getEmail());
            if (userUpdate.getPhone() != null)
                user.setPhone(userUpdate.getPhone());
            userService.save(user);
            return ResponseEntity.ok(UserResponseDTO.from(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserUpdateDTO userUpdate) {
        User user = userService.getAuthenticatedUser();
        User userToUpdate = userService.getUserByUuid(id);
        try {
            if (user == null || userToUpdate == null) {
                return ResponseEntity.notFound().build();
            } else if (user.getRole() != UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (userUpdate.getName() != null)
                userToUpdate.setName(userUpdate.getName());
            if (userUpdate.getEmail() != null)
                userToUpdate.setEmail(userUpdate.getEmail());
            if (userUpdate.getPhone() != null)
                userToUpdate.setPhone(userUpdate.getPhone());
            userService.save(userToUpdate);
            return ResponseEntity.ok(UserResponseDTO.from(userToUpdate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/change-role")
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
