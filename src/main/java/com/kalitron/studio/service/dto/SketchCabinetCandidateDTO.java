package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class SketchCabinetCandidateDTO implements Serializable {

    private String candidateCode;

    private SketchFieldDTO<String> category;

    private SketchFieldDTO<String> label;

    private SketchFieldDTO<String> wallCode;

    private SketchMeasurementDTO x;

    private SketchMeasurementDTO width;

    private SketchMeasurementDTO height;

    private SketchMeasurementDTO depth;

    private SketchFieldDTO<Integer> doors;

    private SketchFieldDTO<Integer> drawers;

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public SketchFieldDTO<String> getCategory() {
        return category;
    }

    public void setCategory(SketchFieldDTO<String> category) {
        this.category = category;
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

    public SketchMeasurementDTO getHeight() {
        return height;
    }

    public void setHeight(SketchMeasurementDTO height) {
        this.height = height;
    }

    public SketchMeasurementDTO getDepth() {
        return depth;
    }

    public void setDepth(SketchMeasurementDTO depth) {
        this.depth = depth;
    }

    public SketchFieldDTO<Integer> getDoors() {
        return doors;
    }

    public void setDoors(SketchFieldDTO<Integer> doors) {
        this.doors = doors;
    }

    public SketchFieldDTO<Integer> getDrawers() {
        return drawers;
    }

    public void setDrawers(SketchFieldDTO<Integer> drawers) {
        this.drawers = drawers;
    }
}
