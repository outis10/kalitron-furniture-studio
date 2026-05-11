package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.QuoteItemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.QuoteItem}.
 */
public interface QuoteItemService {
    /**
     * Save a quoteItem.
     *
     * @param quoteItemDTO the entity to save.
     * @return the persisted entity.
     */
    QuoteItemDTO save(QuoteItemDTO quoteItemDTO);

    /**
     * Updates a quoteItem.
     *
     * @param quoteItemDTO the entity to update.
     * @return the persisted entity.
     */
    QuoteItemDTO update(QuoteItemDTO quoteItemDTO);

    /**
     * Partially updates a quoteItem.
     *
     * @param quoteItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QuoteItemDTO> partialUpdate(QuoteItemDTO quoteItemDTO);

    /**
     * Get all the quoteItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<QuoteItemDTO> findAll(Pageable pageable);

    /**
     * Get all the quoteItems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<QuoteItemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" quoteItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QuoteItemDTO> findOne(Long id);

    /**
     * Delete the "id" quoteItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
