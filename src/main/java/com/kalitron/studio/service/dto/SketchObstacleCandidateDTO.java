package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class SketchObstacleCandidateDTO implements Serializable {

    private SketchFieldDTO<String> obstacleType;

    private SketchFieldDTO<String> label;

    private SketchFieldDTO<String> wallCode;

    private SketchMeasurementDTO x;

    private SketchMeasurementDTO width;

    public SketchFieldDTO<String> getObstacleType() {
        return obstacleType;
    }

    public void setObstacleType(SketchFieldDTO<String> obstacleType) {
        this.obstacleType = obstacleType;
    }

    public SketchFieldDTO<String> getLabel() {
        return label;
    }

    public void setLabel(SketchFieldDTO<String> label) {
        this.label = label;
    }

    public SketchFieldDTO<String> getWallCode() {
        return wallCode;
    }

    public void setWallCode(SketchFieldDTO<String> wallCode) {
        this.wallCode = wallCode;
    }

    public SketchMeasurementDTO getX() {
        return x;
    }

    public void setX(SketchMeasurementDTO x) {
        this.x = x;
    }

    public SketchMeasurementDTO getWidth() {
        return width;
    }

    public void setWidth(SketchMeasurementDTO width) {
        this.width = width;
    }
}
