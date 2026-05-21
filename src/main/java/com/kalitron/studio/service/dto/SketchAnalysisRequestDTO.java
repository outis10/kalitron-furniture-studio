package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ProjectType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class SketchAnalysisRequestDTO implements Serializable {

    @NotNull
    private Long sessionId;

    @Size(max = 7000000)
    private String imageBase64;

    @Size(max = 255)
    private String imageFileName;

    @Size(max = 80)
    private String imageMimeType;

    private Long imageSizeBytes;

    private ProjectType projectTypeHint;

    @Size(max = 20)
    private String unitHint;

    @Size(max = 1000)
    private String userPrompt;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImageMimeType() {
        return imageMimeType;
    }

    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = imageMimeType;
    }

    public Long getImageSizeBytes() {
        return imageSizeBytes;
    }

    public void setImageSizeBytes(Long imageSizeBytes) {
        this.imageSizeBytes = imageSizeBytes;
    }

    public ProjectType getProjectTypeHint() {
        return projectTypeHint;
    }

    public void setProjectTypeHint(ProjectType projectTypeHint) {
        this.projectTypeHint = projectTypeHint;
    }

    public String getUnitHint() {
        return unitHint;
    }

    public void setUnitHint(String unitHint) {
        this.unitHint = unitHint;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }
}
