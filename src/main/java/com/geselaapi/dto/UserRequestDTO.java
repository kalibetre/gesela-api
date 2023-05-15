package com.geselaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @Email(message = "Invalid Email")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Phone is mandatory")
    private String phone;
    @NotBlank(message = "Password is mandatory")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
