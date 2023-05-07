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
@RequestMapping("/issues")
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
        Issue issue = new Issue();
        issue.setTitle(issueRequestDTO.getTitle());
        issue.setDescription(issueRequestDTO.getDescription());
        issue.setUser(userService.getAuthenticatedUser());
        Issue savedIssue = issueRepository.save(issue);
        return ResponseEntity.ok(IssueResponseDTO.from(savedIssue));
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDTO>> getIssues() {
        User user = userService.getAuthenticatedUser();
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
            if (issueUpdate.getStatus() != null)
                issue.setStatus(issueUpdate.getStatus());

            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Issue with the specified id not found");
        } else if (issue.getUser().getUuid() == user.getUuid() || user.getRole() == UserRole.ADMIN)
        {
            issue.setStatus(IssueStatus.PENDING);
            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();
        else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole()))
        {
            issue.setArchived(true);
            issueRepository.save(issue);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<?> unarchiveIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();
        else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole()))
        {
            issue.setArchived(false);
            issueRepository.save(issue);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<IssueResponseDTO> assignIssue(@PathVariable UUID id, @RequestBody UUID employeeId) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        Employee handler = employeeRepository.findById(employeeId).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();
        else if (handler == null)
            return ResponseEntity.badRequest().build();
        else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole()))
        {
            issue.setHandler(handler);
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
        else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole()))
        {
            issueRepository.delete(issue);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
