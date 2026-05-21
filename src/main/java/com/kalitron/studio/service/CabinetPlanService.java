package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.CabinetPlanResponseDTO;
import java.util.Optional;

public interface CabinetPlanService {
    CabinetPlanResponseDTO generateCabinetPlan(Long sessionId);

    CabinetPlanResponseDTO saveCabinetPlan(Long sessionId, CabinetPlanResponseDTO cabinetPlan);

    Optional<CabinetPlanResponseDTO> findCabinetPlan(Long sessionId);
}
