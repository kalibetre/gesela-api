package com.geselaapi.controller;

import com.geselaapi.dto.DepartmentRequestDTO;
import com.geselaapi.dto.DepartmentResponseDTO;
import com.geselaapi.dto.DepartmentUpdateDTO;
import com.geselaapi.model.Department;
import com.geselaapi.model.User;
import com.geselaapi.model.UserRole;
import com.geselaapi.repository.DepartmentRepository;
import com.geselaapi.service.UserService;
import com.geselaapi.utils.Converter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/departments")
@SecurityRequirement(name = "gesela-api")
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final UserService userService;

    public DepartmentController(DepartmentRepository departmentRepository, UserService userService) {
        this.departmentRepository = departmentRepository;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<?> addDepartment(@Valid @RequestBody DepartmentRequestDTO department) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Department newDepartment = new Department();
            newDepartment.setName(department.getName());
            newDepartment.setDescription(department.getDescription());
            departmentRepository.save(newDepartment);
            return ResponseEntity.ok(DepartmentResponseDTO.from(newDepartment));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping()
    public ResponseEntity<?> getDepartments() {
        User user = userService.getAuthenticatedUser();
        if (user != null) {
            List<Department> departments = departmentRepository.findAll();
            return ResponseEntity.ok(Converter.convertList(departments, DepartmentResponseDTO::from));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user != null) {
            Department department = departmentRepository.findById(id).orElse(null);
            if (department == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department with the specified id not found");
            return ResponseEntity.ok(DepartmentResponseDTO.from(department));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable UUID id, @Valid @RequestBody DepartmentUpdateDTO department) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Department existingDepartment = departmentRepository.findById(id).orElse(null);
            if (existingDepartment == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department with the specified id not found");

            if (department.getName() != null)
                existingDepartment.setName(department.getName());

            if (department.getDescription() != null)
                existingDepartment.setName(department.getDescription());

            departmentRepository.save(existingDepartment);
            return ResponseEntity.ok(DepartmentResponseDTO.from(existingDepartment));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Department department = departmentRepository.findById(id).orElse(null);
            if (department == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department with the specified id not found");
            try {
                departmentRepository.delete(department);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Failed to delete Department");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
