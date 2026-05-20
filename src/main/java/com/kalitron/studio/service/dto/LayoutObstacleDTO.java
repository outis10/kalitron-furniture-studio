package com.kalitron.studio.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import jakarta.validation.constraints.*;
import java.io.Serializable;

public class LayoutObstacleDTO implements Serializable {

    @NotNull
    private RoomObstacleType obstacleType;

    @Size(max = 100)
    private String label;

    @NotBlank
    @Size(max = 20)
    private String wallCode;

    @NotNull
    @Min(0)
    @JsonProperty("xMm")
    private Integer xMm;

    @Min(0)
    @JsonProperty("yMm")
    private Integer yMm;

    @Min(0)
    @JsonProperty("zMm")
    private Integer zMm;

    @Min(1)
    private Integer widthMm;

    @Min(1)
    private Integer heightMm;

    @Min(1)
    private Integer depthMm;

    @Size(max = 300)
    private String notes;

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

    public String getWallCode() {
        return wallCode;
    }

    public void setWallCode(String wallCode) {
        this.wallCode = wallCode;
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
}
