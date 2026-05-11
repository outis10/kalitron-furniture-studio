package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.MaterialKind;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.Material} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaterialDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String code;

    @NotNull
    @Size(max = 120)
    private String name;

    @NotNull
    private MaterialKind materialKind;

    private Integer thicknessMm;

    private Integer sheetWidthMm;

    private Integer sheetHeightMm;

    private BigDecimal costPerSheetMxn;

    private BigDecimal costPerSquareMeterMxn;

    @Size(max = 120)
    private String supplierName;

    @NotNull
    private Boolean isActive;

    @Size(max = 300)
    private String notes;

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

    public MaterialKind getMaterialKind() {
        return materialKind;
    }

    public void setMaterialKind(MaterialKind materialKind) {
        this.materialKind = materialKind;
    }

    public Integer getThicknessMm() {
        return thicknessMm;
    }

    public void setThicknessMm(Integer thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public Integer getSheetWidthMm() {
        return sheetWidthMm;
    }

    public void setSheetWidthMm(Integer sheetWidthMm) {
        this.sheetWidthMm = sheetWidthMm;
    }

    public Integer getSheetHeightMm() {
        return sheetHeightMm;
    }

    public void setSheetHeightMm(Integer sheetHeightMm) {
        this.sheetHeightMm = sheetHeightMm;
    }

    public BigDecimal getCostPerSheetMxn() {
        return costPerSheetMxn;
    }

    public void setCostPerSheetMxn(BigDecimal costPerSheetMxn) {
        this.costPerSheetMxn = costPerSheetMxn;
    }

    public BigDecimal getCostPerSquareMeterMxn() {
        return costPerSquareMeterMxn;
    }

    public void setCostPerSquareMeterMxn(BigDecimal costPerSquareMeterMxn) {
        this.costPerSquareMeterMxn = costPerSquareMeterMxn;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaterialDTO)) {
            return false;
        }

        MaterialDTO materialDTO = (MaterialDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, materialDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaterialDTO{" +
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
