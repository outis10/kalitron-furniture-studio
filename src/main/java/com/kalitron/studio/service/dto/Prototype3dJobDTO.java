package com.kalitron.studio.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Prototype3dJobDTO implements Serializable {

    private Long jobId;
    private Long sessionId;
    private String sessionCode;
    private String status;
    private String prototypeMode;
    private Instant startedAt;
    private Instant finishedAt;
    private List<Prototype3dArtifactItemDTO> artifacts = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

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

    public String getPrototypeMode() {
        return prototypeMode;
    }

    public void setPrototypeMode(String prototypeMode) {
        this.prototypeMode = prototypeMode;
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

    public List<Prototype3dArtifactItemDTO> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Prototype3dArtifactItemDTO> artifacts) {
        this.artifacts = artifacts;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
