package com.geselaapi.repository;

import com.geselaapi.model.Employee;
import com.geselaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUserAccount(User userAccount);
}
