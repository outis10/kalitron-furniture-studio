package com.kalitron.studio.domain;

import com.kalitron.studio.domain.enumeration.CabinetCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CabinetTemplate.
 */
@Entity
@Table(name = "cabinet_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 40)
    @Column(name = "code", length = 40, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 120)
    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CabinetCategory category;

    @Column(name = "default_width_mm")
    private Integer defaultWidthMm;

    @Column(name = "default_height_mm")
    private Integer defaultHeightMm;

    @Column(name = "default_depth_mm")
    private Integer defaultDepthMm;

    @Column(name = "min_width_mm")
    private Integer minWidthMm;

    @Column(name = "max_width_mm")
    private Integer maxWidthMm;

    @NotNull
    @Column(name = "supports_doors", nullable = false)
    private Boolean supportsDoors;

    @NotNull
    @Column(name = "supports_drawers", nullable = false)
    private Boolean supportsDrawers;

    @NotNull
    @Column(name = "supports_shelves", nullable = false)
    private Boolean supportsShelves;

    @Size(max = 120)
    @Column(name = "fusion_template_name", length = 120)
    private String fusionTemplateName;

    @Lob
    @Column(name = "csv_profile_json")
    private String csvProfileJson;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CabinetTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public CabinetTemplate code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public CabinetTemplate name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CabinetCategory getCategory() {
        return this.category;
    }

    public CabinetTemplate category(CabinetCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(CabinetCategory category) {
        this.category = category;
    }

    public Integer getDefaultWidthMm() {
        return this.defaultWidthMm;
    }

    public CabinetTemplate defaultWidthMm(Integer defaultWidthMm) {
        this.setDefaultWidthMm(defaultWidthMm);
        return this;
    }

    public void setDefaultWidthMm(Integer defaultWidthMm) {
        this.defaultWidthMm = defaultWidthMm;
    }

    public Integer getDefaultHeightMm() {
        return this.defaultHeightMm;
    }

    public CabinetTemplate defaultHeightMm(Integer defaultHeightMm) {
        this.setDefaultHeightMm(defaultHeightMm);
        return this;
    }

    public void setDefaultHeightMm(Integer defaultHeightMm) {
        this.defaultHeightMm = defaultHeightMm;
    }

    public Integer getDefaultDepthMm() {
        return this.defaultDepthMm;
    }

    public CabinetTemplate defaultDepthMm(Integer defaultDepthMm) {
        this.setDefaultDepthMm(defaultDepthMm);
        return this;
    }

    public void setDefaultDepthMm(Integer defaultDepthMm) {
        this.defaultDepthMm = defaultDepthMm;
    }

    public Integer getMinWidthMm() {
        return this.minWidthMm;
    }

    public CabinetTemplate minWidthMm(Integer minWidthMm) {
        this.setMinWidthMm(minWidthMm);
        return this;
    }

    public void setMinWidthMm(Integer minWidthMm) {
        this.minWidthMm = minWidthMm;
    }

    public Integer getMaxWidthMm() {
        return this.maxWidthMm;
    }

    public CabinetTemplate maxWidthMm(Integer maxWidthMm) {
        this.setMaxWidthMm(maxWidthMm);
        return this;
    }

    public void setMaxWidthMm(Integer maxWidthMm) {
        this.maxWidthMm = maxWidthMm;
    }

    public Boolean getSupportsDoors() {
        return this.supportsDoors;
    }

    public CabinetTemplate supportsDoors(Boolean supportsDoors) {
        this.setSupportsDoors(supportsDoors);
        return this;
    }

    public void setSupportsDoors(Boolean supportsDoors) {
        this.supportsDoors = supportsDoors;
    }

    public Boolean getSupportsDrawers() {
        return this.supportsDrawers;
    }

    public CabinetTemplate supportsDrawers(Boolean supportsDrawers) {
        this.setSupportsDrawers(supportsDrawers);
        return this;
    }

    public void setSupportsDrawers(Boolean supportsDrawers) {
        this.supportsDrawers = supportsDrawers;
    }

    public Boolean getSupportsShelves() {
        return this.supportsShelves;
    }

    public CabinetTemplate supportsShelves(Boolean supportsShelves) {
        this.setSupportsShelves(supportsShelves);
        return this;
    }

    public void setSupportsShelves(Boolean supportsShelves) {
        this.supportsShelves = supportsShelves;
    }

    public String getFusionTemplateName() {
        return this.fusionTemplateName;
    }

    public CabinetTemplate fusionTemplateName(String fusionTemplateName) {
        this.setFusionTemplateName(fusionTemplateName);
        return this;
    }

    public void setFusionTemplateName(String fusionTemplateName) {
        this.fusionTemplateName = fusionTemplateName;
    }

    public String getCsvProfileJson() {
        return this.csvProfileJson;
    }

    public CabinetTemplate csvProfileJson(String csvProfileJson) {
        this.setCsvProfileJson(csvProfileJson);
        return this;
    }

    public void setCsvProfileJson(String csvProfileJson) {
        this.csvProfileJson = csvProfileJson;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public CabinetTemplate isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public CabinetTemplate sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CabinetTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((CabinetTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetTemplate{" +
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
