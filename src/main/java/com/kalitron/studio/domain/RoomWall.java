package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RoomWall.
 */
@Entity
@Table(name = "room_wall")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomWall implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @NotNull
    @Column(name = "length_mm", nullable = false)
    private Integer lengthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "angle_deg")
    private Integer angleDeg;

    @Column(name = "position_x")
    private Integer positionX;

    @Column(name = "position_y")
    private Integer positionY;

    @Column(name = "sort_order")
    private Integer sortOrder;

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

    public RoomWall id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public RoomWall name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLengthMm() {
        return this.lengthMm;
    }

    public RoomWall lengthMm(Integer lengthMm) {
        this.setLengthMm(lengthMm);
        return this;
    }

    public void setLengthMm(Integer lengthMm) {
        this.lengthMm = lengthMm;
    }

    public Integer getHeightMm() {
        return this.heightMm;
    }

    public RoomWall heightMm(Integer heightMm) {
        this.setHeightMm(heightMm);
        return this;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getAngleDeg() {
        return this.angleDeg;
    }

    public RoomWall angleDeg(Integer angleDeg) {
        this.setAngleDeg(angleDeg);
        return this;
    }

    public void setAngleDeg(Integer angleDeg) {
        this.angleDeg = angleDeg;
    }

    public Integer getPositionX() {
        return this.positionX;
    }

    public RoomWall positionX(Integer positionX) {
        this.setPositionX(positionX);
        return this;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return this.positionY;
    }

    public RoomWall positionY(Integer positionY) {
        this.setPositionY(positionY);
        return this;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public RoomWall sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public RoomWall session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoomWall)) {
            return false;
        }
        return getId() != null && getId().equals(((RoomWall) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomWall{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", lengthMm=" + getLengthMm() +
            ", heightMm=" + getHeightMm() +
            ", angleDeg=" + getAngleDeg() +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}
