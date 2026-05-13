package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class ChatRequestDTO implements Serializable {

    @NotNull
    private Long sessionId;

    @Size(max = 4000)
    private String message;

    @Size(max = 7000000)
    private String imageBase64;

    @Size(max = 255)
    private String imageFileName;

    @Size(max = 80)
    private String imageMimeType;

    private Long imageSizeBytes;

    @Size(max = 80)
    private String selectedStyle;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }
}
