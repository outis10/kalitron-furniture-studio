package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.CatalogStyleDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.CatalogStyle}.
 */
public interface CatalogStyleService {
    /**
     * Save a catalogStyle.
     *
     * @param catalogStyleDTO the entity to save.
     * @return the persisted entity.
     */
    CatalogStyleDTO save(CatalogStyleDTO catalogStyleDTO);

    /**
     * Updates a catalogStyle.
     *
     * @param catalogStyleDTO the entity to update.
     * @return the persisted entity.
     */
    CatalogStyleDTO update(CatalogStyleDTO catalogStyleDTO);

    /**
     * Partially updates a catalogStyle.
     *
     * @param catalogStyleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CatalogStyleDTO> partialUpdate(CatalogStyleDTO catalogStyleDTO);

    /**
     * Get all the catalogStyles.
     *
     * @return the list of entities.
     */
    List<CatalogStyleDTO> findAll();

    /**
     * Get the "id" catalogStyle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CatalogStyleDTO> findOne(Long id);

    /**
     * Delete the "id" catalogStyle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
