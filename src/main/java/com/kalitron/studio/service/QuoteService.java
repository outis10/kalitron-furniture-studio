package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.QuoteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.kalitron.studio.domain.Quote}.
 */
public interface QuoteService {
    /**
     * Save a quote.
     *
     * @param quoteDTO the entity to save.
     * @return the persisted entity.
     */
    QuoteDTO save(QuoteDTO quoteDTO);

    /**
     * Updates a quote.
     *
     * @param quoteDTO the entity to update.
     * @return the persisted entity.
     */
    QuoteDTO update(QuoteDTO quoteDTO);

    /**
     * Partially updates a quote.
     *
     * @param quoteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QuoteDTO> partialUpdate(QuoteDTO quoteDTO);

    /**
     * Get all the quotes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<QuoteDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" quote.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QuoteDTO> findOne(Long id);

    /**
     * Delete the "id" quote.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
