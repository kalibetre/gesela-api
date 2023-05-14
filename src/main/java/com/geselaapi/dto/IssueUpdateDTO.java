package com.geselaapi.dto;

import com.geselaapi.model.IssueStatus;

import java.util.UUID;

public class IssueUpdateDTO {
    private String title;
    private String description;
    private IssueStatus status;
    private UUID handlerId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public UUID getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(UUID handlerId) {
        this.handlerId = handlerId;
    }
}
