package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.service.DesignArtifactService;
import com.kalitron.studio.service.dto.DesignArtifactDTO;
import com.kalitron.studio.service.mapper.DesignArtifactMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.DesignArtifact}.
 */
@Service
@Transactional
public class DesignArtifactServiceImpl implements DesignArtifactService {

    private static final Logger LOG = LoggerFactory.getLogger(DesignArtifactServiceImpl.class);

    private final DesignArtifactRepository designArtifactRepository;

    private final DesignArtifactMapper designArtifactMapper;

    public DesignArtifactServiceImpl(DesignArtifactRepository designArtifactRepository, DesignArtifactMapper designArtifactMapper) {
        this.designArtifactRepository = designArtifactRepository;
        this.designArtifactMapper = designArtifactMapper;
    }

    @Override
    public DesignArtifactDTO save(DesignArtifactDTO designArtifactDTO) {
        LOG.debug("Request to save DesignArtifact : {}", designArtifactDTO);
        DesignArtifact designArtifact = designArtifactMapper.toEntity(designArtifactDTO);
        designArtifact = designArtifactRepository.save(designArtifact);
        return designArtifactMapper.toDto(designArtifact);
    }

    @Override
    public DesignArtifactDTO update(DesignArtifactDTO designArtifactDTO) {
        LOG.debug("Request to update DesignArtifact : {}", designArtifactDTO);
        DesignArtifact designArtifact = designArtifactMapper.toEntity(designArtifactDTO);
        designArtifact = designArtifactRepository.save(designArtifact);
        return designArtifactMapper.toDto(designArtifact);
    }

    @Override
    public Optional<DesignArtifactDTO> partialUpdate(DesignArtifactDTO designArtifactDTO) {
        LOG.debug("Request to partially update DesignArtifact : {}", designArtifactDTO);

        return designArtifactRepository
            .findById(designArtifactDTO.getId())
            .map(existingDesignArtifact -> {
                designArtifactMapper.partialUpdate(existingDesignArtifact, designArtifactDTO);

                return existingDesignArtifact;
            })
            .map(designArtifactRepository::save)
            .map(designArtifactMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DesignArtifactDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DesignArtifacts");
        return designArtifactRepository.findAll(pageable).map(designArtifactMapper::toDto);
    }

    public Page<DesignArtifactDTO> findAllWithEagerRelationships(Pageable pageable) {
        return designArtifactRepository.findAllWithEagerRelationships(pageable).map(designArtifactMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DesignArtifactDTO> findOne(Long id) {
        LOG.debug("Request to get DesignArtifact : {}", id);
        return designArtifactRepository.findOneWithEagerRelationships(id).map(designArtifactMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DesignArtifact : {}", id);
        designArtifactRepository.deleteById(id);
    }
}
