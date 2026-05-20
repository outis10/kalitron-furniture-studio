package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.FinishType;
import jakarta.validation.constraints.*;
import java.io.Serializable;

public class CabinetPlanItemDTO implements Serializable {

    @Size(max = 20)
    private String cabinetCode;

    @Size(max = 40)
    private String templateCode;

    @NotNull
    private CabinetCategory category;

    @NotBlank
    @Size(max = 120)
    private String label;

    @NotNull
    @Min(1)
    private Integer widthMm;

    @NotNull
    @Min(1)
    private Integer heightMm;

    @NotNull
    @Min(1)
    private Integer depthMm;

    @Min(0)
    @Max(6)
    private Integer doors;

    @Min(0)
    @Max(8)
    private Integer drawers;

    @Min(0)
    @Max(12)
    private Integer shelves;

    private FinishType finish;

    @NotBlank
    @Size(max = 20)
    private String wallCode;

    @NotNull
    private Integer xMm;

    @NotNull
    private Integer yMm;

    @NotNull
    private Integer zMm;

    private Integer rotationDeg;

    @Min(0)
    private Integer positionSeq;

    @Size(max = 40)
    private String materialCode;

    @Size(max = 300)
    private String notes;

    public String getCabinetCode() {
        return cabinetCode;
    }

    public void setCabinetCode(String cabinetCode) {
        this.cabinetCode = cabinetCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public CabinetCategory getCategory() {
        return category;
    }

    public void setCategory(CabinetCategory category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public Integer getDrawers() {
        return drawers;
    }

    public void setDrawers(Integer drawers) {
        this.drawers = drawers;
    }

    public Integer getShelves() {
        return shelves;
    }

    public void setShelves(Integer shelves) {
        this.shelves = shelves;
    }

    public FinishType getFinish() {
        return finish;
    }

    public void setFinish(FinishType finish) {
        this.finish = finish;
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

    public Integer getRotationDeg() {
        return rotationDeg;
    }

    public void setRotationDeg(Integer rotationDeg) {
        this.rotationDeg = rotationDeg;
    }

    public Integer getPositionSeq() {
        return positionSeq;
    }

    public void setPositionSeq(Integer positionSeq) {
        this.positionSeq = positionSeq;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
