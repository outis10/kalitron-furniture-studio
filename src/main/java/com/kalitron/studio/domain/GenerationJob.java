package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.GenerationJobStatus;
import com.kalitron.studio.domain.enumeration.GenerationJobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GenerationJob.
 */
@Entity
@Table(name = "generation_job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GenerationJob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private GenerationJobType jobType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GenerationJobStatus status;

    @Lob
    @Column(name = "input_json")
    private String inputJson;

    @Lob
    @Column(name = "output_json")
    private String outputJson;

    @Lob
    @Column(name = "error_message")
    private String errorMessage;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private DesignArtifact artifact;

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

    public GenerationJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GenerationJobType getJobType() {
        return this.jobType;
    }

    public GenerationJob jobType(GenerationJobType jobType) {
        this.setJobType(jobType);
        return this;
    }

    public void setJobType(GenerationJobType jobType) {
        this.jobType = jobType;
    }

    public GenerationJobStatus getStatus() {
        return this.status;
    }

    public GenerationJob status(GenerationJobStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(GenerationJobStatus status) {
        this.status = status;
    }

    public String getInputJson() {
        return this.inputJson;
    }

    public GenerationJob inputJson(String inputJson) {
        this.setInputJson(inputJson);
        return this;
    }

    public void setInputJson(String inputJson) {
        this.inputJson = inputJson;
    }

    public String getOutputJson() {
        return this.outputJson;
    }

    public GenerationJob outputJson(String outputJson) {
        this.setOutputJson(outputJson);
        return this;
    }

    public void setOutputJson(String outputJson) {
        this.outputJson = outputJson;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public GenerationJob errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public GenerationJob createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public GenerationJob startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return this.finishedAt;
    }

    public GenerationJob finishedAt(Instant finishedAt) {
        this.setFinishedAt(finishedAt);
        return this;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public DesignArtifact getArtifact() {
        return this.artifact;
    }

    public void setArtifact(DesignArtifact designArtifact) {
        this.artifact = designArtifact;
    }

    public GenerationJob artifact(DesignArtifact designArtifact) {
        this.setArtifact(designArtifact);
        return this;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public GenerationJob session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenerationJob)) {
            return false;
        }
        return getId() != null && getId().equals(((GenerationJob) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GenerationJob{" +
            "id=" + getId() +
            ", jobType='" + getJobType() + "'" +
            ", status='" + getStatus() + "'" +
            ", inputJson='" + getInputJson() + "'" +
            ", outputJson='" + getOutputJson() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", finishedAt='" + getFinishedAt() + "'" +
            "}";
    }
}
