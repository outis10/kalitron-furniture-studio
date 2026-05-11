package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.RoomObstacleDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.RoomObstacle}.
 */
public interface RoomObstacleService {
    /**
     * Save a roomObstacle.
     *
     * @param roomObstacleDTO the entity to save.
     * @return the persisted entity.
     */
    RoomObstacleDTO save(RoomObstacleDTO roomObstacleDTO);

    /**
     * Updates a roomObstacle.
     *
     * @param roomObstacleDTO the entity to update.
     * @return the persisted entity.
     */
    RoomObstacleDTO update(RoomObstacleDTO roomObstacleDTO);

    /**
     * Partially updates a roomObstacle.
     *
     * @param roomObstacleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RoomObstacleDTO> partialUpdate(RoomObstacleDTO roomObstacleDTO);

    /**
     * Get all the roomObstacles.
     *
     * @return the list of entities.
     */
    List<RoomObstacleDTO> findAll();

    /**
     * Get all the roomObstacles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RoomObstacleDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" roomObstacle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RoomObstacleDTO> findOne(Long id);

    /**
     * Delete the "id" roomObstacle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
