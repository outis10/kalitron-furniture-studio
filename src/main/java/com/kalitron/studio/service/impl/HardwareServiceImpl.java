package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.Hardware;
import com.kalitron.studio.repository.HardwareRepository;
import com.kalitron.studio.service.HardwareService;
import com.kalitron.studio.service.dto.HardwareDTO;
import com.kalitron.studio.service.mapper.HardwareMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.Hardware}.
 */
@Service
@Transactional
public class HardwareServiceImpl implements HardwareService {

    private static final Logger LOG = LoggerFactory.getLogger(HardwareServiceImpl.class);

    private final HardwareRepository hardwareRepository;

    private final HardwareMapper hardwareMapper;

    public HardwareServiceImpl(HardwareRepository hardwareRepository, HardwareMapper hardwareMapper) {
        this.hardwareRepository = hardwareRepository;
        this.hardwareMapper = hardwareMapper;
    }

    @Override
    public HardwareDTO save(HardwareDTO hardwareDTO) {
        LOG.debug("Request to save Hardware : {}", hardwareDTO);
        Hardware hardware = hardwareMapper.toEntity(hardwareDTO);
        hardware = hardwareRepository.save(hardware);
        return hardwareMapper.toDto(hardware);
    }

    @Override
    public HardwareDTO update(HardwareDTO hardwareDTO) {
        LOG.debug("Request to update Hardware : {}", hardwareDTO);
        Hardware hardware = hardwareMapper.toEntity(hardwareDTO);
        hardware = hardwareRepository.save(hardware);
        return hardwareMapper.toDto(hardware);
    }

    @Override
    public Optional<HardwareDTO> partialUpdate(HardwareDTO hardwareDTO) {
        LOG.debug("Request to partially update Hardware : {}", hardwareDTO);

        return hardwareRepository
            .findById(hardwareDTO.getId())
            .map(existingHardware -> {
                hardwareMapper.partialUpdate(existingHardware, hardwareDTO);

                return existingHardware;
            })
            .map(hardwareRepository::save)
            .map(hardwareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HardwareDTO> findOne(Long id) {
        LOG.debug("Request to get Hardware : {}", id);
        return hardwareRepository.findById(id).map(hardwareMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Hardware : {}", id);
        hardwareRepository.deleteById(id);
    }
}
