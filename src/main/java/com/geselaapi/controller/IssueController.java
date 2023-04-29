package com.geselaapi.controller;

import com.geselaapi.dto.IssueRequestDTO;
import com.geselaapi.dto.IssueResponseDTO;
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
        issue.setRaisedBy(userService.getAuthenticatedUser());
        Issue savedIssue = issueRepository.save(issue);
        return ResponseEntity.ok(IssueResponseDTO.from(savedIssue));
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDTO>> getIssues() {
        User user = userService.getAuthenticatedUser();
        if (user.getRole() == UserRole.DEFAULT) {
            return ResponseEntity.ok(Converter.convertList(issueRepository.findByRaisedBy(user), IssueResponseDTO::from));
        }
        else if (user.getRole() == UserRole.ISSUE_HANDLER) {
            var raised = issueRepository.findByRaisedBy(user);
            raised.addAll(issueRepository.findByHandler(user));
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
           || (List.of(issue.getRaisedBy().getUuid(), issue.getHandler().getUuid()).contains(user.getUuid())))
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> updateIssue(@PathVariable UUID id, @RequestBody IssueRequestDTO issueRequestDTO) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        } else if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole())
                || (List.of(issue.getRaisedBy().getUuid(), issue.getHandler().getUuid()).contains(user.getUuid())))
        {
            issue.setTitle(issueRequestDTO.getTitle());
            issue.setDescription(issueRequestDTO.getDescription());
            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<IssueResponseDTO> updateIssueStatus(@PathVariable UUID id, @PathVariable IssueStatus status) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        } else if (List.of(UserRole.ISSUE_MANAGER, UserRole.ISSUE_HANDLER).contains(user.getRole())
                || (List.of(issue.getRaisedBy().getUuid(), issue.getHandler().getUuid()).contains(user.getUuid())))
        {
            issue.setStatus(status);
            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<IssueResponseDTO> submitIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        } else if (issue.getRaisedBy().getUuid() == user.getUuid())
        {
            issue.setStatus(IssueStatus.PENDING);
            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
            issue.setHandler(handler.getUserAccount());
            issueRepository.save(issue);
            return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
