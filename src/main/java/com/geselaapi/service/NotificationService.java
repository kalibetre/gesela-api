package com.geselaapi.service;

import com.geselaapi.model.Issue;
import com.geselaapi.model.Notification;
import com.geselaapi.model.User;
import com.geselaapi.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void issueCreated(Issue issue, User user) {
        Notification notification = new Notification();
        notification.setMessage(issue.getTitle() + " issue has been created");
        notification.setFromUser(user);
        notification.setToUser(issue.getUser());
        notification.setIssue(issue);
        issue.getNotifications().add(notification);
    }

    public void issueArchived(Issue issue, User user) {
        Notification notification = new Notification();
        notification.setMessage(issue.getTitle() + " issue has been archived");
        notification.setFromUser(user);
        notification.setToUser(issue.getUser());
        notification.setIssue(issue);
        issue.getNotifications().add(notification);
    }

    public void issueUnArchived(Issue issue, User user) {
        Notification notification = new Notification();
        notification.setMessage(issue.getTitle() + " issue has been unarchived");
        notification.setFromUser(user);
        notification.setToUser(issue.getUser());
        notification.setIssue(issue);
        issue.getNotifications().add(notification);
    }

    public void issueStatusChanged(Issue issue, User user) {
        Notification notification = new Notification();
        notification.setMessage(issue.getTitle() + " issue has been changed to " + issue.getStatus().name().replace('_', ' '));
        notification.setFromUser(user);
        notification.setToUser(issue.getUser());
        notification.setIssue(issue);
        issue.getNotifications().add(notification);
    }

    public void issueAssigned(Issue issue, User user) {
        Notification notification = new Notification();
        notification.setMessage(
                issue.getTitle() + " issue has been assigned to " + issue.getHandler().getUserAccount().getName());
        notification.setFromUser(user);
        notification.setToUser(issue.getUser());
        notification.setIssue(issue);
        issue.getNotifications().add(notification);
    }

    public List<Notification> getByIssue(Issue issue, User user) {
        return notificationRepository.findByIssue(issue);
    }
}
