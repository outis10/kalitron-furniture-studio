package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.CabinetPart;
import com.kalitron.studio.repository.CabinetPartRepository;
import com.kalitron.studio.service.CabinetPartService;
import com.kalitron.studio.service.dto.CabinetPartDTO;
import com.kalitron.studio.service.mapper.CabinetPartMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.CabinetPart}.
 */
@Service
@Transactional
public class CabinetPartServiceImpl implements CabinetPartService {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetPartServiceImpl.class);

    private final CabinetPartRepository cabinetPartRepository;

    private final CabinetPartMapper cabinetPartMapper;

    public CabinetPartServiceImpl(CabinetPartRepository cabinetPartRepository, CabinetPartMapper cabinetPartMapper) {
        this.cabinetPartRepository = cabinetPartRepository;
        this.cabinetPartMapper = cabinetPartMapper;
    }

    @Override
    public CabinetPartDTO save(CabinetPartDTO cabinetPartDTO) {
        LOG.debug("Request to save CabinetPart : {}", cabinetPartDTO);
        CabinetPart cabinetPart = cabinetPartMapper.toEntity(cabinetPartDTO);
        cabinetPart = cabinetPartRepository.save(cabinetPart);
        return cabinetPartMapper.toDto(cabinetPart);
    }

    @Override
    public CabinetPartDTO update(CabinetPartDTO cabinetPartDTO) {
        LOG.debug("Request to update CabinetPart : {}", cabinetPartDTO);
        CabinetPart cabinetPart = cabinetPartMapper.toEntity(cabinetPartDTO);
        cabinetPart = cabinetPartRepository.save(cabinetPart);
        return cabinetPartMapper.toDto(cabinetPart);
    }

    @Override
    public Optional<CabinetPartDTO> partialUpdate(CabinetPartDTO cabinetPartDTO) {
        LOG.debug("Request to partially update CabinetPart : {}", cabinetPartDTO);

        return cabinetPartRepository
            .findById(cabinetPartDTO.getId())
            .map(existingCabinetPart -> {
                cabinetPartMapper.partialUpdate(existingCabinetPart, cabinetPartDTO);

                return existingCabinetPart;
            })
            .map(cabinetPartRepository::save)
            .map(cabinetPartMapper::toDto);
    }

    public Page<CabinetPartDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cabinetPartRepository.findAllWithEagerRelationships(pageable).map(cabinetPartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CabinetPartDTO> findOne(Long id) {
        LOG.debug("Request to get CabinetPart : {}", id);
        return cabinetPartRepository.findOneWithEagerRelationships(id).map(cabinetPartMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CabinetPart : {}", id);
        cabinetPartRepository.deleteById(id);
    }
}
