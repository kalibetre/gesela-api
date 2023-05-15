package com.geselaapi.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee extends  BaseModel {
    @ManyToOne
    private Department department;

    @Column(nullable = false)
    private Boolean isArchived;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_uuid")
    private User userAccount;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = false)
    private final List<Issue> assignedIssues = new ArrayList<>();

    public Employee() {
        this.isArchived = false;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public List<Issue> getAssignedIssues() {
        return assignedIssues;
    }
}
