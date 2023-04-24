package com.geselaapi.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "issues")
public class Issue extends BaseModel{
    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private IssueStatus status;

    @CreatedDate
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private Boolean isArchived;

    @ManyToOne
    @JoinColumn(name = "customer_uuid")
    private Customer customer;

    private List<Notification> notifications;

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
