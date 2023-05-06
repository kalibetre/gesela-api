package com.geselaapi.dto;

import java.time.LocalDate;

public class EmployeeUpdateDTO {
    private String department;
    private LocalDate hireDate;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

}
