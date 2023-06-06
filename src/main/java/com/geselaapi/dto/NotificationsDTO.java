package com.geselaapi.dto;

import com.geselaapi.model.Notification;

import java.time.LocalDateTime;

public class NotificationsDTO {
    private String message;
    private UserResponseDTO fromUser;
    private UserResponseDTO toUser;
    private LocalDateTime date;

    public static NotificationsDTO from(Notification notification) {
        NotificationsDTO notificationsDTO = new NotificationsDTO();
        notificationsDTO.setMessage(notification.getMessage());
        notificationsDTO.setFromUser(UserResponseDTO.from(notification.getFromUser()));
        notificationsDTO.setToUser(UserResponseDTO.from(notification.getToUser()));
        notificationsDTO.date = notification.getTimeStamp();
        return notificationsDTO;
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
}
