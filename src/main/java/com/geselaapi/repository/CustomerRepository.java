package com.geselaapi.repository;

import com.geselaapi.model.Customer;
import com.geselaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByUserAccount(User userAccount);
}
