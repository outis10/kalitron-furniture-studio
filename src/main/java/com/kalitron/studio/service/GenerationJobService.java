package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.GenerationJobDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.GenerationJob}.
 */
public interface GenerationJobService {
    /**
     * Save a generationJob.
     *
     * @param generationJobDTO the entity to save.
     * @return the persisted entity.
     */
    GenerationJobDTO save(GenerationJobDTO generationJobDTO);

    /**
     * Updates a generationJob.
     *
     * @param generationJobDTO the entity to update.
     * @return the persisted entity.
     */
    GenerationJobDTO update(GenerationJobDTO generationJobDTO);

    /**
     * Partially updates a generationJob.
     *
     * @param generationJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GenerationJobDTO> partialUpdate(GenerationJobDTO generationJobDTO);

    /**
     * Get all the generationJobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GenerationJobDTO> findAll(Pageable pageable);

    /**
     * Get all the generationJobs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GenerationJobDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" generationJob.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GenerationJobDTO> findOne(Long id);

    /**
     * Delete the "id" generationJob.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
