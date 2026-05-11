package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.DesignSession} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.DesignSessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /design-sessions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignSessionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProjectType
     */
    public static class ProjectTypeFilter extends Filter<ProjectType> {

        public ProjectTypeFilter() {}

        public ProjectTypeFilter(ProjectTypeFilter filter) {
            super(filter);
        }

        @Override
        public ProjectTypeFilter copy() {
            return new ProjectTypeFilter(this);
        }
    }

    /**
     * Class for filtering SessionStatus
     */
    public static class SessionStatusFilter extends Filter<SessionStatus> {

        public SessionStatusFilter() {}

        public SessionStatusFilter(SessionStatusFilter filter) {
            super(filter);
        }

        @Override
        public SessionStatusFilter copy() {
            return new SessionStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sessionCode;

    private ProjectTypeFilter projectType;

    private SessionStatusFilter status;

    private StringFilter clientName;

    private StringFilter clientEmail;

    private StringFilter clientPhone;

    private StringFilter selectedStyle;

    private StringFilter notes;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter specId;

    private LongFilter messagesId;

    private LongFilter imagesId;

    private LongFilter artifactsId;

    private LongFilter jobsId;

    private LongFilter quotesId;

    private LongFilter wallsId;

    private LongFilter obstaclesId;

    private LongFilter catalogStyleId;

    private Boolean distinct;

    public DesignSessionCriteria() {}

    public DesignSessionCriteria(DesignSessionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sessionCode = other.optionalSessionCode().map(StringFilter::copy).orElse(null);
        this.projectType = other.optionalProjectType().map(ProjectTypeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(SessionStatusFilter::copy).orElse(null);
        this.clientName = other.optionalClientName().map(StringFilter::copy).orElse(null);
        this.clientEmail = other.optionalClientEmail().map(StringFilter::copy).orElse(null);
        this.clientPhone = other.optionalClientPhone().map(StringFilter::copy).orElse(null);
        this.selectedStyle = other.optionalSelectedStyle().map(StringFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.specId = other.optionalSpecId().map(LongFilter::copy).orElse(null);
        this.messagesId = other.optionalMessagesId().map(LongFilter::copy).orElse(null);
        this.imagesId = other.optionalImagesId().map(LongFilter::copy).orElse(null);
        this.artifactsId = other.optionalArtifactsId().map(LongFilter::copy).orElse(null);
        this.jobsId = other.optionalJobsId().map(LongFilter::copy).orElse(null);
        this.quotesId = other.optionalQuotesId().map(LongFilter::copy).orElse(null);
        this.wallsId = other.optionalWallsId().map(LongFilter::copy).orElse(null);
        this.obstaclesId = other.optionalObstaclesId().map(LongFilter::copy).orElse(null);
        this.catalogStyleId = other.optionalCatalogStyleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DesignSessionCriteria copy() {
        return new DesignSessionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSessionCode() {
        return sessionCode;
    }

    public Optional<StringFilter> optionalSessionCode() {
        return Optional.ofNullable(sessionCode);
    }

    public StringFilter sessionCode() {
        if (sessionCode == null) {
            setSessionCode(new StringFilter());
        }
        return sessionCode;
    }

    public void setSessionCode(StringFilter sessionCode) {
        this.sessionCode = sessionCode;
    }

    public ProjectTypeFilter getProjectType() {
        return projectType;
    }

    public Optional<ProjectTypeFilter> optionalProjectType() {
        return Optional.ofNullable(projectType);
    }

    public ProjectTypeFilter projectType() {
        if (projectType == null) {
            setProjectType(new ProjectTypeFilter());
        }
        return projectType;
    }

    public void setProjectType(ProjectTypeFilter projectType) {
        this.projectType = projectType;
    }

    public SessionStatusFilter getStatus() {
        return status;
    }

    public Optional<SessionStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public SessionStatusFilter status() {
        if (status == null) {
            setStatus(new SessionStatusFilter());
        }
        return status;
    }

    public void setStatus(SessionStatusFilter status) {
        this.status = status;
    }

    public StringFilter getClientName() {
        return clientName;
    }

    public Optional<StringFilter> optionalClientName() {
        return Optional.ofNullable(clientName);
    }

    public StringFilter clientName() {
        if (clientName == null) {
            setClientName(new StringFilter());
        }
        return clientName;
    }

    public void setClientName(StringFilter clientName) {
        this.clientName = clientName;
    }

    public StringFilter getClientEmail() {
        return clientEmail;
    }

    public Optional<StringFilter> optionalClientEmail() {
        return Optional.ofNullable(clientEmail);
    }

    public StringFilter clientEmail() {
        if (clientEmail == null) {
            setClientEmail(new StringFilter());
        }
        return clientEmail;
    }

    public void setClientEmail(StringFilter clientEmail) {
        this.clientEmail = clientEmail;
    }

    public StringFilter getClientPhone() {
        return clientPhone;
    }

    public Optional<StringFilter> optionalClientPhone() {
        return Optional.ofNullable(clientPhone);
    }

    public StringFilter clientPhone() {
        if (clientPhone == null) {
            setClientPhone(new StringFilter());
        }
        return clientPhone;
    }

    public void setClientPhone(StringFilter clientPhone) {
        this.clientPhone = clientPhone;
    }

    public StringFilter getSelectedStyle() {
        return selectedStyle;
    }

    public Optional<StringFilter> optionalSelectedStyle() {
        return Optional.ofNullable(selectedStyle);
    }

    public StringFilter selectedStyle() {
        if (selectedStyle == null) {
            setSelectedStyle(new StringFilter());
        }
        return selectedStyle;
    }

    public void setSelectedStyle(StringFilter selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getSpecId() {
        return specId;
    }

    public Optional<LongFilter> optionalSpecId() {
        return Optional.ofNullable(specId);
    }

    public LongFilter specId() {
        if (specId == null) {
            setSpecId(new LongFilter());
        }
        return specId;
    }

    public void setSpecId(LongFilter specId) {
        this.specId = specId;
    }

    public LongFilter getMessagesId() {
        return messagesId;
    }

    public Optional<LongFilter> optionalMessagesId() {
        return Optional.ofNullable(messagesId);
    }

    public LongFilter messagesId() {
        if (messagesId == null) {
            setMessagesId(new LongFilter());
        }
        return messagesId;
    }

    public void setMessagesId(LongFilter messagesId) {
        this.messagesId = messagesId;
    }

    public LongFilter getImagesId() {
        return imagesId;
    }

    public Optional<LongFilter> optionalImagesId() {
        return Optional.ofNullable(imagesId);
    }

    public LongFilter imagesId() {
        if (imagesId == null) {
            setImagesId(new LongFilter());
        }
        return imagesId;
    }

    public void setImagesId(LongFilter imagesId) {
        this.imagesId = imagesId;
    }

    public LongFilter getArtifactsId() {
        return artifactsId;
    }

    public Optional<LongFilter> optionalArtifactsId() {
        return Optional.ofNullable(artifactsId);
    }

    public LongFilter artifactsId() {
        if (artifactsId == null) {
            setArtifactsId(new LongFilter());
        }
        return artifactsId;
    }

    public void setArtifactsId(LongFilter artifactsId) {
        this.artifactsId = artifactsId;
    }

    public LongFilter getJobsId() {
        return jobsId;
    }

    public Optional<LongFilter> optionalJobsId() {
        return Optional.ofNullable(jobsId);
    }

    public LongFilter jobsId() {
        if (jobsId == null) {
            setJobsId(new LongFilter());
        }
        return jobsId;
    }

    public void setJobsId(LongFilter jobsId) {
        this.jobsId = jobsId;
    }

    public LongFilter getQuotesId() {
        return quotesId;
    }

    public Optional<LongFilter> optionalQuotesId() {
        return Optional.ofNullable(quotesId);
    }

    public LongFilter quotesId() {
        if (quotesId == null) {
            setQuotesId(new LongFilter());
        }
        return quotesId;
    }

    public void setQuotesId(LongFilter quotesId) {
        this.quotesId = quotesId;
    }

    public LongFilter getWallsId() {
        return wallsId;
    }

    public Optional<LongFilter> optionalWallsId() {
        return Optional.ofNullable(wallsId);
    }

    public LongFilter wallsId() {
        if (wallsId == null) {
            setWallsId(new LongFilter());
        }
        return wallsId;
    }

    public void setWallsId(LongFilter wallsId) {
        this.wallsId = wallsId;
    }

    public LongFilter getObstaclesId() {
        return obstaclesId;
    }

    public Optional<LongFilter> optionalObstaclesId() {
        return Optional.ofNullable(obstaclesId);
    }

    public LongFilter obstaclesId() {
        if (obstaclesId == null) {
            setObstaclesId(new LongFilter());
        }
        return obstaclesId;
    }

    public void setObstaclesId(LongFilter obstaclesId) {
        this.obstaclesId = obstaclesId;
    }

    public LongFilter getCatalogStyleId() {
        return catalogStyleId;
    }

    public Optional<LongFilter> optionalCatalogStyleId() {
        return Optional.ofNullable(catalogStyleId);
    }

    public LongFilter catalogStyleId() {
        if (catalogStyleId == null) {
            setCatalogStyleId(new LongFilter());
        }
        return catalogStyleId;
    }

    public void setCatalogStyleId(LongFilter catalogStyleId) {
        this.catalogStyleId = catalogStyleId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DesignSessionCriteria that = (DesignSessionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sessionCode, that.sessionCode) &&
            Objects.equals(projectType, that.projectType) &&
            Objects.equals(status, that.status) &&
            Objects.equals(clientName, that.clientName) &&
            Objects.equals(clientEmail, that.clientEmail) &&
            Objects.equals(clientPhone, that.clientPhone) &&
            Objects.equals(selectedStyle, that.selectedStyle) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(specId, that.specId) &&
            Objects.equals(messagesId, that.messagesId) &&
            Objects.equals(imagesId, that.imagesId) &&
            Objects.equals(artifactsId, that.artifactsId) &&
            Objects.equals(jobsId, that.jobsId) &&
            Objects.equals(quotesId, that.quotesId) &&
            Objects.equals(wallsId, that.wallsId) &&
            Objects.equals(obstaclesId, that.obstaclesId) &&
            Objects.equals(catalogStyleId, that.catalogStyleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sessionCode,
            projectType,
            status,
            clientName,
            clientEmail,
            clientPhone,
            selectedStyle,
            notes,
            createdAt,
            updatedAt,
            specId,
            messagesId,
            imagesId,
            artifactsId,
            jobsId,
            quotesId,
            wallsId,
            obstaclesId,
            catalogStyleId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignSessionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSessionCode().map(f -> "sessionCode=" + f + ", ").orElse("") +
            optionalProjectType().map(f -> "projectType=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalClientName().map(f -> "clientName=" + f + ", ").orElse("") +
            optionalClientEmail().map(f -> "clientEmail=" + f + ", ").orElse("") +
            optionalClientPhone().map(f -> "clientPhone=" + f + ", ").orElse("") +
            optionalSelectedStyle().map(f -> "selectedStyle=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalSpecId().map(f -> "specId=" + f + ", ").orElse("") +
            optionalMessagesId().map(f -> "messagesId=" + f + ", ").orElse("") +
            optionalImagesId().map(f -> "imagesId=" + f + ", ").orElse("") +
            optionalArtifactsId().map(f -> "artifactsId=" + f + ", ").orElse("") +
            optionalJobsId().map(f -> "jobsId=" + f + ", ").orElse("") +
            optionalQuotesId().map(f -> "quotesId=" + f + ", ").orElse("") +
            optionalWallsId().map(f -> "wallsId=" + f + ", ").orElse("") +
            optionalObstaclesId().map(f -> "obstaclesId=" + f + ", ").orElse("") +
            optionalCatalogStyleId().map(f -> "catalogStyleId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
