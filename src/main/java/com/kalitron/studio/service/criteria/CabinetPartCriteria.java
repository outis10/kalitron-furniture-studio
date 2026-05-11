package com.kalitron.studio.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.CabinetPart} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.CabinetPartResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cabinet-parts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetPartCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter partCode;

    private StringFilter name;

    private IntegerFilter widthMm;

    private IntegerFilter heightMm;

    private IntegerFilter thicknessMm;

    private IntegerFilter quantity;

    private StringFilter edgeBanding;

    private StringFilter grainDirection;

    private StringFilter cncOperation;

    private StringFilter notes;

    private LongFilter materialId;

    private LongFilter cabinetId;

    private Boolean distinct;

    public CabinetPartCriteria() {}

    public CabinetPartCriteria(CabinetPartCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.partCode = other.optionalPartCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.widthMm = other.optionalWidthMm().map(IntegerFilter::copy).orElse(null);
        this.heightMm = other.optionalHeightMm().map(IntegerFilter::copy).orElse(null);
        this.thicknessMm = other.optionalThicknessMm().map(IntegerFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(IntegerFilter::copy).orElse(null);
        this.edgeBanding = other.optionalEdgeBanding().map(StringFilter::copy).orElse(null);
        this.grainDirection = other.optionalGrainDirection().map(StringFilter::copy).orElse(null);
        this.cncOperation = other.optionalCncOperation().map(StringFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.materialId = other.optionalMaterialId().map(LongFilter::copy).orElse(null);
        this.cabinetId = other.optionalCabinetId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CabinetPartCriteria copy() {
        return new CabinetPartCriteria(this);
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

    public StringFilter getPartCode() {
        return partCode;
    }

    public Optional<StringFilter> optionalPartCode() {
        return Optional.ofNullable(partCode);
    }

    public StringFilter partCode() {
        if (partCode == null) {
            setPartCode(new StringFilter());
        }
        return partCode;
    }

    public void setPartCode(StringFilter partCode) {
        this.partCode = partCode;
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

    public IntegerFilter getWidthMm() {
        return widthMm;
    }

    public Optional<IntegerFilter> optionalWidthMm() {
        return Optional.ofNullable(widthMm);
    }

    public IntegerFilter widthMm() {
        if (widthMm == null) {
            setWidthMm(new IntegerFilter());
        }
        return widthMm;
    }

    public void setWidthMm(IntegerFilter widthMm) {
        this.widthMm = widthMm;
    }

    public IntegerFilter getHeightMm() {
        return heightMm;
    }

    public Optional<IntegerFilter> optionalHeightMm() {
        return Optional.ofNullable(heightMm);
    }

    public IntegerFilter heightMm() {
        if (heightMm == null) {
            setHeightMm(new IntegerFilter());
        }
        return heightMm;
    }

    public void setHeightMm(IntegerFilter heightMm) {
        this.heightMm = heightMm;
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

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public Optional<IntegerFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            setQuantity(new IntegerFilter());
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public StringFilter getEdgeBanding() {
        return edgeBanding;
    }

    public Optional<StringFilter> optionalEdgeBanding() {
        return Optional.ofNullable(edgeBanding);
    }

    public StringFilter edgeBanding() {
        if (edgeBanding == null) {
            setEdgeBanding(new StringFilter());
        }
        return edgeBanding;
    }

    public void setEdgeBanding(StringFilter edgeBanding) {
        this.edgeBanding = edgeBanding;
    }

    public StringFilter getGrainDirection() {
        return grainDirection;
    }

    public Optional<StringFilter> optionalGrainDirection() {
        return Optional.ofNullable(grainDirection);
    }

    public StringFilter grainDirection() {
        if (grainDirection == null) {
            setGrainDirection(new StringFilter());
        }
        return grainDirection;
    }

    public void setGrainDirection(StringFilter grainDirection) {
        this.grainDirection = grainDirection;
    }

    public StringFilter getCncOperation() {
        return cncOperation;
    }

    public Optional<StringFilter> optionalCncOperation() {
        return Optional.ofNullable(cncOperation);
    }

    public StringFilter cncOperation() {
        if (cncOperation == null) {
            setCncOperation(new StringFilter());
        }
        return cncOperation;
    }

    public void setCncOperation(StringFilter cncOperation) {
        this.cncOperation = cncOperation;
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

    public LongFilter getMaterialId() {
        return materialId;
    }

    public Optional<LongFilter> optionalMaterialId() {
        return Optional.ofNullable(materialId);
    }

    public LongFilter materialId() {
        if (materialId == null) {
            setMaterialId(new LongFilter());
        }
        return materialId;
    }

    public void setMaterialId(LongFilter materialId) {
        this.materialId = materialId;
    }

    public LongFilter getCabinetId() {
        return cabinetId;
    }

    public Optional<LongFilter> optionalCabinetId() {
        return Optional.ofNullable(cabinetId);
    }

    public LongFilter cabinetId() {
        if (cabinetId == null) {
            setCabinetId(new LongFilter());
        }
        return cabinetId;
    }

    public void setCabinetId(LongFilter cabinetId) {
        this.cabinetId = cabinetId;
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
        final CabinetPartCriteria that = (CabinetPartCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(partCode, that.partCode) &&
            Objects.equals(name, that.name) &&
            Objects.equals(widthMm, that.widthMm) &&
            Objects.equals(heightMm, that.heightMm) &&
            Objects.equals(thicknessMm, that.thicknessMm) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(edgeBanding, that.edgeBanding) &&
            Objects.equals(grainDirection, that.grainDirection) &&
            Objects.equals(cncOperation, that.cncOperation) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(materialId, that.materialId) &&
            Objects.equals(cabinetId, that.cabinetId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            partCode,
            name,
            widthMm,
            heightMm,
            thicknessMm,
            quantity,
            edgeBanding,
            grainDirection,
            cncOperation,
            notes,
            materialId,
            cabinetId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetPartCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPartCode().map(f -> "partCode=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalWidthMm().map(f -> "widthMm=" + f + ", ").orElse("") +
            optionalHeightMm().map(f -> "heightMm=" + f + ", ").orElse("") +
            optionalThicknessMm().map(f -> "thicknessMm=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalEdgeBanding().map(f -> "edgeBanding=" + f + ", ").orElse("") +
            optionalGrainDirection().map(f -> "grainDirection=" + f + ", ").orElse("") +
            optionalCncOperation().map(f -> "cncOperation=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalMaterialId().map(f -> "materialId=" + f + ", ").orElse("") +
            optionalCabinetId().map(f -> "cabinetId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
