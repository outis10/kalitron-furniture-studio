package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.FinishType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.CatalogStyle} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CatalogStyleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @Size(max = 500)
    private String thumbnailPath;

    @Size(max = 60)
    private String style;

    private FinishType primaryFinish;

    @Size(max = 30)
    private String priceRange;

    @NotNull
    private Boolean isActive;

    private Integer sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public FinishType getPrimaryFinish() {
        return primaryFinish;
    }

    public void setPrimaryFinish(FinishType primaryFinish) {
        this.primaryFinish = primaryFinish;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CatalogStyleDTO)) {
            return false;
        }

        CatalogStyleDTO catalogStyleDTO = (CatalogStyleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, catalogStyleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CatalogStyleDTO{" +
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
