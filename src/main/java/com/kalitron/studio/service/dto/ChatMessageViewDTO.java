package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.MessageRole;
import java.io.Serializable;
import java.time.Instant;

public class ChatMessageViewDTO implements Serializable {

    private MessageRole role;

    private String content;

    private Instant createdAt;

    private String imagePreviewUrl;

    private String imageFileName;

    private String imageBadge;

    public ChatMessageViewDTO() {}

    public ChatMessageViewDTO(MessageRole role, String content, Instant createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public ChatMessageViewDTO(
        MessageRole role,
        String content,
        Instant createdAt,
        String imagePreviewUrl,
        String imageFileName,
        String imageBadge
    ) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
        this.imagePreviewUrl = imagePreviewUrl;
        this.imageFileName = imageFileName;
        this.imageBadge = imageBadge;
    }

    public MessageRole getRole() {
        return role;
    }

    public void setRole(MessageRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagePreviewUrl() {
        return imagePreviewUrl;
    }

    public void setImagePreviewUrl(String imagePreviewUrl) {
        this.imagePreviewUrl = imagePreviewUrl;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImageBadge() {
        return imageBadge;
    }

    public void setImageBadge(String imageBadge) {
        this.imageBadge = imageBadge;
    }
}
