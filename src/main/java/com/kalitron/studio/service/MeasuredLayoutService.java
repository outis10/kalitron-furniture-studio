package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import java.util.Optional;

public interface MeasuredLayoutService {
    MeasuredLayoutRequestDTO saveMeasuredLayout(Long sessionId, MeasuredLayoutRequestDTO request);

    Optional<MeasuredLayoutRequestDTO> findMeasuredLayout(Long sessionId);
}
