package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.KitchenSpec} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.KitchenSpecResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /kitchen-specs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KitchenSpecCriteria implements Serializable, Criteria {

    /**
     * Class for filtering KitchenLayout
     */
    public static class KitchenLayoutFilter extends Filter<KitchenLayout> {

        public KitchenLayoutFilter() {}

        public KitchenLayoutFilter(KitchenLayoutFilter filter) {
            super(filter);
        }

        @Override
        public KitchenLayoutFilter copy() {
            return new KitchenLayoutFilter(this);
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

    private KitchenLayoutFilter layout;

    private IntegerFilter totalWidthMm;

    private IntegerFilter totalHeightMm;

    private IntegerFilter totalDepthMm;

    private StringFilter style;

    private FinishTypeFilter primaryFinish;

    private StringFilter handleType;

    private StringFilter countertopMaterial;

    private StringFilter sinkPosition;

    private BooleanFilter confirmedByClient;

    private InstantFilter confirmedAt;

    private LongFilter cabinetsId;

    private LongFilter primaryMaterialId;

    private LongFilter sessionId;

    private Boolean distinct;

    public KitchenSpecCriteria() {}

    public KitchenSpecCriteria(KitchenSpecCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.layout = other.optionalLayout().map(KitchenLayoutFilter::copy).orElse(null);
        this.totalWidthMm = other.optionalTotalWidthMm().map(IntegerFilter::copy).orElse(null);
        this.totalHeightMm = other.optionalTotalHeightMm().map(IntegerFilter::copy).orElse(null);
        this.totalDepthMm = other.optionalTotalDepthMm().map(IntegerFilter::copy).orElse(null);
        this.style = other.optionalStyle().map(StringFilter::copy).orElse(null);
        this.primaryFinish = other.optionalPrimaryFinish().map(FinishTypeFilter::copy).orElse(null);
        this.handleType = other.optionalHandleType().map(StringFilter::copy).orElse(null);
        this.countertopMaterial = other.optionalCountertopMaterial().map(StringFilter::copy).orElse(null);
        this.sinkPosition = other.optionalSinkPosition().map(StringFilter::copy).orElse(null);
        this.confirmedByClient = other.optionalConfirmedByClient().map(BooleanFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.cabinetsId = other.optionalCabinetsId().map(LongFilter::copy).orElse(null);
        this.primaryMaterialId = other.optionalPrimaryMaterialId().map(LongFilter::copy).orElse(null);
        this.sessionId = other.optionalSessionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public KitchenSpecCriteria copy() {
        return new KitchenSpecCriteria(this);
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

    public KitchenLayoutFilter getLayout() {
        return layout;
    }

    public Optional<KitchenLayoutFilter> optionalLayout() {
        return Optional.ofNullable(layout);
    }

    public KitchenLayoutFilter layout() {
        if (layout == null) {
            setLayout(new KitchenLayoutFilter());
        }
        return layout;
    }

    public void setLayout(KitchenLayoutFilter layout) {
        this.layout = layout;
    }

    public IntegerFilter getTotalWidthMm() {
        return totalWidthMm;
    }

    public Optional<IntegerFilter> optionalTotalWidthMm() {
        return Optional.ofNullable(totalWidthMm);
    }

    public IntegerFilter totalWidthMm() {
        if (totalWidthMm == null) {
            setTotalWidthMm(new IntegerFilter());
        }
        return totalWidthMm;
    }

    public void setTotalWidthMm(IntegerFilter totalWidthMm) {
        this.totalWidthMm = totalWidthMm;
    }

    public IntegerFilter getTotalHeightMm() {
        return totalHeightMm;
    }

    public Optional<IntegerFilter> optionalTotalHeightMm() {
        return Optional.ofNullable(totalHeightMm);
    }

    public IntegerFilter totalHeightMm() {
        if (totalHeightMm == null) {
            setTotalHeightMm(new IntegerFilter());
        }
        return totalHeightMm;
    }

    public void setTotalHeightMm(IntegerFilter totalHeightMm) {
        this.totalHeightMm = totalHeightMm;
    }

    public IntegerFilter getTotalDepthMm() {
        return totalDepthMm;
    }

    public Optional<IntegerFilter> optionalTotalDepthMm() {
        return Optional.ofNullable(totalDepthMm);
    }

    public IntegerFilter totalDepthMm() {
        if (totalDepthMm == null) {
            setTotalDepthMm(new IntegerFilter());
        }
        return totalDepthMm;
    }

    public void setTotalDepthMm(IntegerFilter totalDepthMm) {
        this.totalDepthMm = totalDepthMm;
    }

    public StringFilter getStyle() {
        return style;
    }

    public Optional<StringFilter> optionalStyle() {
        return Optional.ofNullable(style);
    }

    public StringFilter style() {
        if (style == null) {
            setStyle(new StringFilter());
        }
        return style;
    }

    public void setStyle(StringFilter style) {
        this.style = style;
    }

    public FinishTypeFilter getPrimaryFinish() {
        return primaryFinish;
    }

    public Optional<FinishTypeFilter> optionalPrimaryFinish() {
        return Optional.ofNullable(primaryFinish);
    }

    public FinishTypeFilter primaryFinish() {
        if (primaryFinish == null) {
            setPrimaryFinish(new FinishTypeFilter());
        }
        return primaryFinish;
    }

    public void setPrimaryFinish(FinishTypeFilter primaryFinish) {
        this.primaryFinish = primaryFinish;
    }

    public StringFilter getHandleType() {
        return handleType;
    }

    public Optional<StringFilter> optionalHandleType() {
        return Optional.ofNullable(handleType);
    }

    public StringFilter handleType() {
        if (handleType == null) {
            setHandleType(new StringFilter());
        }
        return handleType;
    }

    public void setHandleType(StringFilter handleType) {
        this.handleType = handleType;
    }

    public StringFilter getCountertopMaterial() {
        return countertopMaterial;
    }

    public Optional<StringFilter> optionalCountertopMaterial() {
        return Optional.ofNullable(countertopMaterial);
    }

    public StringFilter countertopMaterial() {
        if (countertopMaterial == null) {
            setCountertopMaterial(new StringFilter());
        }
        return countertopMaterial;
    }

    public void setCountertopMaterial(StringFilter countertopMaterial) {
        this.countertopMaterial = countertopMaterial;
    }

    public StringFilter getSinkPosition() {
        return sinkPosition;
    }

    public Optional<StringFilter> optionalSinkPosition() {
        return Optional.ofNullable(sinkPosition);
    }

    public StringFilter sinkPosition() {
        if (sinkPosition == null) {
            setSinkPosition(new StringFilter());
        }
        return sinkPosition;
    }

    public void setSinkPosition(StringFilter sinkPosition) {
        this.sinkPosition = sinkPosition;
    }

    public BooleanFilter getConfirmedByClient() {
        return confirmedByClient;
    }

    public Optional<BooleanFilter> optionalConfirmedByClient() {
        return Optional.ofNullable(confirmedByClient);
    }

    public BooleanFilter confirmedByClient() {
        if (confirmedByClient == null) {
            setConfirmedByClient(new BooleanFilter());
        }
        return confirmedByClient;
    }

    public void setConfirmedByClient(BooleanFilter confirmedByClient) {
        this.confirmedByClient = confirmedByClient;
    }

    public InstantFilter getConfirmedAt() {
        return confirmedAt;
    }

    public Optional<InstantFilter> optionalConfirmedAt() {
        return Optional.ofNullable(confirmedAt);
    }

    public InstantFilter confirmedAt() {
        if (confirmedAt == null) {
            setConfirmedAt(new InstantFilter());
        }
        return confirmedAt;
    }

    public void setConfirmedAt(InstantFilter confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LongFilter getCabinetsId() {
        return cabinetsId;
    }

    public Optional<LongFilter> optionalCabinetsId() {
        return Optional.ofNullable(cabinetsId);
    }

    public LongFilter cabinetsId() {
        if (cabinetsId == null) {
            setCabinetsId(new LongFilter());
        }
        return cabinetsId;
    }

    public void setCabinetsId(LongFilter cabinetsId) {
        this.cabinetsId = cabinetsId;
    }

    public LongFilter getPrimaryMaterialId() {
        return primaryMaterialId;
    }

    public Optional<LongFilter> optionalPrimaryMaterialId() {
        return Optional.ofNullable(primaryMaterialId);
    }

    public LongFilter primaryMaterialId() {
        if (primaryMaterialId == null) {
            setPrimaryMaterialId(new LongFilter());
        }
        return primaryMaterialId;
    }

    public void setPrimaryMaterialId(LongFilter primaryMaterialId) {
        this.primaryMaterialId = primaryMaterialId;
    }

    public LongFilter getSessionId() {
        return sessionId;
    }

    public Optional<LongFilter> optionalSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public LongFilter sessionId() {
        if (sessionId == null) {
            setSessionId(new LongFilter());
        }
        return sessionId;
    }

    public void setSessionId(LongFilter sessionId) {
        this.sessionId = sessionId;
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
        final KitchenSpecCriteria that = (KitchenSpecCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(layout, that.layout) &&
            Objects.equals(totalWidthMm, that.totalWidthMm) &&
            Objects.equals(totalHeightMm, that.totalHeightMm) &&
            Objects.equals(totalDepthMm, that.totalDepthMm) &&
            Objects.equals(style, that.style) &&
            Objects.equals(primaryFinish, that.primaryFinish) &&
            Objects.equals(handleType, that.handleType) &&
            Objects.equals(countertopMaterial, that.countertopMaterial) &&
            Objects.equals(sinkPosition, that.sinkPosition) &&
            Objects.equals(confirmedByClient, that.confirmedByClient) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(cabinetsId, that.cabinetsId) &&
            Objects.equals(primaryMaterialId, that.primaryMaterialId) &&
            Objects.equals(sessionId, that.sessionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            layout,
            totalWidthMm,
            totalHeightMm,
            totalDepthMm,
            style,
            primaryFinish,
            handleType,
            countertopMaterial,
            sinkPosition,
            confirmedByClient,
            confirmedAt,
            cabinetsId,
            primaryMaterialId,
            sessionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KitchenSpecCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLayout().map(f -> "layout=" + f + ", ").orElse("") +
            optionalTotalWidthMm().map(f -> "totalWidthMm=" + f + ", ").orElse("") +
            optionalTotalHeightMm().map(f -> "totalHeightMm=" + f + ", ").orElse("") +
            optionalTotalDepthMm().map(f -> "totalDepthMm=" + f + ", ").orElse("") +
            optionalStyle().map(f -> "style=" + f + ", ").orElse("") +
            optionalPrimaryFinish().map(f -> "primaryFinish=" + f + ", ").orElse("") +
            optionalHandleType().map(f -> "handleType=" + f + ", ").orElse("") +
            optionalCountertopMaterial().map(f -> "countertopMaterial=" + f + ", ").orElse("") +
            optionalSinkPosition().map(f -> "sinkPosition=" + f + ", ").orElse("") +
            optionalConfirmedByClient().map(f -> "confirmedByClient=" + f + ", ").orElse("") +
            optionalConfirmedAt().map(f -> "confirmedAt=" + f + ", ").orElse("") +
            optionalCabinetsId().map(f -> "cabinetsId=" + f + ", ").orElse("") +
            optionalPrimaryMaterialId().map(f -> "primaryMaterialId=" + f + ", ").orElse("") +
            optionalSessionId().map(f -> "sessionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
