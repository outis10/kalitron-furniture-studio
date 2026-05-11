package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.CabinetCategory;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.CabinetTemplate} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.CabinetTemplateResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cabinet-templates?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CabinetTemplateCriteria implements Serializable, Criteria {

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

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private CabinetCategoryFilter category;

    private IntegerFilter defaultWidthMm;

    private IntegerFilter defaultHeightMm;

    private IntegerFilter defaultDepthMm;

    private IntegerFilter minWidthMm;

    private IntegerFilter maxWidthMm;

    private BooleanFilter supportsDoors;

    private BooleanFilter supportsDrawers;

    private BooleanFilter supportsShelves;

    private StringFilter fusionTemplateName;

    private BooleanFilter isActive;

    private IntegerFilter sortOrder;

    private Boolean distinct;

    public CabinetTemplateCriteria() {}

    public CabinetTemplateCriteria(CabinetTemplateCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.category = other.optionalCategory().map(CabinetCategoryFilter::copy).orElse(null);
        this.defaultWidthMm = other.optionalDefaultWidthMm().map(IntegerFilter::copy).orElse(null);
        this.defaultHeightMm = other.optionalDefaultHeightMm().map(IntegerFilter::copy).orElse(null);
        this.defaultDepthMm = other.optionalDefaultDepthMm().map(IntegerFilter::copy).orElse(null);
        this.minWidthMm = other.optionalMinWidthMm().map(IntegerFilter::copy).orElse(null);
        this.maxWidthMm = other.optionalMaxWidthMm().map(IntegerFilter::copy).orElse(null);
        this.supportsDoors = other.optionalSupportsDoors().map(BooleanFilter::copy).orElse(null);
        this.supportsDrawers = other.optionalSupportsDrawers().map(BooleanFilter::copy).orElse(null);
        this.supportsShelves = other.optionalSupportsShelves().map(BooleanFilter::copy).orElse(null);
        this.fusionTemplateName = other.optionalFusionTemplateName().map(StringFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.sortOrder = other.optionalSortOrder().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CabinetTemplateCriteria copy() {
        return new CabinetTemplateCriteria(this);
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

    public IntegerFilter getDefaultWidthMm() {
        return defaultWidthMm;
    }

    public Optional<IntegerFilter> optionalDefaultWidthMm() {
        return Optional.ofNullable(defaultWidthMm);
    }

    public IntegerFilter defaultWidthMm() {
        if (defaultWidthMm == null) {
            setDefaultWidthMm(new IntegerFilter());
        }
        return defaultWidthMm;
    }

    public void setDefaultWidthMm(IntegerFilter defaultWidthMm) {
        this.defaultWidthMm = defaultWidthMm;
    }

    public IntegerFilter getDefaultHeightMm() {
        return defaultHeightMm;
    }

    public Optional<IntegerFilter> optionalDefaultHeightMm() {
        return Optional.ofNullable(defaultHeightMm);
    }

    public IntegerFilter defaultHeightMm() {
        if (defaultHeightMm == null) {
            setDefaultHeightMm(new IntegerFilter());
        }
        return defaultHeightMm;
    }

    public void setDefaultHeightMm(IntegerFilter defaultHeightMm) {
        this.defaultHeightMm = defaultHeightMm;
    }

    public IntegerFilter getDefaultDepthMm() {
        return defaultDepthMm;
    }

    public Optional<IntegerFilter> optionalDefaultDepthMm() {
        return Optional.ofNullable(defaultDepthMm);
    }

    public IntegerFilter defaultDepthMm() {
        if (defaultDepthMm == null) {
            setDefaultDepthMm(new IntegerFilter());
        }
        return defaultDepthMm;
    }

    public void setDefaultDepthMm(IntegerFilter defaultDepthMm) {
        this.defaultDepthMm = defaultDepthMm;
    }

    public IntegerFilter getMinWidthMm() {
        return minWidthMm;
    }

    public Optional<IntegerFilter> optionalMinWidthMm() {
        return Optional.ofNullable(minWidthMm);
    }

    public IntegerFilter minWidthMm() {
        if (minWidthMm == null) {
            setMinWidthMm(new IntegerFilter());
        }
        return minWidthMm;
    }

    public void setMinWidthMm(IntegerFilter minWidthMm) {
        this.minWidthMm = minWidthMm;
    }

    public IntegerFilter getMaxWidthMm() {
        return maxWidthMm;
    }

    public Optional<IntegerFilter> optionalMaxWidthMm() {
        return Optional.ofNullable(maxWidthMm);
    }

    public IntegerFilter maxWidthMm() {
        if (maxWidthMm == null) {
            setMaxWidthMm(new IntegerFilter());
        }
        return maxWidthMm;
    }

    public void setMaxWidthMm(IntegerFilter maxWidthMm) {
        this.maxWidthMm = maxWidthMm;
    }

    public BooleanFilter getSupportsDoors() {
        return supportsDoors;
    }

    public Optional<BooleanFilter> optionalSupportsDoors() {
        return Optional.ofNullable(supportsDoors);
    }

    public BooleanFilter supportsDoors() {
        if (supportsDoors == null) {
            setSupportsDoors(new BooleanFilter());
        }
        return supportsDoors;
    }

    public void setSupportsDoors(BooleanFilter supportsDoors) {
        this.supportsDoors = supportsDoors;
    }

    public BooleanFilter getSupportsDrawers() {
        return supportsDrawers;
    }

    public Optional<BooleanFilter> optionalSupportsDrawers() {
        return Optional.ofNullable(supportsDrawers);
    }

    public BooleanFilter supportsDrawers() {
        if (supportsDrawers == null) {
            setSupportsDrawers(new BooleanFilter());
        }
        return supportsDrawers;
    }

    public void setSupportsDrawers(BooleanFilter supportsDrawers) {
        this.supportsDrawers = supportsDrawers;
    }

    public BooleanFilter getSupportsShelves() {
        return supportsShelves;
    }

    public Optional<BooleanFilter> optionalSupportsShelves() {
        return Optional.ofNullable(supportsShelves);
    }

    public BooleanFilter supportsShelves() {
        if (supportsShelves == null) {
            setSupportsShelves(new BooleanFilter());
        }
        return supportsShelves;
    }

    public void setSupportsShelves(BooleanFilter supportsShelves) {
        this.supportsShelves = supportsShelves;
    }

    public StringFilter getFusionTemplateName() {
        return fusionTemplateName;
    }

    public Optional<StringFilter> optionalFusionTemplateName() {
        return Optional.ofNullable(fusionTemplateName);
    }

    public StringFilter fusionTemplateName() {
        if (fusionTemplateName == null) {
            setFusionTemplateName(new StringFilter());
        }
        return fusionTemplateName;
    }

    public void setFusionTemplateName(StringFilter fusionTemplateName) {
        this.fusionTemplateName = fusionTemplateName;
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

    public IntegerFilter getSortOrder() {
        return sortOrder;
    }

    public Optional<IntegerFilter> optionalSortOrder() {
        return Optional.ofNullable(sortOrder);
    }

    public IntegerFilter sortOrder() {
        if (sortOrder == null) {
            setSortOrder(new IntegerFilter());
        }
        return sortOrder;
    }

    public void setSortOrder(IntegerFilter sortOrder) {
        this.sortOrder = sortOrder;
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
        final CabinetTemplateCriteria that = (CabinetTemplateCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(category, that.category) &&
            Objects.equals(defaultWidthMm, that.defaultWidthMm) &&
            Objects.equals(defaultHeightMm, that.defaultHeightMm) &&
            Objects.equals(defaultDepthMm, that.defaultDepthMm) &&
            Objects.equals(minWidthMm, that.minWidthMm) &&
            Objects.equals(maxWidthMm, that.maxWidthMm) &&
            Objects.equals(supportsDoors, that.supportsDoors) &&
            Objects.equals(supportsDrawers, that.supportsDrawers) &&
            Objects.equals(supportsShelves, that.supportsShelves) &&
            Objects.equals(fusionTemplateName, that.fusionTemplateName) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(sortOrder, that.sortOrder) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            name,
            category,
            defaultWidthMm,
            defaultHeightMm,
            defaultDepthMm,
            minWidthMm,
            maxWidthMm,
            supportsDoors,
            supportsDrawers,
            supportsShelves,
            fusionTemplateName,
            isActive,
            sortOrder,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CabinetTemplateCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCategory().map(f -> "category=" + f + ", ").orElse("") +
            optionalDefaultWidthMm().map(f -> "defaultWidthMm=" + f + ", ").orElse("") +
            optionalDefaultHeightMm().map(f -> "defaultHeightMm=" + f + ", ").orElse("") +
            optionalDefaultDepthMm().map(f -> "defaultDepthMm=" + f + ", ").orElse("") +
            optionalMinWidthMm().map(f -> "minWidthMm=" + f + ", ").orElse("") +
            optionalMaxWidthMm().map(f -> "maxWidthMm=" + f + ", ").orElse("") +
            optionalSupportsDoors().map(f -> "supportsDoors=" + f + ", ").orElse("") +
            optionalSupportsDrawers().map(f -> "supportsDrawers=" + f + ", ").orElse("") +
            optionalSupportsShelves().map(f -> "supportsShelves=" + f + ", ").orElse("") +
            optionalFusionTemplateName().map(f -> "fusionTemplateName=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalSortOrder().map(f -> "sortOrder=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
