package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ImageType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.DesignImage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignImageDTO implements Serializable {

    private Long id;

    @NotNull
    private ImageType imageType;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @NotNull
    @Size(max = 500)
    private String filePath;

    @Size(max = 80)
    private String mimeType;

    private Long fileSizeKb;

    private Integer widthPx;

    private Integer heightPx;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant uploadedAt;

    @Size(max = 300)
    private String description;

    @NotNull
    private DesignSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSizeKb() {
        return fileSizeKb;
    }

    public void setFileSizeKb(Long fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

    public Integer getWidthPx() {
        return widthPx;
    }

    public void setWidthPx(Integer widthPx) {
        this.widthPx = widthPx;
    }

    public Integer getHeightPx() {
        return heightPx;
    }

    public void setHeightPx(Integer heightPx) {
        this.heightPx = heightPx;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DesignSessionDTO getSession() {
        return session;
    }

    public void setSession(DesignSessionDTO session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DesignImageDTO)) {
            return false;
        }

        DesignImageDTO designImageDTO = (DesignImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, designImageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignImageDTO{" +
            "id=" + getId() +
            ", imageType='" + getImageType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", filePath='" + getFilePath() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", fileSizeKb=" + getFileSizeKb() +
            ", widthPx=" + getWidthPx() +
            ", heightPx=" + getHeightPx() +
            ", isActive='" + getIsActive() + "'" +
            ", uploadedAt='" + getUploadedAt() + "'" +
            ", description='" + getDescription() + "'" +
            ", session=" + getSession() +
            "}";
    }
}
