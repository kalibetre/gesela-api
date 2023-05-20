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

import java.util.HashSet;
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

        List<Issue> ownIssues = issueRepository.findByUser(user);
        if (List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole())) {
            List<Issue> nonDraftIssues = issueRepository.findAll().stream()
                    .filter(issue -> issue.getStatus() != IssueStatus.DRAFT)
                    .toList();

            HashSet<Issue> issues = new HashSet<>(ownIssues);
            issues.addAll(nonDraftIssues);
            return ResponseEntity.ok(Converter.convertList(issues.stream().toList(), IssueResponseDTO::from));
        } else if (user.getRole() == UserRole.ISSUE_HANDLER) {
            var employee = employeeRepository.findByUserAccount(user).orElse(null);
            List<Issue> handlerIssues = issueRepository.findByHandler(employee);

            HashSet<Issue> issues = new HashSet<>(ownIssues);
            issues.addAll(handlerIssues);
            return ResponseEntity.ok(Converter.convertList(issues.stream().toList(), IssueResponseDTO::from));
        }
        return ResponseEntity.ok(Converter.convertList(ownIssues, IssueResponseDTO::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> getIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue != null) {
            boolean isUserOwner =  issue.getUser().getUuid() == user.getUuid();
            boolean isUserAdminOrHandler = List.of(UserRole.ADMIN, UserRole.ISSUE_MANAGER).contains(user.getRole());
            boolean isUserIssueHandler = user.getRole() == UserRole.ISSUE_HANDLER && issue.getHandler().getUuid() == user.getUuid();

            if (isUserOwner || isUserIssueHandler || (isUserAdminOrHandler && issue.getStatus() != IssueStatus.DRAFT))
                return ResponseEntity.ok(IssueResponseDTO.from(issue));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIssue(@PathVariable UUID id, @Valid @RequestBody IssueUpdateDTO issueUpdate) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Issue with the specified id not found");
        }

        boolean isUserOwner =  issue.getUser().getUuid() == user.getUuid();
        boolean isUserIssueManager = user.getRole() == UserRole.ISSUE_MANAGER;
        boolean isUserIssueHandler = user.getRole() == UserRole.ISSUE_HANDLER && issue.getHandler().getUserAccount().getUuid() == user.getUuid();
        boolean isDraft = issue.getStatus() == IssueStatus.DRAFT;
        boolean isComplete = issue.getStatus() == IssueStatus.CLOSED;

        if (isUserOwner && (isDraft || isComplete)) {
            if (issueUpdate.getTitle() != null)
                issue.setTitle(issueUpdate.getTitle());
            if (issueUpdate.getDescription() != null)
                issue.setDescription(issueUpdate.getDescription());
            if (issueUpdate.getStatus() != null && issueUpdate.getStatus() == IssueStatus.SUBMITTED)
                issue.setStatus(issueUpdate.getStatus());
            return ResponseEntity.ok(IssueResponseDTO.from(issueRepository.save(issue)));
        }

        if (isUserIssueHandler && !isDraft) {
            if (issueUpdate.getStatus() != null && List.of(IssueStatus.PENDING, IssueStatus.IN_PROGRESS, IssueStatus.CLOSED).contains(issueUpdate.getStatus())) {
                issue.setStatus(issueUpdate.getStatus());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status");
            }
            return ResponseEntity.ok(IssueResponseDTO.from(issueRepository.save(issue)));
        }

        if (isUserIssueManager && !isDraft) {
            if (issueUpdate.getHandlerId() != null) {
                Employee handler = employeeRepository.findById(issueUpdate.getHandlerId()).orElse(null);
                if (handler != null) {
                    issue.setHandler(handler);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid handler id");
                }
                return ResponseEntity.ok(IssueResponseDTO.from(issueRepository.save(issue)));
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();

        boolean isUserOwner =  issue.getUser().getUuid() == user.getUuid();

        if (isUserOwner && List.of(IssueStatus.DRAFT, IssueStatus.CLOSED).contains(issue.getStatus()))
        {
            try {
                issueRepository.delete(issue);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                return archiveIssue(issue.getUuid());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping("/{id}/archive")
    public ResponseEntity<?> archiveIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();

        boolean isUserOwner =  issue.getUser().getUuid() == user.getUuid();

        if (isUserOwner && List.of(IssueStatus.DRAFT, IssueStatus.CLOSED).contains(issue.getStatus()))
        {
            issue.setArchived(true);
            issueRepository.save(issue);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> unarchiveIssue(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null)
            return ResponseEntity.notFound().build();

        boolean isUserOwner =  issue.getUser().getUuid() == user.getUuid();

        if (isUserOwner && List.of(IssueStatus.DRAFT, IssueStatus.CLOSED).contains(issue.getStatus()))
        {
            issue.setArchived(false);
            issueRepository.save(issue);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }
}
