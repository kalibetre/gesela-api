package com.geselaapi.dto;

import com.geselaapi.model.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationsDTO {
    private UUID uuid;
    private String message;
    private UserResponseDTO fromUser;
    private UserResponseDTO toUser;
    private LocalDateTime date;
    private boolean seen;

    public static NotificationsDTO from(Notification notification) {
        NotificationsDTO notificationsDTO = new NotificationsDTO();
        notificationsDTO.setUuid(notification.getUuid());
        notificationsDTO.setMessage(notification.getMessage());
        notificationsDTO.setFromUser(UserResponseDTO.from(notification.getFromUser()));
        notificationsDTO.setToUser(UserResponseDTO.from(notification.getToUser()));
        notificationsDTO.setDate(notification.getTimeStamp());
        notificationsDTO.setSeen(notification.isSeen());
        return notificationsDTO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public UserResponseDTO getFromUser() {
        return fromUser;
    }

    public UserResponseDTO getToUser() {
        return toUser;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFromUser(UserResponseDTO fromUser) {
        this.fromUser = fromUser;
    }

    public void setToUser(UserResponseDTO toUser) {
        this.toUser = toUser;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
