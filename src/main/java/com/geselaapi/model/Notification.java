package com.geselaapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification extends BaseModel{
    @Column(nullable = false)
    private String message;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "from_user_uuid", nullable = false)
    private User fromUser;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "to_user_uuid", nullable = false)
    private User toUser;

    @ManyToOne
    private Issue issue;

    private LocalDateTime timeStamp;

    private boolean seen = false;

    @PrePersist
    public void prePersist() {
        this.timeStamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
