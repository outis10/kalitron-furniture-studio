package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.FinishType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.Cabinet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String cabinetCode;

    @NotNull
    private CabinetCategory category;

    @NotNull
    @Size(max = 120)
    private String label;

    @NotNull
    private Integer widthMm;

    @NotNull
    private Integer heightMm;

    @NotNull
    private Integer depthMm;

    @NotNull
    @Min(value = 0)
    @Max(value = 6)
    private Integer doors;

    @NotNull
    @Min(value = 0)
    @Max(value = 8)
    private Integer drawers;

    @Min(value = 0)
    @Max(value = 12)
    private Integer shelves;

    @NotNull
    private FinishType finish;

    private Integer positionX;

    private Integer positionY;

    private Integer positionZ;

    private Integer rotationDeg;

    private Integer positionSeq;

    @Lob
    private String csvRowJson;

    @Size(max = 300)
    private String notes;

    private CabinetTemplateDTO template;

    @NotNull
    private MaterialDTO material;

    @NotNull
    private KitchenSpecDTO spec;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCabinetCode() {
        return cabinetCode;
    }

    public void setCabinetCode(String cabinetCode) {
        this.cabinetCode = cabinetCode;
    }

    public CabinetCategory getCategory() {
        return category;
    }

    public void setCategory(CabinetCategory category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Integer getDepthMm() {
        return depthMm;
    }

    public void setDepthMm(Integer depthMm) {
        this.depthMm = depthMm;
    }

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public Integer getDrawers() {
        return drawers;
    }

    public void setDrawers(Integer drawers) {
        this.drawers = drawers;
    }

    public Integer getShelves() {
        return shelves;
    }

    public void setShelves(Integer shelves) {
        this.shelves = shelves;
    }

    public FinishType getFinish() {
        return finish;
    }

    public void setFinish(FinishType finish) {
        this.finish = finish;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public Integer getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(Integer positionZ) {
        this.positionZ = positionZ;
    }

    public Integer getRotationDeg() {
        return rotationDeg;
    }

    public void setRotationDeg(Integer rotationDeg) {
        this.rotationDeg = rotationDeg;
    }

    public Integer getPositionSeq() {
        return positionSeq;
    }

    public void setPositionSeq(Integer positionSeq) {
        this.positionSeq = positionSeq;
    }

    public String getCsvRowJson() {
        return csvRowJson;
    }

    public void setCsvRowJson(String csvRowJson) {
        this.csvRowJson = csvRowJson;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CabinetTemplateDTO getTemplate() {
        return template;
    }

    public void setTemplate(CabinetTemplateDTO template) {
        this.template = template;
    }

    public MaterialDTO getMaterial() {
        return material;
    }

    public void setMaterial(MaterialDTO material) {
        this.material = material;
    }

    public KitchenSpecDTO getSpec() {
        return spec;
    }

    public void setSpec(KitchenSpecDTO spec) {
        this.spec = spec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CabinetDTO)) {
            return false;
        }

        CabinetDTO cabinetDTO = (CabinetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cabinetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetDTO{" +
            "id=" + getId() +
            ", cabinetCode='" + getCabinetCode() + "'" +
            ", category='" + getCategory() + "'" +
            ", label='" + getLabel() + "'" +
            ", widthMm=" + getWidthMm() +
            ", heightMm=" + getHeightMm() +
            ", depthMm=" + getDepthMm() +
            ", doors=" + getDoors() +
            ", drawers=" + getDrawers() +
            ", shelves=" + getShelves() +
            ", finish='" + getFinish() + "'" +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", positionZ=" + getPositionZ() +
            ", rotationDeg=" + getRotationDeg() +
            ", positionSeq=" + getPositionSeq() +
            ", csvRowJson='" + getCsvRowJson() + "'" +
            ", notes='" + getNotes() + "'" +
            ", template=" + getTemplate() +
            ", material=" + getMaterial() +
            ", spec=" + getSpec() +
            "}";
    }
}
