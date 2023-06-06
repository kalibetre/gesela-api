package com.geselaapi.controller;

import com.geselaapi.dto.NotificationsDTO;
import com.geselaapi.dto.UserResponseDTO;
import com.geselaapi.dto.UserUpdateDTO;
import com.geselaapi.model.Issue;
import com.geselaapi.model.Notification;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;
import com.geselaapi.repository.IssueRepository;
import com.geselaapi.service.NotificationService;
import com.geselaapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final IssueRepository issueRepository;

    public UserController(UserService userService, NotificationService notificationService,
                          IssueRepository issueRepository) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.issueRepository = issueRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUser() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getUserRoles() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(UserRole.values());
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

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationsDTO>> getNotifications() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Notification> notifications = notificationService.getReceivedNotifications(user);
        return ResponseEntity.ok(notifications.stream().map(NotificationsDTO::from).toList());
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Notification notification = notificationService.getById(id);
        if (notification == null)
            return ResponseEntity.notFound().build();

        if (notification.getToUser().getUuid() != user.getUuid())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = notification.getIssue();
        issue.getNotifications().forEach(n -> {
            if (n.getToUser().getUuid() == user.getUuid() && notification.getUuid() == n.getUuid())
                n.setSeen(true);
        });
        issueRepository.save(issue);
        return ResponseEntity.ok().build();
    }
}
