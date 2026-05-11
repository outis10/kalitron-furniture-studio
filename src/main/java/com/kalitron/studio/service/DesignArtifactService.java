package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.DesignArtifactDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.DesignArtifact}.
 */
public interface DesignArtifactService {
    /**
     * Save a designArtifact.
     *
     * @param designArtifactDTO the entity to save.
     * @return the persisted entity.
     */
    DesignArtifactDTO save(DesignArtifactDTO designArtifactDTO);

    /**
     * Updates a designArtifact.
     *
     * @param designArtifactDTO the entity to update.
     * @return the persisted entity.
     */
    DesignArtifactDTO update(DesignArtifactDTO designArtifactDTO);

    /**
     * Partially updates a designArtifact.
     *
     * @param designArtifactDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DesignArtifactDTO> partialUpdate(DesignArtifactDTO designArtifactDTO);

    /**
     * Get all the designArtifacts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DesignArtifactDTO> findAll(Pageable pageable);

    /**
     * Get all the designArtifacts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DesignArtifactDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" designArtifact.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DesignArtifactDTO> findOne(Long id);

    /**
     * Delete the "id" designArtifact.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
