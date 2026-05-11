package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A QuoteItem.
 */
@Entity
@Table(name = "quote_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuoteItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "description", length = 200, nullable = false)
    private String description;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "unit_price_mxn", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPriceMxn;

    @NotNull
    @Column(name = "total_mxn", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalMxn;

    @Size(max = 60)
    @Column(name = "category", length = 60)
    private String category;

    @Size(max = 200)
    @Column(name = "notes", length = 200)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "partses", "template", "material", "spec" }, allowSetters = true)
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hardware hardware;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "itemses", "renderImage", "pdfArtifact", "session" }, allowSetters = true)
    private Quote quote;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public QuoteItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public QuoteItem description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public QuoteItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceMxn() {
        return this.unitPriceMxn;
    }

    public QuoteItem unitPriceMxn(BigDecimal unitPriceMxn) {
        this.setUnitPriceMxn(unitPriceMxn);
        return this;
    }

    public void setUnitPriceMxn(BigDecimal unitPriceMxn) {
        this.unitPriceMxn = unitPriceMxn;
    }

    public BigDecimal getTotalMxn() {
        return this.totalMxn;
    }

    public QuoteItem totalMxn(BigDecimal totalMxn) {
        this.setTotalMxn(totalMxn);
        return this;
    }

    public void setTotalMxn(BigDecimal totalMxn) {
        this.totalMxn = totalMxn;
    }

    public String getCategory() {
        return this.category;
    }

    public QuoteItem category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return this.notes;
    }

    public QuoteItem notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Cabinet getCabinet() {
        return this.cabinet;
    }

    public void setCabinet(Cabinet cabinet) {
        this.cabinet = cabinet;
    }

    public QuoteItem cabinet(Cabinet cabinet) {
        this.setCabinet(cabinet);
        return this;
    }

    public Hardware getHardware() {
        return this.hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public QuoteItem hardware(Hardware hardware) {
        this.setHardware(hardware);
        return this;
    }

    public Quote getQuote() {
        return this.quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public QuoteItem quote(Quote quote) {
        this.setQuote(quote);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuoteItem)) {
            return false;
        }
        return getId() != null && getId().equals(((QuoteItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuoteItem{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPriceMxn=" + getUnitPriceMxn() +
            ", totalMxn=" + getTotalMxn() +
            ", category='" + getCategory() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
