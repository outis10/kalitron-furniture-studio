package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.KitchenSpecDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.KitchenSpec}.
 */
public interface KitchenSpecService {
    /**
     * Save a kitchenSpec.
     *
     * @param kitchenSpecDTO the entity to save.
     * @return the persisted entity.
     */
    KitchenSpecDTO save(KitchenSpecDTO kitchenSpecDTO);

    /**
     * Updates a kitchenSpec.
     *
     * @param kitchenSpecDTO the entity to update.
     * @return the persisted entity.
     */
    KitchenSpecDTO update(KitchenSpecDTO kitchenSpecDTO);

    /**
     * Partially updates a kitchenSpec.
     *
     * @param kitchenSpecDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KitchenSpecDTO> partialUpdate(KitchenSpecDTO kitchenSpecDTO);

    /**
     * Get all the KitchenSpecDTO where Session is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<KitchenSpecDTO> findAllWhereSessionIsNull();

    /**
     * Get all the kitchenSpecs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<KitchenSpecDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" kitchenSpec.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KitchenSpecDTO> findOne(Long id);

    /**
     * Delete the "id" kitchenSpec.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
