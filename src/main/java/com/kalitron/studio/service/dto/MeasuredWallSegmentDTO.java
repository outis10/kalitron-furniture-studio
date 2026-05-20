package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

public class MeasuredWallSegmentDTO implements Serializable {

    @NotBlank
    @Size(max = 20)
    private String wallCode;

    @NotNull
    @Min(1)
    private Integer lengthMm;

    @Min(1)
    private Integer heightMm;

    @Min(-360)
    @Max(360)
    private Integer angleDeg;

    private Integer startXMm;

    private Integer startYMm;

    @Min(0)
    private Integer sortOrder;

    public String getWallCode() {
        return wallCode;
    }

    public void setWallCode(String wallCode) {
        this.wallCode = wallCode;
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

    public Integer getStartXMm() {
        return startXMm;
    }

    public void setStartXMm(Integer startXMm) {
        this.startXMm = startXMm;
    }

    public Integer getStartYMm() {
        return startYMm;
    }

    public void setStartYMm(Integer startYMm) {
        this.startYMm = startYMm;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
