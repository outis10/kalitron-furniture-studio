package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.ImageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DesignImage.
 */
@Entity
@Table(name = "design_image")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignImage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

    @NotNull
    @Size(max = 255)
    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @NotNull
    @Size(max = 500)
    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    @Size(max = 80)
    @Column(name = "mime_type", length = 80)
    private String mimeType;

    @Column(name = "file_size_kb")
    private Long fileSizeKb;

    @Column(name = "width_px")
    private Integer widthPx;

    @Column(name = "height_px")
    private Integer heightPx;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Size(max = 300)
    @Column(name = "description", length = 300)
    private String description;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "spec", "messageses", "imageses", "artifactses", "jobses", "quoteses", "wallses", "obstacleses", "catalogStyle" },
        allowSetters = true
    )
    private DesignSession session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DesignImage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImageType getImageType() {
        return this.imageType;
    }

    public DesignImage imageType(ImageType imageType) {
        this.setImageType(imageType);
        return this;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public DesignImage fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public DesignImage filePath(String filePath) {
        this.setFilePath(filePath);
        return this;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public DesignImage mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSizeKb() {
        return this.fileSizeKb;
    }

    public DesignImage fileSizeKb(Long fileSizeKb) {
        this.setFileSizeKb(fileSizeKb);
        return this;
    }

    public void setFileSizeKb(Long fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

    public Integer getWidthPx() {
        return this.widthPx;
    }

    public DesignImage widthPx(Integer widthPx) {
        this.setWidthPx(widthPx);
        return this;
    }

    public void setWidthPx(Integer widthPx) {
        this.widthPx = widthPx;
    }

    public Integer getHeightPx() {
        return this.heightPx;
    }

    public DesignImage heightPx(Integer heightPx) {
        this.setHeightPx(heightPx);
        return this;
    }

    public void setHeightPx(Integer heightPx) {
        this.heightPx = heightPx;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public DesignImage isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public DesignImage uploadedAt(Instant uploadedAt) {
        this.setUploadedAt(uploadedAt);
        return this;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getDescription() {
        return this.description;
    }

    public DesignImage description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public DesignImage session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DesignImage)) {
            return false;
        }
        return getId() != null && getId().equals(((DesignImage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignImage{" +
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
            "}";
    }
}
