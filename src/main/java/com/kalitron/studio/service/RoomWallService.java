package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.RoomWallDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.RoomWall}.
 */
public interface RoomWallService {
    /**
     * Save a roomWall.
     *
     * @param roomWallDTO the entity to save.
     * @return the persisted entity.
     */
    RoomWallDTO save(RoomWallDTO roomWallDTO);

    /**
     * Updates a roomWall.
     *
     * @param roomWallDTO the entity to update.
     * @return the persisted entity.
     */
    RoomWallDTO update(RoomWallDTO roomWallDTO);

    /**
     * Partially updates a roomWall.
     *
     * @param roomWallDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RoomWallDTO> partialUpdate(RoomWallDTO roomWallDTO);

    /**
     * Get all the roomWalls.
     *
     * @return the list of entities.
     */
    List<RoomWallDTO> findAll();

    /**
     * Get all the roomWalls with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RoomWallDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" roomWall.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RoomWallDTO> findOne(Long id);

    /**
     * Delete the "id" roomWall.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
