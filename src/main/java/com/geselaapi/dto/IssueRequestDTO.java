package com.geselaapi.dto;

import jakarta.validation.constraints.NotBlank;

public class IssueRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;
    private String description;

    private boolean submitted;

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

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
