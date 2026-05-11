package com.kalitron.studio.domain;

import com.kalitron.studio.domain.enumeration.MaterialKind;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Material.
 */
@Entity
@Table(name = "material")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Material implements Serializable {

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
    @Column(name = "material_kind", nullable = false)
    private MaterialKind materialKind;

    @Column(name = "thickness_mm")
    private Integer thicknessMm;

    @Column(name = "sheet_width_mm")
    private Integer sheetWidthMm;

    @Column(name = "sheet_height_mm")
    private Integer sheetHeightMm;

    @Column(name = "cost_per_sheet_mxn", precision = 21, scale = 2)
    private BigDecimal costPerSheetMxn;

    @Column(name = "cost_per_square_meter_mxn", precision = 21, scale = 2)
    private BigDecimal costPerSquareMeterMxn;

    @Size(max = 120)
    @Column(name = "supplier_name", length = 120)
    private String supplierName;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Size(max = 300)
    @Column(name = "notes", length = 300)
    private String notes;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Material id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Material code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Material name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaterialKind getMaterialKind() {
        return this.materialKind;
    }

    public Material materialKind(MaterialKind materialKind) {
        this.setMaterialKind(materialKind);
        return this;
    }

    public void setMaterialKind(MaterialKind materialKind) {
        this.materialKind = materialKind;
    }

    public Integer getThicknessMm() {
        return this.thicknessMm;
    }

    public Material thicknessMm(Integer thicknessMm) {
        this.setThicknessMm(thicknessMm);
        return this;
    }

    public void setThicknessMm(Integer thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public Integer getSheetWidthMm() {
        return this.sheetWidthMm;
    }

    public Material sheetWidthMm(Integer sheetWidthMm) {
        this.setSheetWidthMm(sheetWidthMm);
        return this;
    }

    public void setSheetWidthMm(Integer sheetWidthMm) {
        this.sheetWidthMm = sheetWidthMm;
    }

    public Integer getSheetHeightMm() {
        return this.sheetHeightMm;
    }

    public Material sheetHeightMm(Integer sheetHeightMm) {
        this.setSheetHeightMm(sheetHeightMm);
        return this;
    }

    public void setSheetHeightMm(Integer sheetHeightMm) {
        this.sheetHeightMm = sheetHeightMm;
    }

    public BigDecimal getCostPerSheetMxn() {
        return this.costPerSheetMxn;
    }

    public Material costPerSheetMxn(BigDecimal costPerSheetMxn) {
        this.setCostPerSheetMxn(costPerSheetMxn);
        return this;
    }

    public void setCostPerSheetMxn(BigDecimal costPerSheetMxn) {
        this.costPerSheetMxn = costPerSheetMxn;
    }

    public BigDecimal getCostPerSquareMeterMxn() {
        return this.costPerSquareMeterMxn;
    }

    public Material costPerSquareMeterMxn(BigDecimal costPerSquareMeterMxn) {
        this.setCostPerSquareMeterMxn(costPerSquareMeterMxn);
        return this;
    }

    public void setCostPerSquareMeterMxn(BigDecimal costPerSquareMeterMxn) {
        this.costPerSquareMeterMxn = costPerSquareMeterMxn;
    }

    public String getSupplierName() {
        return this.supplierName;
    }

    public Material supplierName(String supplierName) {
        this.setSupplierName(supplierName);
        return this;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Material isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return this.notes;
    }

    public Material notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Material)) {
            return false;
        }
        return getId() != null && getId().equals(((Material) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Material{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", materialKind='" + getMaterialKind() + "'" +
            ", thicknessMm=" + getThicknessMm() +
            ", sheetWidthMm=" + getSheetWidthMm() +
            ", sheetHeightMm=" + getSheetHeightMm() +
            ", costPerSheetMxn=" + getCostPerSheetMxn() +
            ", costPerSquareMeterMxn=" + getCostPerSquareMeterMxn() +
            ", supplierName='" + getSupplierName() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
