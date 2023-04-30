package com.geselaapi.repository;

import com.geselaapi.model.Employee;
import com.geselaapi.model.Issue;
import com.geselaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findByUser(User raisedBy);
    List<Issue> findByHandler(Employee handler);
}
