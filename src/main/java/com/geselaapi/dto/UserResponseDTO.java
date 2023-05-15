package com.geselaapi.dto;

import com.geselaapi.model.Employee;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;

import java.util.UUID;

public class UserResponseDTO {
    private UUID uuid;
    private String name;
    private String email;
    private String phone;

    private UserRole role;

    public static UserResponseDTO from(User user){
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUuid(user.getUuid());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setRole(user.getRole());
        return responseDTO;
    }

    public static UserResponseDTO from(Employee employee) {
        return from(employee.getUserAccount());
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
