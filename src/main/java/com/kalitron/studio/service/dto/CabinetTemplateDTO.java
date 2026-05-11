package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.CabinetCategory;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.CabinetTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String code;

    @NotNull
    @Size(max = 120)
    private String name;

    @NotNull
    private CabinetCategory category;

    private Integer defaultWidthMm;

    private Integer defaultHeightMm;

    private Integer defaultDepthMm;

    private Integer minWidthMm;

    private Integer maxWidthMm;

    @NotNull
    private Boolean supportsDoors;

    @NotNull
    private Boolean supportsDrawers;

    @NotNull
    private Boolean supportsShelves;

    @Size(max = 120)
    private String fusionTemplateName;

    @Lob
    private String csvProfileJson;

    @NotNull
    private Boolean isActive;

    private Integer sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CabinetCategory getCategory() {
        return category;
    }

    public void setCategory(CabinetCategory category) {
        this.category = category;
    }

    public Integer getDefaultWidthMm() {
        return defaultWidthMm;
    }

    public void setDefaultWidthMm(Integer defaultWidthMm) {
        this.defaultWidthMm = defaultWidthMm;
    }

    public Integer getDefaultHeightMm() {
        return defaultHeightMm;
    }

    public void setDefaultHeightMm(Integer defaultHeightMm) {
        this.defaultHeightMm = defaultHeightMm;
    }

    public Integer getDefaultDepthMm() {
        return defaultDepthMm;
    }

    public void setDefaultDepthMm(Integer defaultDepthMm) {
        this.defaultDepthMm = defaultDepthMm;
    }

    public Integer getMinWidthMm() {
        return minWidthMm;
    }

    public void setMinWidthMm(Integer minWidthMm) {
        this.minWidthMm = minWidthMm;
    }

    public Integer getMaxWidthMm() {
        return maxWidthMm;
    }

    public void setMaxWidthMm(Integer maxWidthMm) {
        this.maxWidthMm = maxWidthMm;
    }

    public Boolean getSupportsDoors() {
        return supportsDoors;
    }

    public void setSupportsDoors(Boolean supportsDoors) {
        this.supportsDoors = supportsDoors;
    }

    public Boolean getSupportsDrawers() {
        return supportsDrawers;
    }

    public void setSupportsDrawers(Boolean supportsDrawers) {
        this.supportsDrawers = supportsDrawers;
    }

    public Boolean getSupportsShelves() {
        return supportsShelves;
    }

    public void setSupportsShelves(Boolean supportsShelves) {
        this.supportsShelves = supportsShelves;
    }

    public String getFusionTemplateName() {
        return fusionTemplateName;
    }

    public void setFusionTemplateName(String fusionTemplateName) {
        this.fusionTemplateName = fusionTemplateName;
    }

    public String getCsvProfileJson() {
        return csvProfileJson;
    }

    public void setCsvProfileJson(String csvProfileJson) {
        this.csvProfileJson = csvProfileJson;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CabinetTemplateDTO)) {
            return false;
        }

        CabinetTemplateDTO cabinetTemplateDTO = (CabinetTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cabinetTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetTemplateDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", category='" + getCategory() + "'" +
            ", defaultWidthMm=" + getDefaultWidthMm() +
            ", defaultHeightMm=" + getDefaultHeightMm() +
            ", defaultDepthMm=" + getDefaultDepthMm() +
            ", minWidthMm=" + getMinWidthMm() +
            ", maxWidthMm=" + getMaxWidthMm() +
            ", supportsDoors='" + getSupportsDoors() + "'" +
            ", supportsDrawers='" + getSupportsDrawers() + "'" +
            ", supportsShelves='" + getSupportsShelves() + "'" +
            ", fusionTemplateName='" + getFusionTemplateName() + "'" +
            ", csvProfileJson='" + getCsvProfileJson() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}
