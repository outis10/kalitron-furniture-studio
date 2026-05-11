package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.DesignImageDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.DesignImage}.
 */
public interface DesignImageService {
    /**
     * Save a designImage.
     *
     * @param designImageDTO the entity to save.
     * @return the persisted entity.
     */
    DesignImageDTO save(DesignImageDTO designImageDTO);

    /**
     * Updates a designImage.
     *
     * @param designImageDTO the entity to update.
     * @return the persisted entity.
     */
    DesignImageDTO update(DesignImageDTO designImageDTO);

    /**
     * Partially updates a designImage.
     *
     * @param designImageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DesignImageDTO> partialUpdate(DesignImageDTO designImageDTO);

    /**
     * Get all the designImages.
     *
     * @return the list of entities.
     */
    List<DesignImageDTO> findAll();

    /**
     * Get all the designImages with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DesignImageDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" designImage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DesignImageDTO> findOne(Long id);

    /**
     * Delete the "id" designImage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
