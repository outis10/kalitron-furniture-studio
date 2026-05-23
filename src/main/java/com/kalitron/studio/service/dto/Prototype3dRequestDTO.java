package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class Prototype3dRequestDTO implements Serializable {

    private String prototypeMode = "BLOCKOUT";
    private boolean includeZones = true;
    private boolean includeObstacles = true;
    private boolean includeLabels = true;
    private Long cabinetPlanArtifactId;

    public String getPrototypeMode() {
        return prototypeMode;
    }

    public void setPrototypeMode(String prototypeMode) {
        this.prototypeMode = prototypeMode;
    }

    public boolean isIncludeZones() {
        return includeZones;
    }

    public void setIncludeZones(boolean includeZones) {
        this.includeZones = includeZones;
    }

    public boolean isIncludeObstacles() {
        return includeObstacles;
    }

    public void setIncludeObstacles(boolean includeObstacles) {
        this.includeObstacles = includeObstacles;
    }

    public boolean isIncludeLabels() {
        return includeLabels;
    }

    public void setIncludeLabels(boolean includeLabels) {
        this.includeLabels = includeLabels;
    }

    public Long getCabinetPlanArtifactId() {
        return cabinetPlanArtifactId;
    }

    public void setCabinetPlanArtifactId(Long cabinetPlanArtifactId) {
        this.cabinetPlanArtifactId = cabinetPlanArtifactId;
    }
}
