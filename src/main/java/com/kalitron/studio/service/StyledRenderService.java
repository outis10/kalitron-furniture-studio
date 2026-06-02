package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.StyledRenderJobDTO;
import com.kalitron.studio.service.dto.StyledRenderRequestDTO;
import java.util.Optional;

public interface StyledRenderService {
    StyledRenderJobDTO generateStyledRender(Long sessionId, StyledRenderRequestDTO request);

    Optional<StyledRenderJobDTO> getLatestJob(Long sessionId);
}
