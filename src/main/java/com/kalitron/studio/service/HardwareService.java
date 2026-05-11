package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.HardwareDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.Hardware}.
 */
public interface HardwareService {
    /**
     * Save a hardware.
     *
     * @param hardwareDTO the entity to save.
     * @return the persisted entity.
     */
    HardwareDTO save(HardwareDTO hardwareDTO);

    /**
     * Updates a hardware.
     *
     * @param hardwareDTO the entity to update.
     * @return the persisted entity.
     */
    HardwareDTO update(HardwareDTO hardwareDTO);

    /**
     * Partially updates a hardware.
     *
     * @param hardwareDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HardwareDTO> partialUpdate(HardwareDTO hardwareDTO);

    /**
     * Get the "id" hardware.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HardwareDTO> findOne(Long id);

    /**
     * Delete the "id" hardware.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
