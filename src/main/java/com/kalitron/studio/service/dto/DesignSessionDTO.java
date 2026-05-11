package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.DesignSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignSessionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String sessionCode;

    @NotNull
    private ProjectType projectType;

    @NotNull
    private SessionStatus status;

    @NotNull
    @Size(max = 120)
    private String clientName;

    @Size(max = 120)
    private String clientEmail;

    @Size(max = 20)
    private String clientPhone;

    @Size(max = 80)
    private String selectedStyle;

    @Size(max = 2000)
    private String notes;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private KitchenSpecDTO spec;

    private CatalogStyleDTO catalogStyle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public KitchenSpecDTO getSpec() {
        return spec;
    }

    public void setSpec(KitchenSpecDTO spec) {
        this.spec = spec;
    }

    public CatalogStyleDTO getCatalogStyle() {
        return catalogStyle;
    }

    public void setCatalogStyle(CatalogStyleDTO catalogStyle) {
        this.catalogStyle = catalogStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DesignSessionDTO)) {
            return false;
        }

        DesignSessionDTO designSessionDTO = (DesignSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, designSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignSessionDTO{" +
            "id=" + getId() +
            ", sessionCode='" + getSessionCode() + "'" +
            ", projectType='" + getProjectType() + "'" +
            ", status='" + getStatus() + "'" +
            ", clientName='" + getClientName() + "'" +
            ", clientEmail='" + getClientEmail() + "'" +
            ", clientPhone='" + getClientPhone() + "'" +
            ", selectedStyle='" + getSelectedStyle() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", spec=" + getSpec() +
            ", catalogStyle=" + getCatalogStyle() +
            "}";
    }
}
