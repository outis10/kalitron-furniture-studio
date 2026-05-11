package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.repository.KitchenSpecRepository;
import com.kalitron.studio.service.KitchenSpecService;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
import com.kalitron.studio.service.mapper.KitchenSpecMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.KitchenSpec}.
 */
@Service
@Transactional
public class KitchenSpecServiceImpl implements KitchenSpecService {

    private static final Logger LOG = LoggerFactory.getLogger(KitchenSpecServiceImpl.class);

    private final KitchenSpecRepository kitchenSpecRepository;

    private final KitchenSpecMapper kitchenSpecMapper;

    public KitchenSpecServiceImpl(KitchenSpecRepository kitchenSpecRepository, KitchenSpecMapper kitchenSpecMapper) {
        this.kitchenSpecRepository = kitchenSpecRepository;
        this.kitchenSpecMapper = kitchenSpecMapper;
    }

    @Override
    public KitchenSpecDTO save(KitchenSpecDTO kitchenSpecDTO) {
        LOG.debug("Request to save KitchenSpec : {}", kitchenSpecDTO);
        KitchenSpec kitchenSpec = kitchenSpecMapper.toEntity(kitchenSpecDTO);
        kitchenSpec = kitchenSpecRepository.save(kitchenSpec);
        return kitchenSpecMapper.toDto(kitchenSpec);
    }

    @Override
    public KitchenSpecDTO update(KitchenSpecDTO kitchenSpecDTO) {
        LOG.debug("Request to update KitchenSpec : {}", kitchenSpecDTO);
        KitchenSpec kitchenSpec = kitchenSpecMapper.toEntity(kitchenSpecDTO);
        kitchenSpec = kitchenSpecRepository.save(kitchenSpec);
        return kitchenSpecMapper.toDto(kitchenSpec);
    }

    @Override
    public Optional<KitchenSpecDTO> partialUpdate(KitchenSpecDTO kitchenSpecDTO) {
        LOG.debug("Request to partially update KitchenSpec : {}", kitchenSpecDTO);

        return kitchenSpecRepository
            .findById(kitchenSpecDTO.getId())
            .map(existingKitchenSpec -> {
                kitchenSpecMapper.partialUpdate(existingKitchenSpec, kitchenSpecDTO);

                return existingKitchenSpec;
            })
            .map(kitchenSpecRepository::save)
            .map(kitchenSpecMapper::toDto);
    }

    public Page<KitchenSpecDTO> findAllWithEagerRelationships(Pageable pageable) {
        return kitchenSpecRepository.findAllWithEagerRelationships(pageable).map(kitchenSpecMapper::toDto);
    }

    /**
     *  Get all the kitchenSpecs where Session is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<KitchenSpecDTO> findAllWhereSessionIsNull() {
        LOG.debug("Request to get all kitchenSpecs where Session is null");
        return StreamSupport.stream(kitchenSpecRepository.findAll().spliterator(), false)
            .filter(kitchenSpec -> kitchenSpec.getSession() == null)
            .map(kitchenSpecMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KitchenSpecDTO> findOne(Long id) {
        LOG.debug("Request to get KitchenSpec : {}", id);
        return kitchenSpecRepository.findOneWithEagerRelationships(id).map(kitchenSpecMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete KitchenSpec : {}", id);
        kitchenSpecRepository.deleteById(id);
    }
}
