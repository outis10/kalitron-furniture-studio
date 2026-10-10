package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.repository.SiteMeasurementRepository;
import com.kalitron.studio.service.SiteMeasurementService;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import com.kalitron.studio.service.mapper.SiteMeasurementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.SiteMeasurement}.
 */
@Service
@Transactional
public class SiteMeasurementServiceImpl implements SiteMeasurementService {

    private static final Logger LOG = LoggerFactory.getLogger(SiteMeasurementServiceImpl.class);

    private final SiteMeasurementRepository siteMeasurementRepository;

    private final SiteMeasurementMapper siteMeasurementMapper;

    public SiteMeasurementServiceImpl(SiteMeasurementRepository siteMeasurementRepository, SiteMeasurementMapper siteMeasurementMapper) {
        this.siteMeasurementRepository = siteMeasurementRepository;
        this.siteMeasurementMapper = siteMeasurementMapper;
    }

    @Override
    public SiteMeasurementDTO save(SiteMeasurementDTO siteMeasurementDTO) {
        LOG.debug("Request to save SiteMeasurement : {}", siteMeasurementDTO);
        SiteMeasurement siteMeasurement = siteMeasurementMapper.toEntity(siteMeasurementDTO);
        siteMeasurement = siteMeasurementRepository.save(siteMeasurement);
        return siteMeasurementMapper.toDto(siteMeasurement);
    }

    @Override
    public SiteMeasurementDTO update(SiteMeasurementDTO siteMeasurementDTO) {
        LOG.debug("Request to update SiteMeasurement : {}", siteMeasurementDTO);
        SiteMeasurement siteMeasurement = siteMeasurementMapper.toEntity(siteMeasurementDTO);
        siteMeasurement = siteMeasurementRepository.save(siteMeasurement);
        return siteMeasurementMapper.toDto(siteMeasurement);
    }

    @Override
    public Optional<SiteMeasurementDTO> partialUpdate(SiteMeasurementDTO siteMeasurementDTO) {
        LOG.debug("Request to partially update SiteMeasurement : {}", siteMeasurementDTO);

        return siteMeasurementRepository
            .findById(siteMeasurementDTO.getId())
            .map(existingSiteMeasurement -> {
                siteMeasurementMapper.partialUpdate(existingSiteMeasurement, siteMeasurementDTO);

                return existingSiteMeasurement;
            })
            .map(siteMeasurementRepository::save)
            .map(siteMeasurementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteMeasurementDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SiteMeasurements");
        return siteMeasurementRepository.findAll(pageable).map(siteMeasurementMapper::toDto);
    }

    public Page<SiteMeasurementDTO> findAllWithEagerRelationships(Pageable pageable) {
        return siteMeasurementRepository.findAllWithEagerRelationships(pageable).map(siteMeasurementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SiteMeasurementDTO> findOne(Long id) {
        LOG.debug("Request to get SiteMeasurement : {}", id);
        return siteMeasurementRepository.findOneWithEagerRelationships(id).map(siteMeasurementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SiteMeasurement : {}", id);
        siteMeasurementRepository.deleteById(id);
    }
}
