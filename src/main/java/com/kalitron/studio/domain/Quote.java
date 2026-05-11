package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.QuoteStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Quote.
 */
@Entity
@Table(name = "quote")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Quote implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "quote_number", length = 20, nullable = false, unique = true)
    private String quoteNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuoteStatus status;

    @NotNull
    @Column(name = "subtotal_mxn", precision = 21, scale = 2, nullable = false)
    private BigDecimal subtotalMxn;

    @NotNull
    @Column(name = "tax_mxn", precision = 21, scale = 2, nullable = false)
    private BigDecimal taxMxn;

    @NotNull
    @Column(name = "total_mxn", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalMxn;

    @Column(name = "labor_mxn", precision = 21, scale = 2)
    private BigDecimal laborMxn;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Size(max = 80)
    @Column(name = "public_token", length = 80, unique = true)
    private String publicToken;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    private String notes;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quote")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cabinet", "hardware", "quote" }, allowSetters = true)
    private Set<QuoteItem> itemses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private DesignImage renderImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private DesignArtifact pdfArtifact;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "spec", "messageses", "imageses", "artifactses", "jobses", "quoteses", "wallses", "obstacleses", "catalogStyle" },
        allowSetters = true
    )
    private DesignSession session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Quote id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuoteNumber() {
        return this.quoteNumber;
    }

    public Quote quoteNumber(String quoteNumber) {
        this.setQuoteNumber(quoteNumber);
        return this;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public QuoteStatus getStatus() {
        return this.status;
    }

    public Quote status(QuoteStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotalMxn() {
        return this.subtotalMxn;
    }

    public Quote subtotalMxn(BigDecimal subtotalMxn) {
        this.setSubtotalMxn(subtotalMxn);
        return this;
    }

    public void setSubtotalMxn(BigDecimal subtotalMxn) {
        this.subtotalMxn = subtotalMxn;
    }

    public BigDecimal getTaxMxn() {
        return this.taxMxn;
    }

    public Quote taxMxn(BigDecimal taxMxn) {
        this.setTaxMxn(taxMxn);
        return this;
    }

    public void setTaxMxn(BigDecimal taxMxn) {
        this.taxMxn = taxMxn;
    }

    public BigDecimal getTotalMxn() {
        return this.totalMxn;
    }

    public Quote totalMxn(BigDecimal totalMxn) {
        this.setTotalMxn(totalMxn);
        return this;
    }

    public void setTotalMxn(BigDecimal totalMxn) {
        this.totalMxn = totalMxn;
    }

    public BigDecimal getLaborMxn() {
        return this.laborMxn;
    }

    public Quote laborMxn(BigDecimal laborMxn) {
        this.setLaborMxn(laborMxn);
        return this;
    }

    public void setLaborMxn(BigDecimal laborMxn) {
        this.laborMxn = laborMxn;
    }

    public LocalDate getValidUntil() {
        return this.validUntil;
    }

    public Quote validUntil(LocalDate validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getPublicToken() {
        return this.publicToken;
    }

    public Quote publicToken(String publicToken) {
        this.setPublicToken(publicToken);
        return this;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public String getNotes() {
        return this.notes;
    }

    public Quote notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Quote createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public Quote sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Set<QuoteItem> getItemses() {
        return this.itemses;
    }

    public void setItemses(Set<QuoteItem> quoteItems) {
        if (this.itemses != null) {
            this.itemses.forEach(i -> i.setQuote(null));
        }
        if (quoteItems != null) {
            quoteItems.forEach(i -> i.setQuote(this));
        }
        this.itemses = quoteItems;
    }

    public Quote itemses(Set<QuoteItem> quoteItems) {
        this.setItemses(quoteItems);
        return this;
    }

    public Quote addItems(QuoteItem quoteItem) {
        this.itemses.add(quoteItem);
        quoteItem.setQuote(this);
        return this;
    }

    public Quote removeItems(QuoteItem quoteItem) {
        this.itemses.remove(quoteItem);
        quoteItem.setQuote(null);
        return this;
    }

    public DesignImage getRenderImage() {
        return this.renderImage;
    }

    public void setRenderImage(DesignImage designImage) {
        this.renderImage = designImage;
    }

    public Quote renderImage(DesignImage designImage) {
        this.setRenderImage(designImage);
        return this;
    }

    public DesignArtifact getPdfArtifact() {
        return this.pdfArtifact;
    }

    public void setPdfArtifact(DesignArtifact designArtifact) {
        this.pdfArtifact = designArtifact;
    }

    public Quote pdfArtifact(DesignArtifact designArtifact) {
        this.setPdfArtifact(designArtifact);
        return this;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public Quote session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quote)) {
            return false;
        }
        return getId() != null && getId().equals(((Quote) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Quote{" +
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
            "}";
    }
}
