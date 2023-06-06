package com.geselaapi.repository;

import com.geselaapi.model.Issue;
import com.geselaapi.model.Notification;
import com.geselaapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByToUser(User toUser);
}
