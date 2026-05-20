package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

public class CabinetPlanValidationMessageDTO implements Serializable {

    @NotBlank
    @Pattern(regexp = "INFO|WARNING|ERROR")
    private String severity;

    @NotBlank
    @Size(max = 80)
    private String code;

    @NotBlank
    @Size(max = 500)
    private String message;

    @Size(max = 20)
    private String wallCode;

    @Size(max = 20)
    private String cabinetCode;

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

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

    public String getWallCode() {
        return wallCode;
    }

    public void setWallCode(String wallCode) {
        this.wallCode = wallCode;
    }

    public String getCabinetCode() {
        return cabinetCode;
    }

    public void setCabinetCode(String cabinetCode) {
        this.cabinetCode = cabinetCode;
    }
}
