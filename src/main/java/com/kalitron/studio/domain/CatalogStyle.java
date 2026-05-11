package com.kalitron.studio.domain;

import com.kalitron.studio.domain.enumeration.FinishType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CatalogStyle.
 */
@Entity
@Table(name = "catalog_style")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CatalogStyle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @Size(max = 500)
    @Column(name = "thumbnail_path", length = 500, nullable = false)
    private String thumbnailPath;

    @Size(max = 60)
    @Column(name = "style", length = 60)
    private String style;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_finish")
    private FinishType primaryFinish;

    @Size(max = 30)
    @Column(name = "price_range", length = 30)
    private String priceRange;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CatalogStyle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CatalogStyle name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public CatalogStyle description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public CatalogStyle thumbnailPath(String thumbnailPath) {
        this.setThumbnailPath(thumbnailPath);
        return this;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getStyle() {
        return this.style;
    }

    public CatalogStyle style(String style) {
        this.setStyle(style);
        return this;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public FinishType getPrimaryFinish() {
        return this.primaryFinish;
    }

    public CatalogStyle primaryFinish(FinishType primaryFinish) {
        this.setPrimaryFinish(primaryFinish);
        return this;
    }

    public void setPrimaryFinish(FinishType primaryFinish) {
        this.primaryFinish = primaryFinish;
    }

    public String getPriceRange() {
        return this.priceRange;
    }

    public CatalogStyle priceRange(String priceRange) {
        this.setPriceRange(priceRange);
        return this;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public CatalogStyle isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public CatalogStyle sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CatalogStyle)) {
            return false;
        }
        return getId() != null && getId().equals(((CatalogStyle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CatalogStyle{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbnailPath='" + getThumbnailPath() + "'" +
            ", style='" + getStyle() + "'" +
            ", primaryFinish='" + getPrimaryFinish() + "'" +
            ", priceRange='" + getPriceRange() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}
