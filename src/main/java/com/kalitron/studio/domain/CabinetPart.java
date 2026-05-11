package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CabinetPart.
 */
@Entity
@Table(name = "cabinet_part")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetPart implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 40)
    @Column(name = "part_code", length = 40, nullable = false)
    private String partCode;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Column(name = "width_mm", nullable = false)
    private Integer widthMm;

    @NotNull
    @Column(name = "height_mm", nullable = false)
    private Integer heightMm;

    @NotNull
    @Column(name = "thickness_mm", nullable = false)
    private Integer thicknessMm;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Size(max = 100)
    @Column(name = "edge_banding", length = 100)
    private String edgeBanding;

    @Size(max = 30)
    @Column(name = "grain_direction", length = 30)
    private String grainDirection;

    @Size(max = 120)
    @Column(name = "cnc_operation", length = 120)
    private String cncOperation;

    @Size(max = 300)
    @Column(name = "notes", length = 300)
    private String notes;

    @ManyToOne(optional = false)
    @NotNull
    private Material material;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "partses", "template", "material", "spec" }, allowSetters = true)
    private Cabinet cabinet;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CabinetPart id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartCode() {
        return this.partCode;
    }

    public CabinetPart partCode(String partCode) {
        this.setPartCode(partCode);
        return this;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getName() {
        return this.name;
    }

    public CabinetPart name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWidthMm() {
        return this.widthMm;
    }

    public CabinetPart widthMm(Integer widthMm) {
        this.setWidthMm(widthMm);
        return this;
    }

    public void setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
    }

    public Integer getHeightMm() {
        return this.heightMm;
    }

    public CabinetPart heightMm(Integer heightMm) {
        this.setHeightMm(heightMm);
        return this;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getThicknessMm() {
        return this.thicknessMm;
    }

    public CabinetPart thicknessMm(Integer thicknessMm) {
        this.setThicknessMm(thicknessMm);
        return this;
    }

    public void setThicknessMm(Integer thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public CabinetPart quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getEdgeBanding() {
        return this.edgeBanding;
    }

    public CabinetPart edgeBanding(String edgeBanding) {
        this.setEdgeBanding(edgeBanding);
        return this;
    }

    public void setEdgeBanding(String edgeBanding) {
        this.edgeBanding = edgeBanding;
    }

    public String getGrainDirection() {
        return this.grainDirection;
    }

    public CabinetPart grainDirection(String grainDirection) {
        this.setGrainDirection(grainDirection);
        return this;
    }

    public void setGrainDirection(String grainDirection) {
        this.grainDirection = grainDirection;
    }

    public String getCncOperation() {
        return this.cncOperation;
    }

    public CabinetPart cncOperation(String cncOperation) {
        this.setCncOperation(cncOperation);
        return this;
    }

    public void setCncOperation(String cncOperation) {
        this.cncOperation = cncOperation;
    }

    public String getNotes() {
        return this.notes;
    }

    public CabinetPart notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public CabinetPart material(Material material) {
        this.setMaterial(material);
        return this;
    }

    public Cabinet getCabinet() {
        return this.cabinet;
    }

    public void setCabinet(Cabinet cabinet) {
        this.cabinet = cabinet;
    }

    public CabinetPart cabinet(Cabinet cabinet) {
        this.setCabinet(cabinet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CabinetPart)) {
            return false;
        }
        return getId() != null && getId().equals(((CabinetPart) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetPart{" +
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
            "}";
    }
}
