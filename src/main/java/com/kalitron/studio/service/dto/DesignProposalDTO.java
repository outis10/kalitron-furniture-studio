package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import java.io.Serializable;
import java.time.Instant;

public class DesignProposalDTO implements Serializable {

    private Long sessionId;

    private String sessionCode;

    private String clientName;

    private ProjectType projectType;

    private SessionStatus status;

    private String selectedStyle;

    private Instant updatedAt;

    private String renderImageUrl;

    private String renderBadge;

    private String specsSummary;

    private long cabinetCount;

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

    public String getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRenderImageUrl() {
        return renderImageUrl;
    }

    public void setRenderImageUrl(String renderImageUrl) {
        this.renderImageUrl = renderImageUrl;
    }

    public String getRenderBadge() {
        return renderBadge;
    }

    public void setRenderBadge(String renderBadge) {
        this.renderBadge = renderBadge;
    }

    public String getSpecsSummary() {
        return specsSummary;
    }

    public void setSpecsSummary(String specsSummary) {
        this.specsSummary = specsSummary;
    }

    public long getCabinetCount() {
        return cabinetCount;
    }

    public void setCabinetCount(long cabinetCount) {
        this.cabinetCount = cabinetCount;
    }
}
