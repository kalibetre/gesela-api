package com.geselaapi.service;

import com.geselaapi.dto.UserUpdateDTO;
import com.geselaapi.model.User;
import com.geselaapi.repository.UserRepository;
import com.geselaapi.utils.ValidationError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByUuid(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public ValidationError updateUserProfile(UUID userId, UserUpdateDTO userUpdate) {
        List<String> errors = new ArrayList<>();

        try {
            User userToUpdate = getUserByUuid(userId);

            if (userUpdate.getName() != null) {
                if (Objects.equals(userUpdate.getName(), ""))
                    errors.add("User name can not be empty");
                else
                    userToUpdate.setName(userUpdate.getName());
            }

            if (userUpdate.getEmail() != null) {
                User existingUser = getUserByEmail(userUpdate.getEmail());
                if (existingUser != null && existingUser.getUuid() != userToUpdate.getUuid())
                    errors.add("Email address already in use");
                else
                    userToUpdate.setEmail(userUpdate.getEmail());
            }

            if (userUpdate.getPhone() != null) {
                User existingUser = getUserByPhone(userUpdate.getPhone());
                if (existingUser != null && existingUser.getUuid() != userToUpdate.getUuid())
                    errors.add("Phone number address already in use");
                else
                    userToUpdate.setPhone(userUpdate.getPhone());
            }

            if (errors.size() > 0) {
                return new ValidationError(
                        "Validation",
                        errors
                );
            }
            save(userToUpdate);
            return null;
        } catch (Exception e) {
            errors.add("Profile update failed");
            return new ValidationError(
                    "Validation",
                    errors
            );
        }
    }
}
