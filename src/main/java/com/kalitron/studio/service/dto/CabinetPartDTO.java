package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.CabinetPart} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetPartDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String partCode;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private Integer widthMm;

    @NotNull
    private Integer heightMm;

    @NotNull
    private Integer thicknessMm;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @Size(max = 100)
    private String edgeBanding;

    @Size(max = 30)
    private String grainDirection;

    @Size(max = 120)
    private String cncOperation;

    @Size(max = 300)
    private String notes;

    @NotNull
    private MaterialDTO material;

    @NotNull
    private CabinetDTO cabinet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWidthMm() {
        return widthMm;
    }

    public void setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
    }

    public Integer getHeightMm() {
        return heightMm;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getThicknessMm() {
        return thicknessMm;
    }

    public void setThicknessMm(Integer thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getEdgeBanding() {
        return edgeBanding;
    }

    public void setEdgeBanding(String edgeBanding) {
        this.edgeBanding = edgeBanding;
    }

    public String getGrainDirection() {
        return grainDirection;
    }

    public void setGrainDirection(String grainDirection) {
        this.grainDirection = grainDirection;
    }

    public String getCncOperation() {
        return cncOperation;
    }

    public void setCncOperation(String cncOperation) {
        this.cncOperation = cncOperation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MaterialDTO getMaterial() {
        return material;
    }

    public void setMaterial(MaterialDTO material) {
        this.material = material;
    }

    public CabinetDTO getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetDTO cabinet) {
        this.cabinet = cabinet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CabinetPartDTO)) {
            return false;
        }

        CabinetPartDTO cabinetPartDTO = (CabinetPartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cabinetPartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetPartDTO{" +
            "id=" + getId() +
            ", partCode='" + getPartCode() + "'" +
            ", name='" + getName() + "'" +
            ", widthMm=" + getWidthMm() +
            ", heightMm=" + getHeightMm() +
            ", thicknessMm=" + getThicknessMm() +
            ", quantity=" + getQuantity() +
            ", edgeBanding='" + getEdgeBanding() + "'" +
            ", grainDirection='" + getGrainDirection() + "'" +
            ", cncOperation='" + getCncOperation() + "'" +
            ", notes='" + getNotes() + "'" +
            ", material=" + getMaterial() +
            ", cabinet=" + getCabinet() +
            "}";
    }
}
