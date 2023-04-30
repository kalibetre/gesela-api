package com.geselaapi.dto;

import com.geselaapi.model.Issue;
import com.geselaapi.model.IssueStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class IssueResponseDTO extends IssueRequestDTO {
    private UUID uuid;
    private IssueStatus status;
    private LocalDateTime createdDate;
    private Boolean isArchived;
    private UserResponseDTO raisedBy;
    private UserResponseDTO handler;

    public static IssueResponseDTO from(Issue issue) {
        IssueResponseDTO responseDTO = new IssueResponseDTO();
        responseDTO.setUuid(issue.getUuid());
        responseDTO.setTitle(issue.getTitle());
        responseDTO.setDescription(issue.getDescription());
        responseDTO.setStatus(issue.getStatus());
        responseDTO.setCreatedDate(issue.getCreatedDate());
        responseDTO.setArchived(issue.getArchived());
        responseDTO.setRaisedBy(UserResponseDTO.from(issue.getUser()));
        responseDTO.setHandler(UserResponseDTO.from(issue.getHandler()));
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

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
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
}
