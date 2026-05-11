package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.HardwareType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.Hardware} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.HardwareResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /hardware?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HardwareCriteria implements Serializable, Criteria {

    /**
     * Class for filtering HardwareType
     */
    public static class HardwareTypeFilter extends Filter<HardwareType> {

        public HardwareTypeFilter() {}

        public HardwareTypeFilter(HardwareTypeFilter filter) {
            super(filter);
        }

        @Override
        public HardwareTypeFilter copy() {
            return new HardwareTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private HardwareTypeFilter hardwareType;

    private BigDecimalFilter unitCostMxn;

    private StringFilter supplierName;

    private BooleanFilter isActive;

    private StringFilter notes;

    private Boolean distinct;

    public HardwareCriteria() {}

    public HardwareCriteria(HardwareCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.hardwareType = other.optionalHardwareType().map(HardwareTypeFilter::copy).orElse(null);
        this.unitCostMxn = other.optionalUnitCostMxn().map(BigDecimalFilter::copy).orElse(null);
        this.supplierName = other.optionalSupplierName().map(StringFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HardwareCriteria copy() {
        return new HardwareCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public HardwareTypeFilter getHardwareType() {
        return hardwareType;
    }

    public Optional<HardwareTypeFilter> optionalHardwareType() {
        return Optional.ofNullable(hardwareType);
    }

    public HardwareTypeFilter hardwareType() {
        if (hardwareType == null) {
            setHardwareType(new HardwareTypeFilter());
        }
        return hardwareType;
    }

    public void setHardwareType(HardwareTypeFilter hardwareType) {
        this.hardwareType = hardwareType;
    }

    public BigDecimalFilter getUnitCostMxn() {
        return unitCostMxn;
    }

    public Optional<BigDecimalFilter> optionalUnitCostMxn() {
        return Optional.ofNullable(unitCostMxn);
    }

    public BigDecimalFilter unitCostMxn() {
        if (unitCostMxn == null) {
            setUnitCostMxn(new BigDecimalFilter());
        }
        return unitCostMxn;
    }

    public void setUnitCostMxn(BigDecimalFilter unitCostMxn) {
        this.unitCostMxn = unitCostMxn;
    }

    public StringFilter getSupplierName() {
        return supplierName;
    }

    public Optional<StringFilter> optionalSupplierName() {
        return Optional.ofNullable(supplierName);
    }

    public StringFilter supplierName() {
        if (supplierName == null) {
            setSupplierName(new StringFilter());
        }
        return supplierName;
    }

    public void setSupplierName(StringFilter supplierName) {
        this.supplierName = supplierName;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HardwareCriteria that = (HardwareCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(hardwareType, that.hardwareType) &&
            Objects.equals(unitCostMxn, that.unitCostMxn) &&
            Objects.equals(supplierName, that.supplierName) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, hardwareType, unitCostMxn, supplierName, isActive, notes, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HardwareCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalHardwareType().map(f -> "hardwareType=" + f + ", ").orElse("") +
            optionalUnitCostMxn().map(f -> "unitCostMxn=" + f + ", ").orElse("") +
            optionalSupplierName().map(f -> "supplierName=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
