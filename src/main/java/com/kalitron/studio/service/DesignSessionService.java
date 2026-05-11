package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.DesignSessionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.DesignSession}.
 */
public interface DesignSessionService {
    /**
     * Save a designSession.
     *
     * @param designSessionDTO the entity to save.
     * @return the persisted entity.
     */
    DesignSessionDTO save(DesignSessionDTO designSessionDTO);

    /**
     * Updates a designSession.
     *
     * @param designSessionDTO the entity to update.
     * @return the persisted entity.
     */
    DesignSessionDTO update(DesignSessionDTO designSessionDTO);

    /**
     * Partially updates a designSession.
     *
     * @param designSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DesignSessionDTO> partialUpdate(DesignSessionDTO designSessionDTO);

    /**
     * Get all the designSessions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DesignSessionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" designSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DesignSessionDTO> findOne(Long id);

    /**
     * Delete the "id" designSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
