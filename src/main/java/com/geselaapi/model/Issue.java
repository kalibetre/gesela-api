package com.geselaapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issue extends BaseModel{
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private IssueStatus status;

    private LocalDateTime createdDate;

    @Column(nullable = false)
    private Boolean isArchived;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User raisedBy;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "handler_uuid")
    private User handler;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    public User getHandler() {
        return handler;
    }

    public void setHandler(User handler) {
        this.handler = handler;
    }

    public Issue() {
        this.status = IssueStatus.DRAFT;
        this.isArchived = false;
    }

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

    public User getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(User raisedBy) {
        this.raisedBy = raisedBy;
    }

}
