package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.FinishType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Cabinet.
 */
@Entity
@Table(name = "cabinet")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cabinet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "cabinet_code", length = 20, nullable = false)
    private String cabinetCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CabinetCategory category;

    @NotNull
    @Size(max = 120)
    @Column(name = "label", length = 120, nullable = false)
    private String label;

    @NotNull
    @Column(name = "width_mm", nullable = false)
    private Integer widthMm;

    @NotNull
    @Column(name = "height_mm", nullable = false)
    private Integer heightMm;

    @NotNull
    @Column(name = "depth_mm", nullable = false)
    private Integer depthMm;

    @NotNull
    @Min(value = 0)
    @Max(value = 6)
    @Column(name = "doors", nullable = false)
    private Integer doors;

    @NotNull
    @Min(value = 0)
    @Max(value = 8)
    @Column(name = "drawers", nullable = false)
    private Integer drawers;

    @Min(value = 0)
    @Max(value = 12)
    @Column(name = "shelves")
    private Integer shelves;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "finish", nullable = false)
    private FinishType finish;

    @Column(name = "position_x")
    private Integer positionX;

    @Column(name = "position_y")
    private Integer positionY;

    @Column(name = "position_z")
    private Integer positionZ;

    @Column(name = "rotation_deg")
    private Integer rotationDeg;

    @Column(name = "position_seq")
    private Integer positionSeq;

    @Lob
    @Column(name = "csv_row_json")
    private String csvRowJson;

    @Size(max = 300)
    @Column(name = "notes", length = 300)
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cabinet")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "material", "cabinet" }, allowSetters = true)
    private Set<CabinetPart> partses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private CabinetTemplate template;

    @ManyToOne(optional = false)
    @NotNull
    private Material material;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "cabinetses", "primaryMaterial", "session" }, allowSetters = true)
    private KitchenSpec spec;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cabinet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCabinetCode() {
        return this.cabinetCode;
    }

    public Cabinet cabinetCode(String cabinetCode) {
        this.setCabinetCode(cabinetCode);
        return this;
    }

    public void setCabinetCode(String cabinetCode) {
        this.cabinetCode = cabinetCode;
    }

    public CabinetCategory getCategory() {
        return this.category;
    }

    public Cabinet category(CabinetCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(CabinetCategory category) {
        this.category = category;
    }

    public String getLabel() {
        return this.label;
    }

    public Cabinet label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getWidthMm() {
        return this.widthMm;
    }

    public Cabinet widthMm(Integer widthMm) {
        this.setWidthMm(widthMm);
        return this;
    }

    public void setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
    }

    public Integer getHeightMm() {
        return this.heightMm;
    }

    public Cabinet heightMm(Integer heightMm) {
        this.setHeightMm(heightMm);
        return this;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getDepthMm() {
        return this.depthMm;
    }

    public Cabinet depthMm(Integer depthMm) {
        this.setDepthMm(depthMm);
        return this;
    }

    public void setDepthMm(Integer depthMm) {
        this.depthMm = depthMm;
    }

    public Integer getDoors() {
        return this.doors;
    }

    public Cabinet doors(Integer doors) {
        this.setDoors(doors);
        return this;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public Integer getDrawers() {
        return this.drawers;
    }

    public Cabinet drawers(Integer drawers) {
        this.setDrawers(drawers);
        return this;
    }

    public void setDrawers(Integer drawers) {
        this.drawers = drawers;
    }

    public Integer getShelves() {
        return this.shelves;
    }

    public Cabinet shelves(Integer shelves) {
        this.setShelves(shelves);
        return this;
    }

    public void setShelves(Integer shelves) {
        this.shelves = shelves;
    }

    public FinishType getFinish() {
        return this.finish;
    }

    public Cabinet finish(FinishType finish) {
        this.setFinish(finish);
        return this;
    }

    public void setFinish(FinishType finish) {
        this.finish = finish;
    }

    public Integer getPositionX() {
        return this.positionX;
    }

    public Cabinet positionX(Integer positionX) {
        this.setPositionX(positionX);
        return this;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return this.positionY;
    }

    public Cabinet positionY(Integer positionY) {
        this.setPositionY(positionY);
        return this;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public Integer getPositionZ() {
        return this.positionZ;
    }

    public Cabinet positionZ(Integer positionZ) {
        this.setPositionZ(positionZ);
        return this;
    }

    public void setPositionZ(Integer positionZ) {
        this.positionZ = positionZ;
    }

    public Integer getRotationDeg() {
        return this.rotationDeg;
    }

    public Cabinet rotationDeg(Integer rotationDeg) {
        this.setRotationDeg(rotationDeg);
        return this;
    }

    public void setRotationDeg(Integer rotationDeg) {
        this.rotationDeg = rotationDeg;
    }

    public Integer getPositionSeq() {
        return this.positionSeq;
    }

    public Cabinet positionSeq(Integer positionSeq) {
        this.setPositionSeq(positionSeq);
        return this;
    }

    public void setPositionSeq(Integer positionSeq) {
        this.positionSeq = positionSeq;
    }

    public String getCsvRowJson() {
        return this.csvRowJson;
    }

    public Cabinet csvRowJson(String csvRowJson) {
        this.setCsvRowJson(csvRowJson);
        return this;
    }

    public void setCsvRowJson(String csvRowJson) {
        this.csvRowJson = csvRowJson;
    }

    public String getNotes() {
        return this.notes;
    }

    public Cabinet notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<CabinetPart> getPartses() {
        return this.partses;
    }

    public void setPartses(Set<CabinetPart> cabinetParts) {
        if (this.partses != null) {
            this.partses.forEach(i -> i.setCabinet(null));
        }
        if (cabinetParts != null) {
            cabinetParts.forEach(i -> i.setCabinet(this));
        }
        this.partses = cabinetParts;
    }

    public Cabinet partses(Set<CabinetPart> cabinetParts) {
        this.setPartses(cabinetParts);
        return this;
    }

    public Cabinet addParts(CabinetPart cabinetPart) {
        this.partses.add(cabinetPart);
        cabinetPart.setCabinet(this);
        return this;
    }

    public Cabinet removeParts(CabinetPart cabinetPart) {
        this.partses.remove(cabinetPart);
        cabinetPart.setCabinet(null);
        return this;
    }

    public CabinetTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(CabinetTemplate cabinetTemplate) {
        this.template = cabinetTemplate;
    }

    public Cabinet template(CabinetTemplate cabinetTemplate) {
        this.setTemplate(cabinetTemplate);
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Cabinet material(Material material) {
        this.setMaterial(material);
        return this;
    }

    public KitchenSpec getSpec() {
        return this.spec;
    }

    public void setSpec(KitchenSpec kitchenSpec) {
        this.spec = kitchenSpec;
    }

    public Cabinet spec(KitchenSpec kitchenSpec) {
        this.setSpec(kitchenSpec);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cabinet)) {
            return false;
        }
        return getId() != null && getId().equals(((Cabinet) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cabinet{" +
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
            "}";
    }
}
