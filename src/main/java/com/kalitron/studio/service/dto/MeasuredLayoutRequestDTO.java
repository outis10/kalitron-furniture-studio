package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.KitchenLayout;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MeasuredLayoutRequestDTO implements Serializable {

    @NotNull
    private Long sessionId;

    @NotNull
    private KitchenLayout layout;

    @NotNull
    @Min(1)
    private Integer roomHeightMm;

    @Min(1)
    private Integer defaultBaseDepthMm;

    @Min(1)
    private Integer defaultUpperDepthMm;

    @Valid
    @NotEmpty
    private List<MeasuredWallSegmentDTO> walls = new ArrayList<>();

    @Valid
    private List<LayoutZoneDTO> zones = new ArrayList<>();

    @Valid
    private List<LayoutObstacleDTO> obstacles = new ArrayList<>();

    @Size(max = 1000)
    private String notes;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public KitchenLayout getLayout() {
        return layout;
    }

    public void setLayout(KitchenLayout layout) {
        this.layout = layout;
    }

    public Integer getRoomHeightMm() {
        return roomHeightMm;
    }

    public void setRoomHeightMm(Integer roomHeightMm) {
        this.roomHeightMm = roomHeightMm;
    }

    public Integer getDefaultBaseDepthMm() {
        return defaultBaseDepthMm;
    }

    public void setDefaultBaseDepthMm(Integer defaultBaseDepthMm) {
        this.defaultBaseDepthMm = defaultBaseDepthMm;
    }

    public Integer getDefaultUpperDepthMm() {
        return defaultUpperDepthMm;
    }

    public void setDefaultUpperDepthMm(Integer defaultUpperDepthMm) {
        this.defaultUpperDepthMm = defaultUpperDepthMm;
    }

    public List<MeasuredWallSegmentDTO> getWalls() {
        return walls;
    }

    public void setWalls(List<MeasuredWallSegmentDTO> walls) {
        this.walls = walls;
    }

    public List<LayoutZoneDTO> getZones() {
        return zones;
    }

    public void setZones(List<LayoutZoneDTO> zones) {
        this.zones = zones;
    }

    public List<LayoutObstacleDTO> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<LayoutObstacleDTO> obstacles) {
        this.obstacles = obstacles;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
