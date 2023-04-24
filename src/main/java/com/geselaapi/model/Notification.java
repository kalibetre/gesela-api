package com.geselaapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification extends BaseModel{
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private User fromUser;
    @Column(nullable = false)
    private User toUser;

    @ManyToOne
    @JoinColumn(name = "issue_uuid")
    private Issue issue;

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
}
