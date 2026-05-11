package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.repository.CabinetTemplateRepository;
import com.kalitron.studio.service.CabinetTemplateService;
import com.kalitron.studio.service.dto.CabinetTemplateDTO;
import com.kalitron.studio.service.mapper.CabinetTemplateMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.CabinetTemplate}.
 */
@Service
@Transactional
public class CabinetTemplateServiceImpl implements CabinetTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetTemplateServiceImpl.class);

    private final CabinetTemplateRepository cabinetTemplateRepository;

    private final CabinetTemplateMapper cabinetTemplateMapper;

    public CabinetTemplateServiceImpl(CabinetTemplateRepository cabinetTemplateRepository, CabinetTemplateMapper cabinetTemplateMapper) {
        this.cabinetTemplateRepository = cabinetTemplateRepository;
        this.cabinetTemplateMapper = cabinetTemplateMapper;
    }

    @Override
    public CabinetTemplateDTO save(CabinetTemplateDTO cabinetTemplateDTO) {
        LOG.debug("Request to save CabinetTemplate : {}", cabinetTemplateDTO);
        CabinetTemplate cabinetTemplate = cabinetTemplateMapper.toEntity(cabinetTemplateDTO);
        cabinetTemplate = cabinetTemplateRepository.save(cabinetTemplate);
        return cabinetTemplateMapper.toDto(cabinetTemplate);
    }

    @Override
    public CabinetTemplateDTO update(CabinetTemplateDTO cabinetTemplateDTO) {
        LOG.debug("Request to update CabinetTemplate : {}", cabinetTemplateDTO);
        CabinetTemplate cabinetTemplate = cabinetTemplateMapper.toEntity(cabinetTemplateDTO);
        cabinetTemplate = cabinetTemplateRepository.save(cabinetTemplate);
        return cabinetTemplateMapper.toDto(cabinetTemplate);
    }

    @Override
    public Optional<CabinetTemplateDTO> partialUpdate(CabinetTemplateDTO cabinetTemplateDTO) {
        LOG.debug("Request to partially update CabinetTemplate : {}", cabinetTemplateDTO);

        return cabinetTemplateRepository
            .findById(cabinetTemplateDTO.getId())
            .map(existingCabinetTemplate -> {
                cabinetTemplateMapper.partialUpdate(existingCabinetTemplate, cabinetTemplateDTO);

                return existingCabinetTemplate;
            })
            .map(cabinetTemplateRepository::save)
            .map(cabinetTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CabinetTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get CabinetTemplate : {}", id);
        return cabinetTemplateRepository.findById(id).map(cabinetTemplateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CabinetTemplate : {}", id);
        cabinetTemplateRepository.deleteById(id);
    }
}
