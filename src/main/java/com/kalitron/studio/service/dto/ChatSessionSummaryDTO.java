package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import java.io.Serializable;
import java.time.Instant;

public class ChatSessionSummaryDTO implements Serializable {

    private Long sessionId;

    private String sessionCode;

    private String clientName;

    private ProjectType projectType;

    private SessionStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    private long generatedConceptCount;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getGeneratedConceptCount() {
        return generatedConceptCount;
    }

    public void setGeneratedConceptCount(long generatedConceptCount) {
        this.generatedConceptCount = generatedConceptCount;
    }
}
