package com.geselaapi.dto;

import com.geselaapi.model.Issue;
import com.geselaapi.model.IssueStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IssueResponseDTO extends IssueRequestDTO {
    private UUID uuid;
    private IssueStatus status;
    private LocalDateTime createdDate;
    private UserResponseDTO raisedBy;
    private UserResponseDTO handler;
    private boolean archived;

    private List<NotificationsDTO> notifications;

    public static IssueResponseDTO from(Issue issue) {
        IssueResponseDTO responseDTO = new IssueResponseDTO();
        responseDTO.setUuid(issue.getUuid());
        responseDTO.setTitle(issue.getTitle());
        responseDTO.setDescription(issue.getDescription());
        responseDTO.setStatus(issue.getStatus());
        responseDTO.setCreatedDate(issue.getCreatedDate());
        responseDTO.setRaisedBy(UserResponseDTO.from(issue.getUser()));
        responseDTO.setArchived(issue.isArchived());
        if (issue.getHandler() != null)
            responseDTO.setHandler(UserResponseDTO.from(issue.getHandler()));
        responseDTO.notifications = issue.getNotifications().stream().map(NotificationsDTO::from).collect(Collectors.toList());
        return responseDTO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public UserResponseDTO getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(UserResponseDTO raisedBy) {
        this.raisedBy = raisedBy;
    }

    public UserResponseDTO getHandler() {
        return handler;
    }

    public void setHandler(UserResponseDTO handler) {
        this.handler = handler;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<NotificationsDTO> getNotifications() {
        return notifications;
    }
}
