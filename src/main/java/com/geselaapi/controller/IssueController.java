package com.geselaapi.controller;

import com.geselaapi.dto.IssueRequestDTO;
import com.geselaapi.dto.IssueResponseDTO;
import com.geselaapi.dto.IssueUpdateDTO;
import com.geselaapi.model.*;
import com.geselaapi.repository.EmployeeRepository;
import com.geselaapi.repository.IssueRepository;
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
@RequestMapping("/api/v1/issues")
@SecurityRequirement(name = "gesela-api")
public class IssueController {
    private final IssueRepository issueRepository;
    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    public IssueController(IssueRepository issueRepository,
                           EmployeeRepository employeeRepository,
                           UserService userService) {
        this.issueRepository = issueRepository;
        this.employeeRepository = employeeRepository;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<IssueResponseDTO> createIssue(@Valid @RequestBody IssueRequestDTO issueRequestDTO) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = new Issue();
        issue.setTitle(issueRequestDTO.getTitle());

        String description = issueRequestDTO.getDescription() != null ? issueRequestDTO.getDescription() : "";
        issue.setDescription(description);
        issue.setUser(user);

        if (issueRequestDTO.isSubmitted())
            issue.setStatus(IssueStatus.SUBMITTED);

        Issue savedIssue = issueRepository.save(issue);
        return ResponseEntity.ok(IssueResponseDTO.from(savedIssue));
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDTO>> getIssues() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (user.getRole() == UserRole.DEFAULT) {
            return ResponseEntity.ok(Converter.convertList(issueRepository.findByUser(user), IssueResponseDTO::from));
        }
        else if (user.getRole() == UserRole.ISSUE_HANDLER) {
            var raised = issueRepository.findByUser(user);
            var employee = employeeRepository.findByUserAccount(user).orElse(null);
            raised.addAll(issueRepository.findByHandler(employee));
            return ResponseEntity.ok(Converter.convertList(raised, IssueResponseDTO::from));
        }
        return ResponseEntity.ok(Converter.convertList(issueRepository.findAll(), IssueResponseDTO::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> getIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        } else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole())
           || (List.of(issue.getUser().getUuid(), issue.getHandler().getUuid()).contains(user.getUuid())))
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIssue(@PathVariable UUID id, @Valid @RequestBody IssueUpdateDTO issueUpdate) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Issue with the specified id not found");
        }

        boolean canUpdate = false;
        if (issue.getStatus() == IssueStatus.DRAFT && issue.getUser().getUuid() == user.getUuid())
            canUpdate = true;
        else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole()))
            canUpdate = true;
        else if (issue.getHandler().getUuid() == user.getUuid())
            canUpdate = true;

        if (canUpdate)
        {
            if (issueUpdate.getTitle() != null)
                issue.setTitle(issueUpdate.getTitle());
            if (issueUpdate.getDescription() != null)
                issue.setDescription(issueUpdate.getDescription());
            if (issueUpdate.getStatus() != null) {
                if (user.getRole() == UserRole.DEFAULT) {
                    if (issueUpdate.getStatus() == IssueStatus.SUBMITTED) {
                        if (issue.getUser().getUuid() == user.getUuid()) {
                            issue.setStatus(issueUpdate.getStatus());
                        } else {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                } else if (issue.getStatus() == IssueStatus.ARCHIVED) {
                    if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole())) {
                        issue.setStatus(issueUpdate.getStatus());
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                } else {
                    issue.setStatus(issueUpdate.getStatus());
                }
            }
            if (issueUpdate.getHandlerId() != null) {
                if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole())) {
                    Employee handler = employeeRepository.findById(issueUpdate.getHandlerId()).orElse(null);
                    if (handler == null)
                        return ResponseEntity.badRequest().build();
                    issue.setHandler(handler);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();
        else {
            try {
                if (issue.getUser().getUuid() == user.getUuid()
                        || List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole())) {
                    if (issue.getStatus() != IssueStatus.ARCHIVED) {
                        issue.setStatus(IssueStatus.ARCHIVED);
                        issueRepository.save(issue);
                    }
                    else {
                        issueRepository.delete(issue);
                    }
                    return ResponseEntity.noContent().build();
                }
            } catch (NullPointerException e) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Unable to delete issue");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
