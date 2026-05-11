package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.GenerationJob;
import com.kalitron.studio.repository.GenerationJobRepository;
import com.kalitron.studio.service.GenerationJobService;
import com.kalitron.studio.service.dto.GenerationJobDTO;
import com.kalitron.studio.service.mapper.GenerationJobMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.GenerationJob}.
 */
@Service
@Transactional
public class GenerationJobServiceImpl implements GenerationJobService {

    private static final Logger LOG = LoggerFactory.getLogger(GenerationJobServiceImpl.class);

    private final GenerationJobRepository generationJobRepository;

    private final GenerationJobMapper generationJobMapper;

    public GenerationJobServiceImpl(GenerationJobRepository generationJobRepository, GenerationJobMapper generationJobMapper) {
        this.generationJobRepository = generationJobRepository;
        this.generationJobMapper = generationJobMapper;
    }

    @Override
    public GenerationJobDTO save(GenerationJobDTO generationJobDTO) {
        LOG.debug("Request to save GenerationJob : {}", generationJobDTO);
        GenerationJob generationJob = generationJobMapper.toEntity(generationJobDTO);
        generationJob = generationJobRepository.save(generationJob);
        return generationJobMapper.toDto(generationJob);
    }

    @Override
    public GenerationJobDTO update(GenerationJobDTO generationJobDTO) {
        LOG.debug("Request to update GenerationJob : {}", generationJobDTO);
        GenerationJob generationJob = generationJobMapper.toEntity(generationJobDTO);
        generationJob = generationJobRepository.save(generationJob);
        return generationJobMapper.toDto(generationJob);
    }

    @Override
    public Optional<GenerationJobDTO> partialUpdate(GenerationJobDTO generationJobDTO) {
        LOG.debug("Request to partially update GenerationJob : {}", generationJobDTO);

        return generationJobRepository
            .findById(generationJobDTO.getId())
            .map(existingGenerationJob -> {
                generationJobMapper.partialUpdate(existingGenerationJob, generationJobDTO);

                return existingGenerationJob;
            })
            .map(generationJobRepository::save)
            .map(generationJobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenerationJobDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all GenerationJobs");
        return generationJobRepository.findAll(pageable).map(generationJobMapper::toDto);
    }

    public Page<GenerationJobDTO> findAllWithEagerRelationships(Pageable pageable) {
        return generationJobRepository.findAllWithEagerRelationships(pageable).map(generationJobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenerationJobDTO> findOne(Long id) {
        LOG.debug("Request to get GenerationJob : {}", id);
        return generationJobRepository.findOneWithEagerRelationships(id).map(generationJobMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete GenerationJob : {}", id);
        generationJobRepository.deleteById(id);
    }
}
