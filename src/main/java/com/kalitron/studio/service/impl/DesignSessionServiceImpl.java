package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.service.DesignSessionService;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.mapper.DesignSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.DesignSession}.
 */
@Service
@Transactional
public class DesignSessionServiceImpl implements DesignSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSessionServiceImpl.class);

    private final DesignSessionRepository designSessionRepository;

    private final DesignSessionMapper designSessionMapper;

    public DesignSessionServiceImpl(DesignSessionRepository designSessionRepository, DesignSessionMapper designSessionMapper) {
        this.designSessionRepository = designSessionRepository;
        this.designSessionMapper = designSessionMapper;
    }

    @Override
    public DesignSessionDTO save(DesignSessionDTO designSessionDTO) {
        LOG.debug("Request to save DesignSession : {}", designSessionDTO);
        DesignSession designSession = designSessionMapper.toEntity(designSessionDTO);
        designSession = designSessionRepository.save(designSession);
        return designSessionMapper.toDto(designSession);
    }

    @Override
    public DesignSessionDTO update(DesignSessionDTO designSessionDTO) {
        LOG.debug("Request to update DesignSession : {}", designSessionDTO);
        DesignSession designSession = designSessionMapper.toEntity(designSessionDTO);
        designSession = designSessionRepository.save(designSession);
        return designSessionMapper.toDto(designSession);
    }

    @Override
    public Optional<DesignSessionDTO> partialUpdate(DesignSessionDTO designSessionDTO) {
        LOG.debug("Request to partially update DesignSession : {}", designSessionDTO);

        return designSessionRepository
            .findById(designSessionDTO.getId())
            .map(existingDesignSession -> {
                designSessionMapper.partialUpdate(existingDesignSession, designSessionDTO);

                return existingDesignSession;
            })
            .map(designSessionRepository::save)
            .map(designSessionMapper::toDto);
    }

    public Page<DesignSessionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return designSessionRepository.findAllWithEagerRelationships(pageable).map(designSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DesignSessionDTO> findOne(Long id) {
        LOG.debug("Request to get DesignSession : {}", id);
        return designSessionRepository.findOneWithEagerRelationships(id).map(designSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DesignSession : {}", id);
        designSessionRepository.deleteById(id);
    }
}
