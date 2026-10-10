package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.SiteMeasurement}.
 */
public interface SiteMeasurementService {
    /**
     * Save a siteMeasurement.
     *
     * @param siteMeasurementDTO the entity to save.
     * @return the persisted entity.
     */
    SiteMeasurementDTO save(SiteMeasurementDTO siteMeasurementDTO);

    /**
     * Updates a siteMeasurement.
     *
     * @param siteMeasurementDTO the entity to update.
     * @return the persisted entity.
     */
    SiteMeasurementDTO update(SiteMeasurementDTO siteMeasurementDTO);

    /**
     * Partially updates a siteMeasurement.
     *
     * @param siteMeasurementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SiteMeasurementDTO> partialUpdate(SiteMeasurementDTO siteMeasurementDTO);

    /**
     * Get all the siteMeasurements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SiteMeasurementDTO> findAll(Pageable pageable);

    /**
     * Get all the siteMeasurements with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SiteMeasurementDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" siteMeasurement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SiteMeasurementDTO> findOne(Long id);

    /**
     * Delete the "id" siteMeasurement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
