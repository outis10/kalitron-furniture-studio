package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class VisualConceptRequestDTO implements Serializable {

    @NotNull
    private Long sessionId;

    @Size(max = 80)
    private String style;

    @Size(max = 80)
    private String layout;

    @Size(max = 120)
    private String finish;

    @Size(max = 1000)
    private String visualInstructions;

    private String clientImageBase64;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getVisualInstructions() {
        return visualInstructions;
    }

    public void setVisualInstructions(String visualInstructions) {
        this.visualInstructions = visualInstructions;
    }

    public String getClientImageBase64() {
        return clientImageBase64;
    }

    public void setClientImageBase64(String clientImageBase64) {
        this.clientImageBase64 = clientImageBase64;
    }
}
