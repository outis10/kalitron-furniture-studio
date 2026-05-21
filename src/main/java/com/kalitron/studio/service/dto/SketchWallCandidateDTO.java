package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class SketchWallCandidateDTO implements Serializable {

    private SketchFieldDTO<String> wallCode;

    private SketchMeasurementDTO length;

    private SketchMeasurementDTO height;

    private SketchFieldDTO<Integer> angleDeg;

    public SketchFieldDTO<String> getWallCode() {
        return wallCode;
    }

    public void setWallCode(SketchFieldDTO<String> wallCode) {
        this.wallCode = wallCode;
    }

    public SketchMeasurementDTO getLength() {
        return length;
    }

    public void setLength(SketchMeasurementDTO length) {
        this.length = length;
    }

    public SketchMeasurementDTO getHeight() {
        return height;
    }

    public void setHeight(SketchMeasurementDTO height) {
        this.height = height;
    }

    public SketchFieldDTO<Integer> getAngleDeg() {
        return angleDeg;
    }

    public void setAngleDeg(SketchFieldDTO<Integer> angleDeg) {
        this.angleDeg = angleDeg;
    }
}
