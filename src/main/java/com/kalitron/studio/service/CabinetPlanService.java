package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.CabinetPlanResponseDTO;
import java.util.Optional;

public interface CabinetPlanService {
    CabinetPlanResponseDTO generateCabinetPlan(Long sessionId);

    Optional<CabinetPlanResponseDTO> findCabinetPlan(Long sessionId);
}
