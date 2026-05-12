package com.kalitron.studio.service.dto;

import java.io.Serializable;
import java.util.Map;

public class ChatResponseDTO implements Serializable {

    private Long sessionId;

    private String sessionCode;

    private String reply;

    private boolean specsReady;

    private Map<String, Object> specsSummary;

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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isSpecsReady() {
        return specsReady;
    }

    public void setSpecsReady(boolean specsReady) {
        this.specsReady = specsReady;
    }

    public Map<String, Object> getSpecsSummary() {
        return specsSummary;
    }

    public void setSpecsSummary(Map<String, Object> specsSummary) {
        this.specsSummary = specsSummary;
    }
}
