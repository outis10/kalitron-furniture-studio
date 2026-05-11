package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RoomObstacle.
 */
@Entity
@Table(name = "room_obstacle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomObstacle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "obstacle_type", nullable = false)
    private RoomObstacleType obstacleType;

    @Size(max = 100)
    @Column(name = "label", length = 100)
    private String label;

    @NotNull
    @Column(name = "x_mm", nullable = false)
    private Integer xMm;

    @Column(name = "y_mm")
    private Integer yMm;

    @Column(name = "z_mm")
    private Integer zMm;

    @Column(name = "width_mm")
    private Integer widthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "depth_mm")
    private Integer depthMm;

    @Size(max = 300)
    @Column(name = "notes", length = 300)
    private String notes;

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

    public RoomObstacle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomObstacleType getObstacleType() {
        return this.obstacleType;
    }

    public RoomObstacle obstacleType(RoomObstacleType obstacleType) {
        this.setObstacleType(obstacleType);
        return this;
    }

    public void setObstacleType(RoomObstacleType obstacleType) {
        this.obstacleType = obstacleType;
    }

    public String getLabel() {
        return this.label;
    }

    public RoomObstacle label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getxMm() {
        return this.xMm;
    }

    public RoomObstacle xMm(Integer xMm) {
        this.setxMm(xMm);
        return this;
    }

    public void setxMm(Integer xMm) {
        this.xMm = xMm;
    }

    public Integer getyMm() {
        return this.yMm;
    }

    public RoomObstacle yMm(Integer yMm) {
        this.setyMm(yMm);
        return this;
    }

    public void setyMm(Integer yMm) {
        this.yMm = yMm;
    }

    public Integer getzMm() {
        return this.zMm;
    }

    public RoomObstacle zMm(Integer zMm) {
        this.setzMm(zMm);
        return this;
    }

    public void setzMm(Integer zMm) {
        this.zMm = zMm;
    }

    public Integer getWidthMm() {
        return this.widthMm;
    }

    public RoomObstacle widthMm(Integer widthMm) {
        this.setWidthMm(widthMm);
        return this;
    }

    public void setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
    }

    public Integer getHeightMm() {
        return this.heightMm;
    }

    public RoomObstacle heightMm(Integer heightMm) {
        this.setHeightMm(heightMm);
        return this;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getDepthMm() {
        return this.depthMm;
    }

    public RoomObstacle depthMm(Integer depthMm) {
        this.setDepthMm(depthMm);
        return this;
    }

    public void setDepthMm(Integer depthMm) {
        this.depthMm = depthMm;
    }

    public String getNotes() {
        return this.notes;
    }

    public RoomObstacle notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public RoomObstacle session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoomObstacle)) {
            return false;
        }
        return getId() != null && getId().equals(((RoomObstacle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomObstacle{" +
            "id=" + getId() +
            ", obstacleType='" + getObstacleType() + "'" +
            ", label='" + getLabel() + "'" +
            ", xMm=" + getxMm() +
            ", yMm=" + getyMm() +
            ", zMm=" + getzMm() +
            ", widthMm=" + getWidthMm() +
            ", heightMm=" + getHeightMm() +
            ", depthMm=" + getDepthMm() +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
