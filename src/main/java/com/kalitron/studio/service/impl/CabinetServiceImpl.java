package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.repository.CabinetRepository;
import com.kalitron.studio.service.CabinetService;
import com.kalitron.studio.service.dto.CabinetDTO;
import com.kalitron.studio.service.mapper.CabinetMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.Cabinet}.
 */
@Service
@Transactional
public class CabinetServiceImpl implements CabinetService {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetServiceImpl.class);

    private final CabinetRepository cabinetRepository;

    private final CabinetMapper cabinetMapper;

    public CabinetServiceImpl(CabinetRepository cabinetRepository, CabinetMapper cabinetMapper) {
        this.cabinetRepository = cabinetRepository;
        this.cabinetMapper = cabinetMapper;
    }

    @Override
    public CabinetDTO save(CabinetDTO cabinetDTO) {
        LOG.debug("Request to save Cabinet : {}", cabinetDTO);
        Cabinet cabinet = cabinetMapper.toEntity(cabinetDTO);
        cabinet = cabinetRepository.save(cabinet);
        return cabinetMapper.toDto(cabinet);
    }

    @Override
    public CabinetDTO update(CabinetDTO cabinetDTO) {
        LOG.debug("Request to update Cabinet : {}", cabinetDTO);
        Cabinet cabinet = cabinetMapper.toEntity(cabinetDTO);
        cabinet = cabinetRepository.save(cabinet);
        return cabinetMapper.toDto(cabinet);
    }

    @Override
    public Optional<CabinetDTO> partialUpdate(CabinetDTO cabinetDTO) {
        LOG.debug("Request to partially update Cabinet : {}", cabinetDTO);

        return cabinetRepository
            .findById(cabinetDTO.getId())
            .map(existingCabinet -> {
                cabinetMapper.partialUpdate(existingCabinet, cabinetDTO);

                return existingCabinet;
            })
            .map(cabinetRepository::save)
            .map(cabinetMapper::toDto);
    }

    public Page<CabinetDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cabinetRepository.findAllWithEagerRelationships(pageable).map(cabinetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CabinetDTO> findOne(Long id) {
        LOG.debug("Request to get Cabinet : {}", id);
        return cabinetRepository.findOneWithEagerRelationships(id).map(cabinetMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Cabinet : {}", id);
        cabinetRepository.deleteById(id);
    }
}
