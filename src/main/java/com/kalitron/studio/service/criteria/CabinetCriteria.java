package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.FinishType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.Cabinet} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.CabinetResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cabinets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CabinetCategory
     */
    public static class CabinetCategoryFilter extends Filter<CabinetCategory> {

        public CabinetCategoryFilter() {}

        public CabinetCategoryFilter(CabinetCategoryFilter filter) {
            super(filter);
        }

        @Override
        public CabinetCategoryFilter copy() {
            return new CabinetCategoryFilter(this);
        }
    }

    /**
     * Class for filtering FinishType
     */
    public static class FinishTypeFilter extends Filter<FinishType> {

        public FinishTypeFilter() {}

        public FinishTypeFilter(FinishTypeFilter filter) {
            super(filter);
        }

        @Override
        public FinishTypeFilter copy() {
            return new FinishTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter cabinetCode;

    private CabinetCategoryFilter category;

    private StringFilter label;

    private IntegerFilter widthMm;

    private IntegerFilter heightMm;

    private IntegerFilter depthMm;

    private IntegerFilter doors;

    private IntegerFilter drawers;

    private IntegerFilter shelves;

    private FinishTypeFilter finish;

    private IntegerFilter positionX;

    private IntegerFilter positionY;

    private IntegerFilter positionZ;

    private IntegerFilter rotationDeg;

    private IntegerFilter positionSeq;

    private StringFilter notes;

    private LongFilter partsId;

    private LongFilter templateId;

    private LongFilter materialId;

    private LongFilter specId;

    private Boolean distinct;

    public CabinetCriteria() {}

    public CabinetCriteria(CabinetCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.cabinetCode = other.optionalCabinetCode().map(StringFilter::copy).orElse(null);
        this.category = other.optionalCategory().map(CabinetCategoryFilter::copy).orElse(null);
        this.label = other.optionalLabel().map(StringFilter::copy).orElse(null);
        this.widthMm = other.optionalWidthMm().map(IntegerFilter::copy).orElse(null);
        this.heightMm = other.optionalHeightMm().map(IntegerFilter::copy).orElse(null);
        this.depthMm = other.optionalDepthMm().map(IntegerFilter::copy).orElse(null);
        this.doors = other.optionalDoors().map(IntegerFilter::copy).orElse(null);
        this.drawers = other.optionalDrawers().map(IntegerFilter::copy).orElse(null);
        this.shelves = other.optionalShelves().map(IntegerFilter::copy).orElse(null);
        this.finish = other.optionalFinish().map(FinishTypeFilter::copy).orElse(null);
        this.positionX = other.optionalPositionX().map(IntegerFilter::copy).orElse(null);
        this.positionY = other.optionalPositionY().map(IntegerFilter::copy).orElse(null);
        this.positionZ = other.optionalPositionZ().map(IntegerFilter::copy).orElse(null);
        this.rotationDeg = other.optionalRotationDeg().map(IntegerFilter::copy).orElse(null);
        this.positionSeq = other.optionalPositionSeq().map(IntegerFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.partsId = other.optionalPartsId().map(LongFilter::copy).orElse(null);
        this.templateId = other.optionalTemplateId().map(LongFilter::copy).orElse(null);
        this.materialId = other.optionalMaterialId().map(LongFilter::copy).orElse(null);
        this.specId = other.optionalSpecId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CabinetCriteria copy() {
        return new CabinetCriteria(this);
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

    public StringFilter getCabinetCode() {
        return cabinetCode;
    }

    public Optional<StringFilter> optionalCabinetCode() {
        return Optional.ofNullable(cabinetCode);
    }

    public StringFilter cabinetCode() {
        if (cabinetCode == null) {
            setCabinetCode(new StringFilter());
        }
        return cabinetCode;
    }

    public void setCabinetCode(StringFilter cabinetCode) {
        this.cabinetCode = cabinetCode;
    }

    public CabinetCategoryFilter getCategory() {
        return category;
    }

    public Optional<CabinetCategoryFilter> optionalCategory() {
        return Optional.ofNullable(category);
    }

    public CabinetCategoryFilter category() {
        if (category == null) {
            setCategory(new CabinetCategoryFilter());
        }
        return category;
    }

    public void setCategory(CabinetCategoryFilter category) {
        this.category = category;
    }

    public StringFilter getLabel() {
        return label;
    }

    public Optional<StringFilter> optionalLabel() {
        return Optional.ofNullable(label);
    }

    public StringFilter label() {
        if (label == null) {
            setLabel(new StringFilter());
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
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

    public IntegerFilter getDepthMm() {
        return depthMm;
    }

    public Optional<IntegerFilter> optionalDepthMm() {
        return Optional.ofNullable(depthMm);
    }

    public IntegerFilter depthMm() {
        if (depthMm == null) {
            setDepthMm(new IntegerFilter());
        }
        return depthMm;
    }

    public void setDepthMm(IntegerFilter depthMm) {
        this.depthMm = depthMm;
    }

    public IntegerFilter getDoors() {
        return doors;
    }

    public Optional<IntegerFilter> optionalDoors() {
        return Optional.ofNullable(doors);
    }

    public IntegerFilter doors() {
        if (doors == null) {
            setDoors(new IntegerFilter());
        }
        return doors;
    }

    public void setDoors(IntegerFilter doors) {
        this.doors = doors;
    }

    public IntegerFilter getDrawers() {
        return drawers;
    }

    public Optional<IntegerFilter> optionalDrawers() {
        return Optional.ofNullable(drawers);
    }

    public IntegerFilter drawers() {
        if (drawers == null) {
            setDrawers(new IntegerFilter());
        }
        return drawers;
    }

    public void setDrawers(IntegerFilter drawers) {
        this.drawers = drawers;
    }

    public IntegerFilter getShelves() {
        return shelves;
    }

    public Optional<IntegerFilter> optionalShelves() {
        return Optional.ofNullable(shelves);
    }

    public IntegerFilter shelves() {
        if (shelves == null) {
            setShelves(new IntegerFilter());
        }
        return shelves;
    }

    public void setShelves(IntegerFilter shelves) {
        this.shelves = shelves;
    }

    public FinishTypeFilter getFinish() {
        return finish;
    }

    public Optional<FinishTypeFilter> optionalFinish() {
        return Optional.ofNullable(finish);
    }

    public FinishTypeFilter finish() {
        if (finish == null) {
            setFinish(new FinishTypeFilter());
        }
        return finish;
    }

    public void setFinish(FinishTypeFilter finish) {
        this.finish = finish;
    }

    public IntegerFilter getPositionX() {
        return positionX;
    }

    public Optional<IntegerFilter> optionalPositionX() {
        return Optional.ofNullable(positionX);
    }

    public IntegerFilter positionX() {
        if (positionX == null) {
            setPositionX(new IntegerFilter());
        }
        return positionX;
    }

    public void setPositionX(IntegerFilter positionX) {
        this.positionX = positionX;
    }

    public IntegerFilter getPositionY() {
        return positionY;
    }

    public Optional<IntegerFilter> optionalPositionY() {
        return Optional.ofNullable(positionY);
    }

    public IntegerFilter positionY() {
        if (positionY == null) {
            setPositionY(new IntegerFilter());
        }
        return positionY;
    }

    public void setPositionY(IntegerFilter positionY) {
        this.positionY = positionY;
    }

    public IntegerFilter getPositionZ() {
        return positionZ;
    }

    public Optional<IntegerFilter> optionalPositionZ() {
        return Optional.ofNullable(positionZ);
    }

    public IntegerFilter positionZ() {
        if (positionZ == null) {
            setPositionZ(new IntegerFilter());
        }
        return positionZ;
    }

    public void setPositionZ(IntegerFilter positionZ) {
        this.positionZ = positionZ;
    }

    public IntegerFilter getRotationDeg() {
        return rotationDeg;
    }

    public Optional<IntegerFilter> optionalRotationDeg() {
        return Optional.ofNullable(rotationDeg);
    }

    public IntegerFilter rotationDeg() {
        if (rotationDeg == null) {
            setRotationDeg(new IntegerFilter());
        }
        return rotationDeg;
    }

    public void setRotationDeg(IntegerFilter rotationDeg) {
        this.rotationDeg = rotationDeg;
    }

    public IntegerFilter getPositionSeq() {
        return positionSeq;
    }

    public Optional<IntegerFilter> optionalPositionSeq() {
        return Optional.ofNullable(positionSeq);
    }

    public IntegerFilter positionSeq() {
        if (positionSeq == null) {
            setPositionSeq(new IntegerFilter());
        }
        return positionSeq;
    }

    public void setPositionSeq(IntegerFilter positionSeq) {
        this.positionSeq = positionSeq;
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

    public LongFilter getPartsId() {
        return partsId;
    }

    public Optional<LongFilter> optionalPartsId() {
        return Optional.ofNullable(partsId);
    }

    public LongFilter partsId() {
        if (partsId == null) {
            setPartsId(new LongFilter());
        }
        return partsId;
    }

    public void setPartsId(LongFilter partsId) {
        this.partsId = partsId;
    }

    public LongFilter getTemplateId() {
        return templateId;
    }

    public Optional<LongFilter> optionalTemplateId() {
        return Optional.ofNullable(templateId);
    }

    public LongFilter templateId() {
        if (templateId == null) {
            setTemplateId(new LongFilter());
        }
        return templateId;
    }

    public void setTemplateId(LongFilter templateId) {
        this.templateId = templateId;
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

    public LongFilter getSpecId() {
        return specId;
    }

    public Optional<LongFilter> optionalSpecId() {
        return Optional.ofNullable(specId);
    }

    public LongFilter specId() {
        if (specId == null) {
            setSpecId(new LongFilter());
        }
        return specId;
    }

    public void setSpecId(LongFilter specId) {
        this.specId = specId;
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
        final CabinetCriteria that = (CabinetCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cabinetCode, that.cabinetCode) &&
            Objects.equals(category, that.category) &&
            Objects.equals(label, that.label) &&
            Objects.equals(widthMm, that.widthMm) &&
            Objects.equals(heightMm, that.heightMm) &&
            Objects.equals(depthMm, that.depthMm) &&
            Objects.equals(doors, that.doors) &&
            Objects.equals(drawers, that.drawers) &&
            Objects.equals(shelves, that.shelves) &&
            Objects.equals(finish, that.finish) &&
            Objects.equals(positionX, that.positionX) &&
            Objects.equals(positionY, that.positionY) &&
            Objects.equals(positionZ, that.positionZ) &&
            Objects.equals(rotationDeg, that.rotationDeg) &&
            Objects.equals(positionSeq, that.positionSeq) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(partsId, that.partsId) &&
            Objects.equals(templateId, that.templateId) &&
            Objects.equals(materialId, that.materialId) &&
            Objects.equals(specId, that.specId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            cabinetCode,
            category,
            label,
            widthMm,
            heightMm,
            depthMm,
            doors,
            drawers,
            shelves,
            finish,
            positionX,
            positionY,
            positionZ,
            rotationDeg,
            positionSeq,
            notes,
            partsId,
            templateId,
            materialId,
            specId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCabinetCode().map(f -> "cabinetCode=" + f + ", ").orElse("") +
            optionalCategory().map(f -> "category=" + f + ", ").orElse("") +
            optionalLabel().map(f -> "label=" + f + ", ").orElse("") +
            optionalWidthMm().map(f -> "widthMm=" + f + ", ").orElse("") +
            optionalHeightMm().map(f -> "heightMm=" + f + ", ").orElse("") +
            optionalDepthMm().map(f -> "depthMm=" + f + ", ").orElse("") +
            optionalDoors().map(f -> "doors=" + f + ", ").orElse("") +
            optionalDrawers().map(f -> "drawers=" + f + ", ").orElse("") +
            optionalShelves().map(f -> "shelves=" + f + ", ").orElse("") +
            optionalFinish().map(f -> "finish=" + f + ", ").orElse("") +
            optionalPositionX().map(f -> "positionX=" + f + ", ").orElse("") +
            optionalPositionY().map(f -> "positionY=" + f + ", ").orElse("") +
            optionalPositionZ().map(f -> "positionZ=" + f + ", ").orElse("") +
            optionalRotationDeg().map(f -> "rotationDeg=" + f + ", ").orElse("") +
            optionalPositionSeq().map(f -> "positionSeq=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalPartsId().map(f -> "partsId=" + f + ", ").orElse("") +
            optionalTemplateId().map(f -> "templateId=" + f + ", ").orElse("") +
            optionalMaterialId().map(f -> "materialId=" + f + ", ").orElse("") +
            optionalSpecId().map(f -> "specId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
