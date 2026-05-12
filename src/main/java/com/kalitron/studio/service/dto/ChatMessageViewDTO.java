package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.MessageRole;
import java.io.Serializable;
import java.time.Instant;

public class ChatMessageViewDTO implements Serializable {

    private MessageRole role;

    private String content;

    private Instant createdAt;

    public ChatMessageViewDTO() {}

    public ChatMessageViewDTO(MessageRole role, String content, Instant createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
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
}
