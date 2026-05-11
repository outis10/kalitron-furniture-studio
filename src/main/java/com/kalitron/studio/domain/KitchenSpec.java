package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A KitchenSpec.
 */
@Entity
@Table(name = "kitchen_spec")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KitchenSpec implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "layout", nullable = false)
    private KitchenLayout layout;

    @Column(name = "total_width_mm")
    private Integer totalWidthMm;

    @Column(name = "total_height_mm")
    private Integer totalHeightMm;

    @Column(name = "total_depth_mm")
    private Integer totalDepthMm;

    @NotNull
    @Size(max = 60)
    @Column(name = "style", length = 60, nullable = false)
    private String style;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_finish", nullable = false)
    private FinishType primaryFinish;

    @Size(max = 80)
    @Column(name = "handle_type", length = 80)
    private String handleType;

    @Size(max = 80)
    @Column(name = "countertop_material", length = 80)
    private String countertopMaterial;

    @Size(max = 80)
    @Column(name = "sink_position", length = 80)
    private String sinkPosition;

    @NotNull
    @Column(name = "confirmed_by_client", nullable = false)
    private Boolean confirmedByClient;

    @Lob
    @Column(name = "extracted_json")
    private String extractedJson;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "spec")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "partses", "template", "material", "spec" }, allowSetters = true)
    private Set<Cabinet> cabinetses = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Material primaryMaterial;

    @JsonIgnoreProperties(
        value = { "spec", "messageses", "imageses", "artifactses", "jobses", "quoteses", "wallses", "obstacleses", "catalogStyle" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "spec")
    private DesignSession session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public KitchenSpec id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KitchenLayout getLayout() {
        return this.layout;
    }

    public KitchenSpec layout(KitchenLayout layout) {
        this.setLayout(layout);
        return this;
    }

    public void setLayout(KitchenLayout layout) {
        this.layout = layout;
    }

    public Integer getTotalWidthMm() {
        return this.totalWidthMm;
    }

    public KitchenSpec totalWidthMm(Integer totalWidthMm) {
        this.setTotalWidthMm(totalWidthMm);
        return this;
    }

    public void setTotalWidthMm(Integer totalWidthMm) {
        this.totalWidthMm = totalWidthMm;
    }

    public Integer getTotalHeightMm() {
        return this.totalHeightMm;
    }

    public KitchenSpec totalHeightMm(Integer totalHeightMm) {
        this.setTotalHeightMm(totalHeightMm);
        return this;
    }

    public void setTotalHeightMm(Integer totalHeightMm) {
        this.totalHeightMm = totalHeightMm;
    }

    public Integer getTotalDepthMm() {
        return this.totalDepthMm;
    }

    public KitchenSpec totalDepthMm(Integer totalDepthMm) {
        this.setTotalDepthMm(totalDepthMm);
        return this;
    }

    public void setTotalDepthMm(Integer totalDepthMm) {
        this.totalDepthMm = totalDepthMm;
    }

    public String getStyle() {
        return this.style;
    }

    public KitchenSpec style(String style) {
        this.setStyle(style);
        return this;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public FinishType getPrimaryFinish() {
        return this.primaryFinish;
    }

    public KitchenSpec primaryFinish(FinishType primaryFinish) {
        this.setPrimaryFinish(primaryFinish);
        return this;
    }

    public void setPrimaryFinish(FinishType primaryFinish) {
        this.primaryFinish = primaryFinish;
    }

    public String getHandleType() {
        return this.handleType;
    }

    public KitchenSpec handleType(String handleType) {
        this.setHandleType(handleType);
        return this;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public String getCountertopMaterial() {
        return this.countertopMaterial;
    }

    public KitchenSpec countertopMaterial(String countertopMaterial) {
        this.setCountertopMaterial(countertopMaterial);
        return this;
    }

    public void setCountertopMaterial(String countertopMaterial) {
        this.countertopMaterial = countertopMaterial;
    }

    public String getSinkPosition() {
        return this.sinkPosition;
    }

    public KitchenSpec sinkPosition(String sinkPosition) {
        this.setSinkPosition(sinkPosition);
        return this;
    }

    public void setSinkPosition(String sinkPosition) {
        this.sinkPosition = sinkPosition;
    }

    public Boolean getConfirmedByClient() {
        return this.confirmedByClient;
    }

    public KitchenSpec confirmedByClient(Boolean confirmedByClient) {
        this.setConfirmedByClient(confirmedByClient);
        return this;
    }

    public void setConfirmedByClient(Boolean confirmedByClient) {
        this.confirmedByClient = confirmedByClient;
    }

    public String getExtractedJson() {
        return this.extractedJson;
    }

    public KitchenSpec extractedJson(String extractedJson) {
        this.setExtractedJson(extractedJson);
        return this;
    }

    public void setExtractedJson(String extractedJson) {
        this.extractedJson = extractedJson;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public KitchenSpec confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Set<Cabinet> getCabinetses() {
        return this.cabinetses;
    }

    public void setCabinetses(Set<Cabinet> cabinets) {
        if (this.cabinetses != null) {
            this.cabinetses.forEach(i -> i.setSpec(null));
        }
        if (cabinets != null) {
            cabinets.forEach(i -> i.setSpec(this));
        }
        this.cabinetses = cabinets;
    }

    public KitchenSpec cabinetses(Set<Cabinet> cabinets) {
        this.setCabinetses(cabinets);
        return this;
    }

    public KitchenSpec addCabinets(Cabinet cabinet) {
        this.cabinetses.add(cabinet);
        cabinet.setSpec(this);
        return this;
    }

    public KitchenSpec removeCabinets(Cabinet cabinet) {
        this.cabinetses.remove(cabinet);
        cabinet.setSpec(null);
        return this;
    }

    public Material getPrimaryMaterial() {
        return this.primaryMaterial;
    }

    public void setPrimaryMaterial(Material material) {
        this.primaryMaterial = material;
    }

    public KitchenSpec primaryMaterial(Material material) {
        this.setPrimaryMaterial(material);
        return this;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        if (this.session != null) {
            this.session.setSpec(null);
        }
        if (designSession != null) {
            designSession.setSpec(this);
        }
        this.session = designSession;
    }

    public KitchenSpec session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KitchenSpec)) {
            return false;
        }
        return getId() != null && getId().equals(((KitchenSpec) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KitchenSpec{" +
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
            "}";
    }
}
