package com.geselaapi.controller;

import com.geselaapi.dto.CustomerResponseDTO;
import com.geselaapi.dto.CustomerUpdateDTO;
import com.geselaapi.model.AccountStatus;
import com.geselaapi.model.Customer;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;
import com.geselaapi.repository.CustomerRepository;
import com.geselaapi.service.UserService;
import com.geselaapi.utils.Converter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "gesela-api")
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    public CustomerController(CustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<?> getCustomers() {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            List<Customer> customers = customerRepository.findAll();
            return ResponseEntity.ok(Converter.convertList(customers, CustomerResponseDTO::from));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user != null && (user.getUuid() == id || user.getRole() == UserRole.ADMIN)) {
            Customer customer = customerRepository.findById(id).orElse(null);
            if (customer == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with the specified id not found");
            return ResponseEntity.ok(CustomerResponseDTO.from(customer));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID id, @RequestBody CustomerUpdateDTO customer) {
        User user = userService.getAuthenticatedUser();
        if (user != null && (user.getUuid() == id || user.getRole() == UserRole.ADMIN)) {
            Customer existingCustomer = customerRepository.findById(id).orElse(null);
            if (existingCustomer == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with the specified id not found");

            User existingUser = existingCustomer.getUserAccount();
            if (customer.getName() != null)
                existingUser.setName(customer.getName());
            if (customer.getEmail() != null)
                existingUser.setEmail(customer.getEmail());
            if (customer.getPhone() != null)
                existingUser.setPhone(customer.getPhone());

            if (customer.getAccountStatus() != null && user.getRole() == UserRole.ADMIN)
                existingCustomer.setAccountStatus(customer.getAccountStatus());

            customerRepository.save(existingCustomer);
            return ResponseEntity.ok(CustomerResponseDTO.from(existingCustomer));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user != null && (user.getUuid() == id || user.getRole() == UserRole.ADMIN)) {
            Customer customer = customerRepository.findById(id).orElse(null);
            if (customer == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with the specified id not found");

            if (customer.getAccountStatus() != AccountStatus.ARCHIVED) {
                try {
                    customerRepository.delete(customer);
                } catch (Exception e) {
                    customer.setAccountStatus(AccountStatus.ARCHIVED);
                    customerRepository.save(customer);
                }
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
