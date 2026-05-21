package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class SketchMissingInfoDTO implements Serializable {

    private String code;

    private String message;

    private String severity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
