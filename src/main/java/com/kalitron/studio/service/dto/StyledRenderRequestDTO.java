package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.NotBlank;

public class StyledRenderRequestDTO {

    @NotBlank
    private String style;

    private String finish;
    private String countertopMaterial;
    private String backsplashNotes;
    private String handleStyle;
    private String wallColor;
    private String notes;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getCountertopMaterial() {
        return countertopMaterial;
    }

    public void setCountertopMaterial(String countertopMaterial) {
        this.countertopMaterial = countertopMaterial;
    }

    public String getBacksplashNotes() {
        return backsplashNotes;
    }

    public void setBacksplashNotes(String backsplashNotes) {
        this.backsplashNotes = backsplashNotes;
    }

    public String getHandleStyle() {
        return handleStyle;
    }

    public void setHandleStyle(String handleStyle) {
        this.handleStyle = handleStyle;
    }

    public String getWallColor() {
        return wallColor;
    }

    public void setWallColor(String wallColor) {
        this.wallColor = wallColor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
