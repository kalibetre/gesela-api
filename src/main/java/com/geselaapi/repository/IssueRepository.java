package com.geselaapi.repository;

import com.geselaapi.model.Customer;
import com.geselaapi.model.Employee;
import com.geselaapi.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findByCustomer(Customer customer);
    List<Issue> findByHandler(Employee handler);
}
