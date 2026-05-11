package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.MaterialDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.Material}.
 */
public interface MaterialService {
    /**
     * Save a material.
     *
     * @param materialDTO the entity to save.
     * @return the persisted entity.
     */
    MaterialDTO save(MaterialDTO materialDTO);

    /**
     * Updates a material.
     *
     * @param materialDTO the entity to update.
     * @return the persisted entity.
     */
    MaterialDTO update(MaterialDTO materialDTO);

    /**
     * Partially updates a material.
     *
     * @param materialDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MaterialDTO> partialUpdate(MaterialDTO materialDTO);

    /**
     * Get the "id" material.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MaterialDTO> findOne(Long id);

    /**
     * Delete the "id" material.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
