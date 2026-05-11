package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.CabinetDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.Cabinet}.
 */
public interface CabinetService {
    /**
     * Save a cabinet.
     *
     * @param cabinetDTO the entity to save.
     * @return the persisted entity.
     */
    CabinetDTO save(CabinetDTO cabinetDTO);

    /**
     * Updates a cabinet.
     *
     * @param cabinetDTO the entity to update.
     * @return the persisted entity.
     */
    CabinetDTO update(CabinetDTO cabinetDTO);

    /**
     * Partially updates a cabinet.
     *
     * @param cabinetDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CabinetDTO> partialUpdate(CabinetDTO cabinetDTO);

    /**
     * Get all the cabinets with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CabinetDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" cabinet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CabinetDTO> findOne(Long id);

    /**
     * Delete the "id" cabinet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
