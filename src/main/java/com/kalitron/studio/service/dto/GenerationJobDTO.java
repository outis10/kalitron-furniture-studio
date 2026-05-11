package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.GenerationJobStatus;
import com.kalitron.studio.domain.enumeration.GenerationJobType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.GenerationJob} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GenerationJobDTO implements Serializable {

    private Long id;

    @NotNull
    private GenerationJobType jobType;

    @NotNull
    private GenerationJobStatus status;

    @Lob
    private String inputJson;

    @Lob
    private String outputJson;

    @Lob
    private String errorMessage;

    @NotNull
    private Instant createdAt;

    private Instant startedAt;

    private Instant finishedAt;

    private DesignArtifactDTO artifact;

    @NotNull
    private DesignSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GenerationJobType getJobType() {
        return jobType;
    }

    public void setJobType(GenerationJobType jobType) {
        this.jobType = jobType;
    }

    public GenerationJobStatus getStatus() {
        return status;
    }

    public void setStatus(GenerationJobStatus status) {
        this.status = status;
    }

    public String getInputJson() {
        return inputJson;
    }

    public void setInputJson(String inputJson) {
        this.inputJson = inputJson;
    }

    public String getOutputJson() {
        return outputJson;
    }

    public void setOutputJson(String outputJson) {
        this.outputJson = outputJson;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public DesignArtifactDTO getArtifact() {
        return artifact;
    }

    public void setArtifact(DesignArtifactDTO artifact) {
        this.artifact = artifact;
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
        if (!(o instanceof GenerationJobDTO)) {
            return false;
        }

        GenerationJobDTO generationJobDTO = (GenerationJobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, generationJobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GenerationJobDTO{" +
            "id=" + getId() +
            ", jobType='" + getJobType() + "'" +
            ", status='" + getStatus() + "'" +
            ", inputJson='" + getInputJson() + "'" +
            ", outputJson='" + getOutputJson() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", finishedAt='" + getFinishedAt() + "'" +
            ", artifact=" + getArtifact() +
            ", session=" + getSession() +
            "}";
    }
}
