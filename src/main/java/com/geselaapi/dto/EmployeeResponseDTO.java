package com.geselaapi.dto;

import com.geselaapi.model.Employee;

import java.util.UUID;

public class EmployeeResponseDTO extends UserResponseDTO {
    private UUID uuid;
    private DepartmentResponseDTO department;

    public static EmployeeResponseDTO from(Employee employee) {
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setUuid(employee.getUuid());
        responseDTO.setDepartment(DepartmentResponseDTO.from(employee.getDepartment()));
        responseDTO.setName(employee.getUserAccount().getName());
        responseDTO.setEmail(employee.getUserAccount().getEmail());
        responseDTO.setPhone(employee.getUserAccount().getPhone());
        responseDTO.setRole(employee.getUserAccount().getRole());
        return responseDTO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public DepartmentResponseDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentResponseDTO department) {
        this.department = department;
    }
}
