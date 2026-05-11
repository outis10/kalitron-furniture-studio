package com.kalitron.studio.service.criteria;

import com.kalitron.studio.domain.enumeration.QuoteStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.kalitron.studio.domain.Quote} entity. This class is used
 * in {@link com.kalitron.studio.web.rest.QuoteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /quotes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuoteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering QuoteStatus
     */
    public static class QuoteStatusFilter extends Filter<QuoteStatus> {

        public QuoteStatusFilter() {}

        public QuoteStatusFilter(QuoteStatusFilter filter) {
            super(filter);
        }

        @Override
        public QuoteStatusFilter copy() {
            return new QuoteStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter quoteNumber;

    private QuoteStatusFilter status;

    private BigDecimalFilter subtotalMxn;

    private BigDecimalFilter taxMxn;

    private BigDecimalFilter totalMxn;

    private BigDecimalFilter laborMxn;

    private LocalDateFilter validUntil;

    private StringFilter publicToken;

    private StringFilter notes;

    private InstantFilter createdAt;

    private InstantFilter sentAt;

    private LongFilter itemsId;

    private LongFilter renderImageId;

    private LongFilter pdfArtifactId;

    private LongFilter sessionId;

    private Boolean distinct;

    public QuoteCriteria() {}

    public QuoteCriteria(QuoteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quoteNumber = other.optionalQuoteNumber().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(QuoteStatusFilter::copy).orElse(null);
        this.subtotalMxn = other.optionalSubtotalMxn().map(BigDecimalFilter::copy).orElse(null);
        this.taxMxn = other.optionalTaxMxn().map(BigDecimalFilter::copy).orElse(null);
        this.totalMxn = other.optionalTotalMxn().map(BigDecimalFilter::copy).orElse(null);
        this.laborMxn = other.optionalLaborMxn().map(BigDecimalFilter::copy).orElse(null);
        this.validUntil = other.optionalValidUntil().map(LocalDateFilter::copy).orElse(null);
        this.publicToken = other.optionalPublicToken().map(StringFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(InstantFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.renderImageId = other.optionalRenderImageId().map(LongFilter::copy).orElse(null);
        this.pdfArtifactId = other.optionalPdfArtifactId().map(LongFilter::copy).orElse(null);
        this.sessionId = other.optionalSessionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuoteCriteria copy() {
        return new QuoteCriteria(this);
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

    public StringFilter getQuoteNumber() {
        return quoteNumber;
    }

    public Optional<StringFilter> optionalQuoteNumber() {
        return Optional.ofNullable(quoteNumber);
    }

    public StringFilter quoteNumber() {
        if (quoteNumber == null) {
            setQuoteNumber(new StringFilter());
        }
        return quoteNumber;
    }

    public void setQuoteNumber(StringFilter quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public QuoteStatusFilter getStatus() {
        return status;
    }

    public Optional<QuoteStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public QuoteStatusFilter status() {
        if (status == null) {
            setStatus(new QuoteStatusFilter());
        }
        return status;
    }

    public void setStatus(QuoteStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getSubtotalMxn() {
        return subtotalMxn;
    }

    public Optional<BigDecimalFilter> optionalSubtotalMxn() {
        return Optional.ofNullable(subtotalMxn);
    }

    public BigDecimalFilter subtotalMxn() {
        if (subtotalMxn == null) {
            setSubtotalMxn(new BigDecimalFilter());
        }
        return subtotalMxn;
    }

    public void setSubtotalMxn(BigDecimalFilter subtotalMxn) {
        this.subtotalMxn = subtotalMxn;
    }

    public BigDecimalFilter getTaxMxn() {
        return taxMxn;
    }

    public Optional<BigDecimalFilter> optionalTaxMxn() {
        return Optional.ofNullable(taxMxn);
    }

    public BigDecimalFilter taxMxn() {
        if (taxMxn == null) {
            setTaxMxn(new BigDecimalFilter());
        }
        return taxMxn;
    }

    public void setTaxMxn(BigDecimalFilter taxMxn) {
        this.taxMxn = taxMxn;
    }

    public BigDecimalFilter getTotalMxn() {
        return totalMxn;
    }

    public Optional<BigDecimalFilter> optionalTotalMxn() {
        return Optional.ofNullable(totalMxn);
    }

    public BigDecimalFilter totalMxn() {
        if (totalMxn == null) {
            setTotalMxn(new BigDecimalFilter());
        }
        return totalMxn;
    }

    public void setTotalMxn(BigDecimalFilter totalMxn) {
        this.totalMxn = totalMxn;
    }

    public BigDecimalFilter getLaborMxn() {
        return laborMxn;
    }

    public Optional<BigDecimalFilter> optionalLaborMxn() {
        return Optional.ofNullable(laborMxn);
    }

    public BigDecimalFilter laborMxn() {
        if (laborMxn == null) {
            setLaborMxn(new BigDecimalFilter());
        }
        return laborMxn;
    }

    public void setLaborMxn(BigDecimalFilter laborMxn) {
        this.laborMxn = laborMxn;
    }

    public LocalDateFilter getValidUntil() {
        return validUntil;
    }

    public Optional<LocalDateFilter> optionalValidUntil() {
        return Optional.ofNullable(validUntil);
    }

    public LocalDateFilter validUntil() {
        if (validUntil == null) {
            setValidUntil(new LocalDateFilter());
        }
        return validUntil;
    }

    public void setValidUntil(LocalDateFilter validUntil) {
        this.validUntil = validUntil;
    }

    public StringFilter getPublicToken() {
        return publicToken;
    }

    public Optional<StringFilter> optionalPublicToken() {
        return Optional.ofNullable(publicToken);
    }

    public StringFilter publicToken() {
        if (publicToken == null) {
            setPublicToken(new StringFilter());
        }
        return publicToken;
    }

    public void setPublicToken(StringFilter publicToken) {
        this.publicToken = publicToken;
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

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getSentAt() {
        return sentAt;
    }

    public Optional<InstantFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public InstantFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new InstantFilter());
        }
        return sentAt;
    }

    public void setSentAt(InstantFilter sentAt) {
        this.sentAt = sentAt;
    }

    public LongFilter getItemsId() {
        return itemsId;
    }

    public Optional<LongFilter> optionalItemsId() {
        return Optional.ofNullable(itemsId);
    }

    public LongFilter itemsId() {
        if (itemsId == null) {
            setItemsId(new LongFilter());
        }
        return itemsId;
    }

    public void setItemsId(LongFilter itemsId) {
        this.itemsId = itemsId;
    }

    public LongFilter getRenderImageId() {
        return renderImageId;
    }

    public Optional<LongFilter> optionalRenderImageId() {
        return Optional.ofNullable(renderImageId);
    }

    public LongFilter renderImageId() {
        if (renderImageId == null) {
            setRenderImageId(new LongFilter());
        }
        return renderImageId;
    }

    public void setRenderImageId(LongFilter renderImageId) {
        this.renderImageId = renderImageId;
    }

    public LongFilter getPdfArtifactId() {
        return pdfArtifactId;
    }

    public Optional<LongFilter> optionalPdfArtifactId() {
        return Optional.ofNullable(pdfArtifactId);
    }

    public LongFilter pdfArtifactId() {
        if (pdfArtifactId == null) {
            setPdfArtifactId(new LongFilter());
        }
        return pdfArtifactId;
    }

    public void setPdfArtifactId(LongFilter pdfArtifactId) {
        this.pdfArtifactId = pdfArtifactId;
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
        final QuoteCriteria that = (QuoteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quoteNumber, that.quoteNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(subtotalMxn, that.subtotalMxn) &&
            Objects.equals(taxMxn, that.taxMxn) &&
            Objects.equals(totalMxn, that.totalMxn) &&
            Objects.equals(laborMxn, that.laborMxn) &&
            Objects.equals(validUntil, that.validUntil) &&
            Objects.equals(publicToken, that.publicToken) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(itemsId, that.itemsId) &&
            Objects.equals(renderImageId, that.renderImageId) &&
            Objects.equals(pdfArtifactId, that.pdfArtifactId) &&
            Objects.equals(sessionId, that.sessionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            quoteNumber,
            status,
            subtotalMxn,
            taxMxn,
            totalMxn,
            laborMxn,
            validUntil,
            publicToken,
            notes,
            createdAt,
            sentAt,
            itemsId,
            renderImageId,
            pdfArtifactId,
            sessionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuoteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuoteNumber().map(f -> "quoteNumber=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalSubtotalMxn().map(f -> "subtotalMxn=" + f + ", ").orElse("") +
            optionalTaxMxn().map(f -> "taxMxn=" + f + ", ").orElse("") +
            optionalTotalMxn().map(f -> "totalMxn=" + f + ", ").orElse("") +
            optionalLaborMxn().map(f -> "laborMxn=" + f + ", ").orElse("") +
            optionalValidUntil().map(f -> "validUntil=" + f + ", ").orElse("") +
            optionalPublicToken().map(f -> "publicToken=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
            optionalRenderImageId().map(f -> "renderImageId=" + f + ", ").orElse("") +
            optionalPdfArtifactId().map(f -> "pdfArtifactId=" + f + ", ").orElse("") +
            optionalSessionId().map(f -> "sessionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
