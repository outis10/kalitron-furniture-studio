package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.QuoteStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.Quote} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuoteDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String quoteNumber;

    @NotNull
    private QuoteStatus status;

    @NotNull
    private BigDecimal subtotalMxn;

    @NotNull
    private BigDecimal taxMxn;

    @NotNull
    private BigDecimal totalMxn;

    private BigDecimal laborMxn;

    private LocalDate validUntil;

    @Size(max = 80)
    private String publicToken;

    @Size(max = 1000)
    private String notes;

    @NotNull
    private Instant createdAt;

    private Instant sentAt;

    private DesignImageDTO renderImage;

    private DesignArtifactDTO pdfArtifact;

    @NotNull
    private DesignSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotalMxn() {
        return subtotalMxn;
    }

    public void setSubtotalMxn(BigDecimal subtotalMxn) {
        this.subtotalMxn = subtotalMxn;
    }

    public BigDecimal getTaxMxn() {
        return taxMxn;
    }

    public void setTaxMxn(BigDecimal taxMxn) {
        this.taxMxn = taxMxn;
    }

    public BigDecimal getTotalMxn() {
        return totalMxn;
    }

    public void setTotalMxn(BigDecimal totalMxn) {
        this.totalMxn = totalMxn;
    }

    public BigDecimal getLaborMxn() {
        return laborMxn;
    }

    public void setLaborMxn(BigDecimal laborMxn) {
        this.laborMxn = laborMxn;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public DesignImageDTO getRenderImage() {
        return renderImage;
    }

    public void setRenderImage(DesignImageDTO renderImage) {
        this.renderImage = renderImage;
    }

    public DesignArtifactDTO getPdfArtifact() {
        return pdfArtifact;
    }

    public void setPdfArtifact(DesignArtifactDTO pdfArtifact) {
        this.pdfArtifact = pdfArtifact;
    }

    public DesignSessionDTO getSession() {
        return session;
    }

    public void setSession(DesignSessionDTO session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuoteDTO)) {
            return false;
        }

        QuoteDTO quoteDTO = (QuoteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, quoteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuoteDTO{" +
            "id=" + getId() +
            ", quoteNumber='" + getQuoteNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", subtotalMxn=" + getSubtotalMxn() +
            ", taxMxn=" + getTaxMxn() +
            ", totalMxn=" + getTotalMxn() +
            ", laborMxn=" + getLaborMxn() +
            ", validUntil='" + getValidUntil() + "'" +
            ", publicToken='" + getPublicToken() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", renderImage=" + getRenderImage() +
            ", pdfArtifact=" + getPdfArtifact() +
            ", session=" + getSession() +
            "}";
    }
}
