package com.geselaapi.dto;

import com.geselaapi.model.Employee;

import java.util.UUID;

public class EmployeeResponseDTO {
    private UUID uuid;
    private String department;
    private UserResponseDTO userAccount;

    public static EmployeeResponseDTO from(Employee employee) {
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setUuid(employee.getUuid());
        responseDTO.setDepartment(employee.getDepartment().getName());
        responseDTO.setUserAccount(UserResponseDTO.from(employee.getUserAccount()));
        return responseDTO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public UserResponseDTO getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserResponseDTO userAccount) {
        this.userAccount = userAccount;
    }
}
