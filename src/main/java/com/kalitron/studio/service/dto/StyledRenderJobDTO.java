package com.kalitron.studio.service.dto;

import java.time.Instant;
import java.util.List;

public class StyledRenderJobDTO {

    private Long jobId;
    private Long sessionId;
    private String sessionCode;
    private String status;
    private String promptUsed;
    private String pipeline;
    private Instant startedAt;
    private Instant finishedAt;
    private List<String> warnings;
    private List<StyledRenderArtifactItemDTO> artifacts;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPromptUsed() {
        return promptUsed;
    }

    public void setPromptUsed(String promptUsed) {
        this.promptUsed = promptUsed;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<StyledRenderArtifactItemDTO> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<StyledRenderArtifactItemDTO> artifacts) {
        this.artifacts = artifacts;
    }
}
