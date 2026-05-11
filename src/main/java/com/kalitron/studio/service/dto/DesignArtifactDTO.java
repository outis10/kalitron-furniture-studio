package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ArtifactType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.DesignArtifact} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignArtifactDTO implements Serializable {

    private Long id;

    @NotNull
    private ArtifactType artifactType;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @NotNull
    @Size(max = 500)
    private String filePath;

    @Size(max = 80)
    private String mimeType;

    private Long fileSizeKb;

    @Size(max = 128)
    private String checksum;

    @Lob
    private String metadataJson;

    @NotNull
    private Instant createdAt;

    @NotNull
    private DesignSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
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

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
        if (!(o instanceof DesignArtifactDTO)) {
            return false;
        }

        DesignArtifactDTO designArtifactDTO = (DesignArtifactDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, designArtifactDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignArtifactDTO{" +
            "id=" + getId() +
            ", artifactType='" + getArtifactType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", filePath='" + getFilePath() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", fileSizeKb=" + getFileSizeKb() +
            ", checksum='" + getChecksum() + "'" +
            ", metadataJson='" + getMetadataJson() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", session=" + getSession() +
            "}";
    }
}
