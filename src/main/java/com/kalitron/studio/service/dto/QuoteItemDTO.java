package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.QuoteItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuoteItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String description;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private BigDecimal unitPriceMxn;

    @NotNull
    private BigDecimal totalMxn;

    @Size(max = 60)
    private String category;

    @Size(max = 200)
    private String notes;

    private CabinetDTO cabinet;

    private HardwareDTO hardware;

    @NotNull
    private QuoteDTO quote;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceMxn() {
        return unitPriceMxn;
    }

    public void setUnitPriceMxn(BigDecimal unitPriceMxn) {
        this.unitPriceMxn = unitPriceMxn;
    }

    public BigDecimal getTotalMxn() {
        return totalMxn;
    }

    public void setTotalMxn(BigDecimal totalMxn) {
        this.totalMxn = totalMxn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CabinetDTO getCabinet() {
        return cabinet;
    }

    public void setCabinet(CabinetDTO cabinet) {
        this.cabinet = cabinet;
    }

    public HardwareDTO getHardware() {
        return hardware;
    }

    public void setHardware(HardwareDTO hardware) {
        this.hardware = hardware;
    }

    public QuoteDTO getQuote() {
        return quote;
    }

    public void setQuote(QuoteDTO quote) {
        this.quote = quote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuoteItemDTO)) {
            return false;
        }

        QuoteItemDTO quoteItemDTO = (QuoteItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, quoteItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuoteItemDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPriceMxn=" + getUnitPriceMxn() +
            ", totalMxn=" + getTotalMxn() +
            ", category='" + getCategory() + "'" +
            ", notes='" + getNotes() + "'" +
            ", cabinet=" + getCabinet() +
            ", hardware=" + getHardware() +
            ", quote=" + getQuote() +
            "}";
    }
}
