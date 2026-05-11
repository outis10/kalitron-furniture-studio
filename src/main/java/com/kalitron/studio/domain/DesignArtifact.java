package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DesignArtifact.
 */
@Entity
@Table(name = "design_artifact")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignArtifact implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "artifact_type", nullable = false)
    private ArtifactType artifactType;

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

    @Size(max = 128)
    @Column(name = "checksum", length = 128)
    private String checksum;

    @Lob
    @Column(name = "metadata_json")
    private String metadataJson;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

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

    public DesignArtifact id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArtifactType getArtifactType() {
        return this.artifactType;
    }

    public DesignArtifact artifactType(ArtifactType artifactType) {
        this.setArtifactType(artifactType);
        return this;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public DesignArtifact fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public DesignArtifact filePath(String filePath) {
        this.setFilePath(filePath);
        return this;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public DesignArtifact mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSizeKb() {
        return this.fileSizeKb;
    }

    public DesignArtifact fileSizeKb(Long fileSizeKb) {
        this.setFileSizeKb(fileSizeKb);
        return this;
    }

    public void setFileSizeKb(Long fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public DesignArtifact checksum(String checksum) {
        this.setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getMetadataJson() {
        return this.metadataJson;
    }

    public DesignArtifact metadataJson(String metadataJson) {
        this.setMetadataJson(metadataJson);
        return this;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public DesignArtifact createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public DesignArtifact session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DesignArtifact)) {
            return false;
        }
        return getId() != null && getId().equals(((DesignArtifact) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignArtifact{" +
            "id=" + getId() +
            ", artifactType='" + getArtifactType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", filePath='" + getFilePath() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", fileSizeKb=" + getFileSizeKb() +
            ", checksum='" + getChecksum() + "'" +
            ", metadataJson='" + getMetadataJson() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
