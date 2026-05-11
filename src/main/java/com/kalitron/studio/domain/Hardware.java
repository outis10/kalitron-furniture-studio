package com.kalitron.studio.domain;

import com.kalitron.studio.domain.enumeration.HardwareType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Hardware.
 */
@Entity
@Table(name = "hardware")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Hardware implements Serializable {

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
    @Column(name = "hardware_type", nullable = false)
    private HardwareType hardwareType;

    @NotNull
    @Column(name = "unit_cost_mxn", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitCostMxn;

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

    public Hardware id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Hardware code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Hardware name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HardwareType getHardwareType() {
        return this.hardwareType;
    }

    public Hardware hardwareType(HardwareType hardwareType) {
        this.setHardwareType(hardwareType);
        return this;
    }

    public void setHardwareType(HardwareType hardwareType) {
        this.hardwareType = hardwareType;
    }

    public BigDecimal getUnitCostMxn() {
        return this.unitCostMxn;
    }

    public Hardware unitCostMxn(BigDecimal unitCostMxn) {
        this.setUnitCostMxn(unitCostMxn);
        return this;
    }

    public void setUnitCostMxn(BigDecimal unitCostMxn) {
        this.unitCostMxn = unitCostMxn;
    }

    public String getSupplierName() {
        return this.supplierName;
    }

    public Hardware supplierName(String supplierName) {
        this.setSupplierName(supplierName);
        return this;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Hardware isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return this.notes;
    }

    public Hardware notes(String notes) {
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
        if (!(o instanceof Hardware)) {
            return false;
        }
        return getId() != null && getId().equals(((Hardware) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Hardware{" +
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
