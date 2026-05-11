package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.MaterialKind;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.Material} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.MaterialResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaterialCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MaterialKind
     */
    public static class MaterialKindFilter extends Filter<MaterialKind> {

        public MaterialKindFilter() {}

        public MaterialKindFilter(MaterialKindFilter filter) {
            super(filter);
        }

        @Override
        public MaterialKindFilter copy() {
            return new MaterialKindFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private MaterialKindFilter materialKind;

    private IntegerFilter thicknessMm;

    private IntegerFilter sheetWidthMm;

    private IntegerFilter sheetHeightMm;

    private BigDecimalFilter costPerSheetMxn;

    private BigDecimalFilter costPerSquareMeterMxn;

    private StringFilter supplierName;

    private BooleanFilter isActive;

    private StringFilter notes;

    private Boolean distinct;

    public MaterialCriteria() {}

    public MaterialCriteria(MaterialCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.materialKind = other.optionalMaterialKind().map(MaterialKindFilter::copy).orElse(null);
        this.thicknessMm = other.optionalThicknessMm().map(IntegerFilter::copy).orElse(null);
        this.sheetWidthMm = other.optionalSheetWidthMm().map(IntegerFilter::copy).orElse(null);
        this.sheetHeightMm = other.optionalSheetHeightMm().map(IntegerFilter::copy).orElse(null);
        this.costPerSheetMxn = other.optionalCostPerSheetMxn().map(BigDecimalFilter::copy).orElse(null);
        this.costPerSquareMeterMxn = other.optionalCostPerSquareMeterMxn().map(BigDecimalFilter::copy).orElse(null);
        this.supplierName = other.optionalSupplierName().map(StringFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MaterialCriteria copy() {
        return new MaterialCriteria(this);
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

    public MaterialKindFilter getMaterialKind() {
        return materialKind;
    }

    public Optional<MaterialKindFilter> optionalMaterialKind() {
        return Optional.ofNullable(materialKind);
    }

    public MaterialKindFilter materialKind() {
        if (materialKind == null) {
            setMaterialKind(new MaterialKindFilter());
        }
        return materialKind;
    }

    public void setMaterialKind(MaterialKindFilter materialKind) {
        this.materialKind = materialKind;
    }

    public IntegerFilter getThicknessMm() {
        return thicknessMm;
    }

    public Optional<IntegerFilter> optionalThicknessMm() {
        return Optional.ofNullable(thicknessMm);
    }

    public IntegerFilter thicknessMm() {
        if (thicknessMm == null) {
            setThicknessMm(new IntegerFilter());
        }
        return thicknessMm;
    }

    public void setThicknessMm(IntegerFilter thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public IntegerFilter getSheetWidthMm() {
        return sheetWidthMm;
    }

    public Optional<IntegerFilter> optionalSheetWidthMm() {
        return Optional.ofNullable(sheetWidthMm);
    }

    public IntegerFilter sheetWidthMm() {
        if (sheetWidthMm == null) {
            setSheetWidthMm(new IntegerFilter());
        }
        return sheetWidthMm;
    }

    public void setSheetWidthMm(IntegerFilter sheetWidthMm) {
        this.sheetWidthMm = sheetWidthMm;
    }

    public IntegerFilter getSheetHeightMm() {
        return sheetHeightMm;
    }

    public Optional<IntegerFilter> optionalSheetHeightMm() {
        return Optional.ofNullable(sheetHeightMm);
    }

    public IntegerFilter sheetHeightMm() {
        if (sheetHeightMm == null) {
            setSheetHeightMm(new IntegerFilter());
        }
        return sheetHeightMm;
    }

    public void setSheetHeightMm(IntegerFilter sheetHeightMm) {
        this.sheetHeightMm = sheetHeightMm;
    }

    public BigDecimalFilter getCostPerSheetMxn() {
        return costPerSheetMxn;
    }

    public Optional<BigDecimalFilter> optionalCostPerSheetMxn() {
        return Optional.ofNullable(costPerSheetMxn);
    }

    public BigDecimalFilter costPerSheetMxn() {
        if (costPerSheetMxn == null) {
            setCostPerSheetMxn(new BigDecimalFilter());
        }
        return costPerSheetMxn;
    }

    public void setCostPerSheetMxn(BigDecimalFilter costPerSheetMxn) {
        this.costPerSheetMxn = costPerSheetMxn;
    }

    public BigDecimalFilter getCostPerSquareMeterMxn() {
        return costPerSquareMeterMxn;
    }

    public Optional<BigDecimalFilter> optionalCostPerSquareMeterMxn() {
        return Optional.ofNullable(costPerSquareMeterMxn);
    }

    public BigDecimalFilter costPerSquareMeterMxn() {
        if (costPerSquareMeterMxn == null) {
            setCostPerSquareMeterMxn(new BigDecimalFilter());
        }
        return costPerSquareMeterMxn;
    }

    public void setCostPerSquareMeterMxn(BigDecimalFilter costPerSquareMeterMxn) {
        this.costPerSquareMeterMxn = costPerSquareMeterMxn;
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
        final MaterialCriteria that = (MaterialCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(materialKind, that.materialKind) &&
            Objects.equals(thicknessMm, that.thicknessMm) &&
            Objects.equals(sheetWidthMm, that.sheetWidthMm) &&
            Objects.equals(sheetHeightMm, that.sheetHeightMm) &&
            Objects.equals(costPerSheetMxn, that.costPerSheetMxn) &&
            Objects.equals(costPerSquareMeterMxn, that.costPerSquareMeterMxn) &&
            Objects.equals(supplierName, that.supplierName) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            name,
            materialKind,
            thicknessMm,
            sheetWidthMm,
            sheetHeightMm,
            costPerSheetMxn,
            costPerSquareMeterMxn,
            supplierName,
            isActive,
            notes,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaterialCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalMaterialKind().map(f -> "materialKind=" + f + ", ").orElse("") +
            optionalThicknessMm().map(f -> "thicknessMm=" + f + ", ").orElse("") +
            optionalSheetWidthMm().map(f -> "sheetWidthMm=" + f + ", ").orElse("") +
            optionalSheetHeightMm().map(f -> "sheetHeightMm=" + f + ", ").orElse("") +
            optionalCostPerSheetMxn().map(f -> "costPerSheetMxn=" + f + ", ").orElse("") +
            optionalCostPerSquareMeterMxn().map(f -> "costPerSquareMeterMxn=" + f + ", ").orElse("") +
            optionalSupplierName().map(f -> "supplierName=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
