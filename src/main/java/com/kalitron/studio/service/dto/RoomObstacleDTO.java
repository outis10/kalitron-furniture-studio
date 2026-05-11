package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kalitron.studio.domain.RoomObstacle} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomObstacleDTO implements Serializable {

    private Long id;

    @NotNull
    private RoomObstacleType obstacleType;

    @Size(max = 100)
    private String label;

    @NotNull
    private Integer xMm;

    private Integer yMm;

    private Integer zMm;

    private Integer widthMm;

    private Integer heightMm;

    private Integer depthMm;

    @Size(max = 300)
    private String notes;

    @NotNull
    private DesignSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomObstacleType getObstacleType() {
        return obstacleType;
    }

    public void setObstacleType(RoomObstacleType obstacleType) {
        this.obstacleType = obstacleType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getxMm() {
        return xMm;
    }

    public void setxMm(Integer xMm) {
        this.xMm = xMm;
    }

    public Integer getyMm() {
        return yMm;
    }

    public void setyMm(Integer yMm) {
        this.yMm = yMm;
    }

    public Integer getzMm() {
        return zMm;
    }

    public void setzMm(Integer zMm) {
        this.zMm = zMm;
    }

    public Integer getWidthMm() {
        return widthMm;
    }

    public void setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
    }

    public Integer getHeightMm() {
        return heightMm;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getDepthMm() {
        return depthMm;
    }

    public void setDepthMm(Integer depthMm) {
        this.depthMm = depthMm;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        if (!(o instanceof RoomObstacleDTO)) {
            return false;
        }

        RoomObstacleDTO roomObstacleDTO = (RoomObstacleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, roomObstacleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomObstacleDTO{" +
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
            ", session=" + getSession() +
            "}";
    }
}
