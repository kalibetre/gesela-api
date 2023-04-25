package com.geselaapi.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee extends  BaseModel{
    @ManyToOne
    @JoinColumn(name = "department_uuid")
    private Department department;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private EmployeeRole role;

    @Column(nullable = false)
    private Boolean isArchived;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_uuid")
    private User userAccount;

    public Employee() {
        this.isArchived = false;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public User getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(User userAccount) {
        this.userAccount = userAccount;
    }

}
