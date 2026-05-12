package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatSessionDTO implements Serializable {

    private Long sessionId;

    private String sessionCode;

    private String clientName;

    private String clientEmail;

    private ProjectType projectType;

    private SessionStatus status;

    private List<ChatMessageViewDTO> messages = new ArrayList<>();

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public List<ChatMessageViewDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageViewDTO> messages) {
        this.messages = messages;
    }
}
