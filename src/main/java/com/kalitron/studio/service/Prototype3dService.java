package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.Prototype3dJobDTO;
import com.kalitron.studio.service.dto.Prototype3dRequestDTO;
import java.util.Optional;

public interface Prototype3dService {
    Prototype3dJobDTO generatePrototype(Long sessionId, Prototype3dRequestDTO request);

    Optional<Prototype3dJobDTO> getJobStatus(Long sessionId, Long jobId);

    Optional<Prototype3dJobDTO> getLatestJob(Long sessionId);
}
