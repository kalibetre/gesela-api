package com.geselaapi.dto;

import com.geselaapi.model.UserRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class NewEmployeeRequestDTO extends UserRequestDTO {
    @NotNull(message = "Department is mandatory")
    private UUID departmentUuid;
    @NotNull(message = "Role is mandatory")
    private UserRole role;

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
