package com.geselaapi.dto;

import com.geselaapi.model.UserRole;

import java.util.UUID;

public class EmployeeUpdateDTO {
    private String name;
    private String email;
    private String phone;
    private UUID departmentUuid;
    private UserRole role;

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

    public UUID getDepartmentUuid() {
        return departmentUuid;
    }

    public void setDepartmentUuid(UUID departmentUuid) {
        this.departmentUuid = departmentUuid;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
