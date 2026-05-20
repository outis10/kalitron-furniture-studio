package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.KitchenLayout;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CabinetPlanResponseDTO implements Serializable {

    private Long sessionId;

    private String sessionCode;

    private KitchenLayout layout;

    private boolean valid;

    private int cabinetCount;

    private Integer totalOccupiedLengthMm;

    @Valid
    private List<CabinetPlanItemDTO> cabinets = new ArrayList<>();

    @Valid
    private List<CabinetPlanValidationMessageDTO> validationMessages = new ArrayList<>();

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

    public KitchenLayout getLayout() {
        return layout;
    }

    public void setLayout(KitchenLayout layout) {
        this.layout = layout;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getCabinetCount() {
        return cabinetCount;
    }

    public void setCabinetCount(int cabinetCount) {
        this.cabinetCount = cabinetCount;
    }

    public Integer getTotalOccupiedLengthMm() {
        return totalOccupiedLengthMm;
    }

    public void setTotalOccupiedLengthMm(Integer totalOccupiedLengthMm) {
        this.totalOccupiedLengthMm = totalOccupiedLengthMm;
    }

    public List<CabinetPlanItemDTO> getCabinets() {
        return cabinets;
    }

    public void setCabinets(List<CabinetPlanItemDTO> cabinets) {
        this.cabinets = cabinets;
    }

    public List<CabinetPlanValidationMessageDTO> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(List<CabinetPlanValidationMessageDTO> validationMessages) {
        this.validationMessages = validationMessages;
    }
}
