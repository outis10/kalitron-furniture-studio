package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.CabinetPartDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.CabinetPart}.
 */
public interface CabinetPartService {
    /**
     * Save a cabinetPart.
     *
     * @param cabinetPartDTO the entity to save.
     * @return the persisted entity.
     */
    CabinetPartDTO save(CabinetPartDTO cabinetPartDTO);

    /**
     * Updates a cabinetPart.
     *
     * @param cabinetPartDTO the entity to update.
     * @return the persisted entity.
     */
    CabinetPartDTO update(CabinetPartDTO cabinetPartDTO);

    /**
     * Partially updates a cabinetPart.
     *
     * @param cabinetPartDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CabinetPartDTO> partialUpdate(CabinetPartDTO cabinetPartDTO);

    /**
     * Get all the cabinetParts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CabinetPartDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" cabinetPart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CabinetPartDTO> findOne(Long id);

    /**
     * Delete the "id" cabinetPart.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
