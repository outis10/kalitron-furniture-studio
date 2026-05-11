package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.CabinetTemplateDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.CabinetTemplate}.
 */
public interface CabinetTemplateService {
    /**
     * Save a cabinetTemplate.
     *
     * @param cabinetTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    CabinetTemplateDTO save(CabinetTemplateDTO cabinetTemplateDTO);

    /**
     * Updates a cabinetTemplate.
     *
     * @param cabinetTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    CabinetTemplateDTO update(CabinetTemplateDTO cabinetTemplateDTO);

    /**
     * Partially updates a cabinetTemplate.
     *
     * @param cabinetTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CabinetTemplateDTO> partialUpdate(CabinetTemplateDTO cabinetTemplateDTO);

    /**
     * Get the "id" cabinetTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CabinetTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" cabinetTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
