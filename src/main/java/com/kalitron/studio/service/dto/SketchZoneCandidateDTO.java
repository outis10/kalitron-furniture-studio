package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class SketchZoneCandidateDTO implements Serializable {

    private SketchFieldDTO<String> zoneCode;

    private SketchFieldDTO<String> zoneType;

    private SketchFieldDTO<String> wallCode;

    private SketchMeasurementDTO x;

    private SketchMeasurementDTO width;

    public SketchFieldDTO<String> getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(SketchFieldDTO<String> zoneCode) {
        this.zoneCode = zoneCode;
    }

    public SketchFieldDTO<String> getZoneType() {
        return zoneType;
    }

    public void setZoneType(SketchFieldDTO<String> zoneType) {
        this.zoneType = zoneType;
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
