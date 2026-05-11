package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.RoomWall} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomWallDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
    private Integer lengthMm;

    private Integer heightMm;

    private Integer angleDeg;

    private Integer positionX;

    private Integer positionY;

    private Integer sortOrder;

    @NotNull
    private DesignSessionDTO session;

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

    public Integer getLengthMm() {
        return lengthMm;
    }

    public void setLengthMm(Integer lengthMm) {
        this.lengthMm = lengthMm;
    }

    public Integer getHeightMm() {
        return heightMm;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getAngleDeg() {
        return angleDeg;
    }

    public void setAngleDeg(Integer angleDeg) {
        this.angleDeg = angleDeg;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
        if (!(o instanceof RoomWallDTO)) {
            return false;
        }

        RoomWallDTO roomWallDTO = (RoomWallDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, roomWallDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomWallDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", lengthMm=" + getLengthMm() +
            ", heightMm=" + getHeightMm() +
            ", angleDeg=" + getAngleDeg() +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            ", sortOrder=" + getSortOrder() +
            ", session=" + getSession() +
            "}";
    }
}
