package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.Material;
import com.kalitron.studio.repository.MaterialRepository;
import com.kalitron.studio.service.MaterialService;
import com.kalitron.studio.service.dto.MaterialDTO;
import com.kalitron.studio.service.mapper.MaterialMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.Material}.
 */
@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private static final Logger LOG = LoggerFactory.getLogger(MaterialServiceImpl.class);

    private final MaterialRepository materialRepository;

    private final MaterialMapper materialMapper;

    public MaterialServiceImpl(MaterialRepository materialRepository, MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.materialMapper = materialMapper;
    }

    @Override
    public MaterialDTO save(MaterialDTO materialDTO) {
        LOG.debug("Request to save Material : {}", materialDTO);
        Material material = materialMapper.toEntity(materialDTO);
        material = materialRepository.save(material);
        return materialMapper.toDto(material);
    }

    @Override
    public MaterialDTO update(MaterialDTO materialDTO) {
        LOG.debug("Request to update Material : {}", materialDTO);
        Material material = materialMapper.toEntity(materialDTO);
        material = materialRepository.save(material);
        return materialMapper.toDto(material);
    }

    @Override
    public Optional<MaterialDTO> partialUpdate(MaterialDTO materialDTO) {
        LOG.debug("Request to partially update Material : {}", materialDTO);

        return materialRepository
            .findById(materialDTO.getId())
            .map(existingMaterial -> {
                materialMapper.partialUpdate(existingMaterial, materialDTO);

                return existingMaterial;
            })
            .map(materialRepository::save)
            .map(materialMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MaterialDTO> findOne(Long id) {
        LOG.debug("Request to get Material : {}", id);
        return materialRepository.findById(id).map(materialMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Material : {}", id);
        materialRepository.deleteById(id);
    }
}
