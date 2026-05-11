package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.HardwareType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.Hardware} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HardwareDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String code;

    @NotNull
    @Size(max = 120)
    private String name;

    @NotNull
    private HardwareType hardwareType;

    @NotNull
    private BigDecimal unitCostMxn;

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

    public HardwareType getHardwareType() {
        return hardwareType;
    }

    public void setHardwareType(HardwareType hardwareType) {
        this.hardwareType = hardwareType;
    }

    public BigDecimal getUnitCostMxn() {
        return unitCostMxn;
    }

    public void setUnitCostMxn(BigDecimal unitCostMxn) {
        this.unitCostMxn = unitCostMxn;
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
        if (!(o instanceof HardwareDTO)) {
            return false;
        }

        HardwareDTO hardwareDTO = (HardwareDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, hardwareDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HardwareDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", hardwareType='" + getHardwareType() + "'" +
            ", unitCostMxn=" + getUnitCostMxn() +
            ", supplierName='" + getSupplierName() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
