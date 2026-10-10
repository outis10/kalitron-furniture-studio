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

    private Integer lengthFloorMm;

    private Integer length900Mm;

    private Integer lengthCeilingMm;

    private Integer outOfPlumbMm;

    private Integer closingMm;

    private Integer heightLeftMm;

    private Integer heightRightMm;

    private SiteMeasurementDTO siteMeasurement;

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

    public Integer getLengthFloorMm() {
        return lengthFloorMm;
    }

    public void setLengthFloorMm(Integer lengthFloorMm) {
        this.lengthFloorMm = lengthFloorMm;
    }

    public Integer getLength900Mm() {
        return length900Mm;
    }

    public void setLength900Mm(Integer length900Mm) {
        this.length900Mm = length900Mm;
    }

    public Integer getLengthCeilingMm() {
        return lengthCeilingMm;
    }

    public void setLengthCeilingMm(Integer lengthCeilingMm) {
        this.lengthCeilingMm = lengthCeilingMm;
    }

    public Integer getOutOfPlumbMm() {
        return outOfPlumbMm;
    }

    public void setOutOfPlumbMm(Integer outOfPlumbMm) {
        this.outOfPlumbMm = outOfPlumbMm;
    }

    public Integer getClosingMm() {
        return closingMm;
    }

    public void setClosingMm(Integer closingMm) {
        this.closingMm = closingMm;
    }

    public Integer getHeightLeftMm() {
        return heightLeftMm;
    }

    public void setHeightLeftMm(Integer heightLeftMm) {
        this.heightLeftMm = heightLeftMm;
    }

    public Integer getHeightRightMm() {
        return heightRightMm;
    }

    public void setHeightRightMm(Integer heightRightMm) {
        this.heightRightMm = heightRightMm;
    }

    public SiteMeasurementDTO getSiteMeasurement() {
        return siteMeasurement;
    }

    public void setSiteMeasurement(SiteMeasurementDTO siteMeasurement) {
        this.siteMeasurement = siteMeasurement;
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
            ", lengthFloorMm=" + getLengthFloorMm() +
            ", length900Mm=" + getLength900Mm() +
            ", lengthCeilingMm=" + getLengthCeilingMm() +
            ", outOfPlumbMm=" + getOutOfPlumbMm() +
            ", closingMm=" + getClosingMm() +
            ", heightLeftMm=" + getHeightLeftMm() +
            ", heightRightMm=" + getHeightRightMm() +
            ", siteMeasurement=" + getSiteMeasurement() +
            ", session=" + getSession() +
            "}";
    }
}
