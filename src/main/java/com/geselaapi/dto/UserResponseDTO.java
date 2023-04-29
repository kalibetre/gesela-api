package com.geselaapi.dto;

import com.geselaapi.model.User;

import java.util.UUID;

public class UserResponseDTO {
    private UUID uuid;
    private String name;
    private String email;
    private String phone;

    public static UserResponseDTO from(User user){
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUuid(user.getUuid());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setPhone(user.getPhone());
        return responseDTO;
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

}
