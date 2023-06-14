package com.geselaapi.controller;

import com.geselaapi.dto.NewEmployeeRequestDTO;
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
import com.geselaapi.utils.ValidationError;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
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
    public ResponseEntity<?> addEmployee(@Valid @RequestBody NewEmployeeRequestDTO employee) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            if (userService.getUserByEmail(employee.getEmail()) != null
                    || userService.getUserByPhone(employee.getPhone()) != null) {
                ValidationError validationError = new ValidationError(
                        "Validation",
                        List.of("Email address or phone number already in use")
                );
                return ResponseEntity.badRequest().body(validationError);
            }

            UserRequestDTO userRequestDTO = new UserRequestDTO();
            userRequestDTO.setName(employee.getName());
            userRequestDTO.setEmail(employee.getEmail());
            userRequestDTO.setPhone(employee.getPhone());
            userRequestDTO.setPassword(employee.getPassword());
            authService.register(userRequestDTO);

            User newUser = userRepository.findByEmail(employee.getEmail()).orElse(null);
            Department dept = departmentRepository.findById(employee.getDepartmentUuid()).orElse(null);
            if (newUser == null || dept == null)
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();

            newUser.setRole(employee.getRole());
            Employee newEmployee = new Employee();
            newEmployee.setUserAccount(newUser);
            newEmployee.setDepartment(dept);
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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @Valid @RequestBody EmployeeUpdateDTO employee){
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Employee existingEmployee = employeeRepository.findById(id).orElse(null);
            if (existingEmployee == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with the specified id not found");

            User empUserAccount = existingEmployee.getUserAccount();

            if (employee.getName() != null)
                empUserAccount.setName(employee.getName());
            if (employee.getEmail() != null)
                empUserAccount.setEmail(employee.getEmail());
            if (employee.getPhone() != null)
                empUserAccount.setPhone(employee.getPhone());
            if (employee.getRole() != null)
                empUserAccount.setRole(employee.getRole());

            if (employee.getDepartmentUuid() != existingEmployee.getDepartment().getUuid()) {
                Department oldDept = existingEmployee.getDepartment();
                Department newDept = departmentRepository.findById(employee.getDepartmentUuid()).orElse(null);
                if (newDept == null)
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                oldDept.getEmployees().remove(existingEmployee);
                newDept.getEmployees().add(existingEmployee);
                existingEmployee.setDepartment(newDept);
                departmentRepository.save(oldDept);
                departmentRepository.save(newDept);
            } else {
                employeeRepository.save(existingEmployee);
            }

            EmployeeResponseDTO responseDTO = EmployeeResponseDTO.from(existingEmployee);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            Employee employee = employeeRepository.findById(id).orElse(null);
            if (employee == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with the specified id not found");

            if (!employee.getArchived()) {
                try {
                    Department dept = employee.getDepartment();
                    dept.getEmployees().remove(employee);
                    departmentRepository.save(dept);
                    employeeRepository.delete(employee);
                } catch (Exception e) {
                    employee.setArchived(true);
                    employeeRepository.save(employee);
                }
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
