package com.geselaapi.dto;

import com.geselaapi.model.Department;
import com.geselaapi.utils.Converter;

import java.util.ArrayList;
import java.util.List;

public class DepartmentResponseDTO {
    private String name;
    private String description;

    private final List<EmployeeResponseDTO> employees = new ArrayList<>();

    public static DepartmentResponseDTO from(Department department) {
        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setName(department.getName());
        responseDTO.setDescription(department.getDescription());
        List<EmployeeResponseDTO> employees = Converter.convertList(department.getEmployees(), EmployeeResponseDTO::from);
        employees.forEach(employee -> responseDTO.getEmployees().add(employee));
        return responseDTO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EmployeeResponseDTO> getEmployees() {
        return employees;
    }
}
