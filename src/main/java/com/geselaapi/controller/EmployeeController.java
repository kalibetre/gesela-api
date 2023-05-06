package com.geselaapi.controller;

import com.geselaapi.dto.EmployeeRequestDTO;
import com.geselaapi.dto.EmployeeResponseDTO;
import com.geselaapi.dto.EmployeeUpdateDTO;
import com.geselaapi.dto.UserRequestDTO;
import com.geselaapi.model.*;
import com.geselaapi.repository.DepartmentRepository;
import com.geselaapi.repository.EmployeeRepository;
import com.geselaapi.repository.UserRepository;
import com.geselaapi.service.AuthService;
import com.geselaapi.service.UserService;
import com.geselaapi.utils.Converter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@SecurityRequirement(name = "gesela-api")
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final DepartmentRepository departmentRepository;

    public EmployeeController(EmployeeRepository employeeRepository, UserService userService,
                              UserRepository userRepository, AuthService authService,
                              DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.authService = authService;
        this.departmentRepository = departmentRepository;
    }

    @PostMapping()
    public ResponseEntity<?> addEmployee(@Valid @RequestBody EmployeeRequestDTO employee) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            UserRequestDTO userRequestDTO = new UserRequestDTO();
            userRequestDTO.setName(employee.getName());
            userRequestDTO.setEmail(employee.getEmail());
            userRequestDTO.setPhone(employee.getPhone());
            userRequestDTO.setPassword(employee.getPassword());
            authService.register(userRequestDTO);

            User newUser = userRepository.findByEmail(employee.getEmail()).orElse(null);
            Department dept = departmentRepository.findByName(employee.getDepartment()).orElse(null);
            if (newUser == null || dept == null)
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();

            newUser.setRole(employee.getRole());
            Employee newEmployee = new Employee();
            newEmployee.setUserAccount(newUser);
            newEmployee.setDepartment(dept);
            newEmployee.setHireDate(employee.getHireDate());

            dept.getEmployees().add(newEmployee);
            departmentRepository.save(dept);

            EmployeeResponseDTO responseDTO = EmployeeResponseDTO.from(newEmployee);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping()
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        User user = userService.getAuthenticatedUser();
        if (user != null) {
            List<Employee> employees = employeeRepository
                    .findAll()
                    .stream()
                    .filter(employee -> !employee.getArchived())
                    .toList();
            return ResponseEntity.ok(Converter.convertList(employees, EmployeeResponseDTO::from));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user != null) {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with the specified id not found");
            return ResponseEntity.ok(EmployeeResponseDTO.from(employee));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @Valid @RequestBody EmployeeUpdateDTO employee){
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Employee existingEmployee = employeeRepository.findById(id).orElse(null);
            if (existingEmployee == null)
                return ResponseEntity.notFound().build();

            if (employee.getHireDate() != null)
                existingEmployee.setHireDate(employee.getHireDate());

            if (employee.getDepartment() != null &&!Objects.equals(existingEmployee.getDepartment().getName(), employee.getDepartment()))
            {
                Department dept = departmentRepository.findByName(employee.getDepartment()).orElse(null);
                if (dept == null)
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                existingEmployee.setDepartment(dept);
                dept.getEmployees().add(existingEmployee);
                departmentRepository.save(dept);
            } else {
                employeeRepository.save(existingEmployee);
            }

            EmployeeResponseDTO responseDTO = EmployeeResponseDTO.from(existingEmployee);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID id, @RequestBody UserRole role) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null)
                return ResponseEntity.notFound().build();
            else if (!employee.getArchived()) {
                try {
                    employeeRepository.delete(employee);
                } catch (Exception e) {
                    employee.setArchived(true);
                    employeeRepository.save(employee);
                }
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
