package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.KitchenSpec} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KitchenSpecDTO implements Serializable {

    private Long id;

    @NotNull
    private KitchenLayout layout;

    private Integer totalWidthMm;

    private Integer totalHeightMm;

    private Integer totalDepthMm;

    @NotNull
    @Size(max = 60)
    private String style;

    @NotNull
    private FinishType primaryFinish;

    @Size(max = 80)
    private String handleType;

    @Size(max = 80)
    private String countertopMaterial;

    @Size(max = 80)
    private String sinkPosition;

    @NotNull
    private Boolean confirmedByClient;

    @Lob
    private String extractedJson;

    private Instant confirmedAt;

    @NotNull
    private MaterialDTO primaryMaterial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KitchenLayout getLayout() {
        return layout;
    }

    public void setLayout(KitchenLayout layout) {
        this.layout = layout;
    }

    public Integer getTotalWidthMm() {
        return totalWidthMm;
    }

    public void setTotalWidthMm(Integer totalWidthMm) {
        this.totalWidthMm = totalWidthMm;
    }

    public Integer getTotalHeightMm() {
        return totalHeightMm;
    }

    public void setTotalHeightMm(Integer totalHeightMm) {
        this.totalHeightMm = totalHeightMm;
    }

    public Integer getTotalDepthMm() {
        return totalDepthMm;
    }

    public void setTotalDepthMm(Integer totalDepthMm) {
        this.totalDepthMm = totalDepthMm;
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

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public String getCountertopMaterial() {
        return countertopMaterial;
    }

    public void setCountertopMaterial(String countertopMaterial) {
        this.countertopMaterial = countertopMaterial;
    }

    public String getSinkPosition() {
        return sinkPosition;
    }

    public void setSinkPosition(String sinkPosition) {
        this.sinkPosition = sinkPosition;
    }

    public Boolean getConfirmedByClient() {
        return confirmedByClient;
    }

    public void setConfirmedByClient(Boolean confirmedByClient) {
        this.confirmedByClient = confirmedByClient;
    }

    public String getExtractedJson() {
        return extractedJson;
    }

    public void setExtractedJson(String extractedJson) {
        this.extractedJson = extractedJson;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public MaterialDTO getPrimaryMaterial() {
        return primaryMaterial;
    }

    public void setPrimaryMaterial(MaterialDTO primaryMaterial) {
        this.primaryMaterial = primaryMaterial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KitchenSpecDTO)) {
            return false;
        }

        KitchenSpecDTO kitchenSpecDTO = (KitchenSpecDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, kitchenSpecDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KitchenSpecDTO{" +
            "id=" + getId() +
            ", layout='" + getLayout() + "'" +
            ", totalWidthMm=" + getTotalWidthMm() +
            ", totalHeightMm=" + getTotalHeightMm() +
            ", totalDepthMm=" + getTotalDepthMm() +
            ", style='" + getStyle() + "'" +
            ", primaryFinish='" + getPrimaryFinish() + "'" +
            ", handleType='" + getHandleType() + "'" +
            ", countertopMaterial='" + getCountertopMaterial() + "'" +
            ", sinkPosition='" + getSinkPosition() + "'" +
            ", confirmedByClient='" + getConfirmedByClient() + "'" +
            ", extractedJson='" + getExtractedJson() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", primaryMaterial=" + getPrimaryMaterial() +
            "}";
    }
}
