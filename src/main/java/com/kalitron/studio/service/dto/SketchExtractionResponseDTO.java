package com.kalitron.studio.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SketchExtractionResponseDTO implements Serializable {

    private String schemaVersion;

    private String requestId;

    private Long sessionId;

    private String sessionCode;

    private SketchFieldDTO<String> projectType;

    private SketchFieldDTO<String> layout;

    private SketchFieldDTO<String> unit;

    private List<SketchWallCandidateDTO> walls = new ArrayList<>();

    private List<SketchZoneCandidateDTO> zones = new ArrayList<>();

    private List<SketchObstacleCandidateDTO> obstacles = new ArrayList<>();

    private List<SketchCabinetCandidateDTO> cabinetCandidates = new ArrayList<>();

    private List<SketchMissingInfoDTO> missingInfo = new ArrayList<>();

    private List<String> questions = new ArrayList<>();

    private List<String> warnings = new ArrayList<>();

    private Map<String, Object> rawExtraction;

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public SketchFieldDTO<String> getProjectType() {
        return projectType;
    }

    public void setProjectType(SketchFieldDTO<String> projectType) {
        this.projectType = projectType;
    }

    public SketchFieldDTO<String> getLayout() {
        return layout;
    }

    public void setLayout(SketchFieldDTO<String> layout) {
        this.layout = layout;
    }

    public SketchFieldDTO<String> getUnit() {
        return unit;
    }

    public void setUnit(SketchFieldDTO<String> unit) {
        this.unit = unit;
    }

    public List<SketchWallCandidateDTO> getWalls() {
        return walls;
    }

    public void setWalls(List<SketchWallCandidateDTO> walls) {
        this.walls = walls;
    }

    public List<SketchZoneCandidateDTO> getZones() {
        return zones;
    }

    public void setZones(List<SketchZoneCandidateDTO> zones) {
        this.zones = zones;
    }

    public List<SketchObstacleCandidateDTO> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<SketchObstacleCandidateDTO> obstacles) {
        this.obstacles = obstacles;
    }

    public List<SketchCabinetCandidateDTO> getCabinetCandidates() {
        return cabinetCandidates;
    }

    public void setCabinetCandidates(List<SketchCabinetCandidateDTO> cabinetCandidates) {
        this.cabinetCandidates = cabinetCandidates;
    }

    public List<SketchMissingInfoDTO> getMissingInfo() {
        return missingInfo;
    }

    public void setMissingInfo(List<SketchMissingInfoDTO> missingInfo) {
        this.missingInfo = missingInfo;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public Map<String, Object> getRawExtraction() {
        return rawExtraction;
    }

    public void setRawExtraction(Map<String, Object> rawExtraction) {
        this.rawExtraction = rawExtraction;
    }
}
