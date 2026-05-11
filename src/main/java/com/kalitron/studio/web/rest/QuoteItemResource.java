package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.QuoteItemRepository;
import com.kalitron.studio.service.QuoteItemService;
import com.kalitron.studio.service.dto.QuoteItemDTO;
import com.kalitron.studio.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.kalitron.studio.domain.QuoteItem}.
 */
@RestController
@RequestMapping("/api/quote-items")
public class QuoteItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteItemResource.class);

    private static final String ENTITY_NAME = "quoteItem";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final QuoteItemService quoteItemService;

    private final QuoteItemRepository quoteItemRepository;

    public QuoteItemResource(QuoteItemService quoteItemService, QuoteItemRepository quoteItemRepository) {
        this.quoteItemService = quoteItemService;
        this.quoteItemRepository = quoteItemRepository;
    }

    /**
     * {@code POST  /quote-items} : Create a new quoteItem.
     *
     * @param quoteItemDTO the quoteItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quoteItemDTO, or with status {@code 400 (Bad Request)} if the quoteItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuoteItemDTO> createQuoteItem(@Valid @RequestBody QuoteItemDTO quoteItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save QuoteItem : {}", quoteItemDTO);
        if (quoteItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new quoteItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        quoteItemDTO = quoteItemService.save(quoteItemDTO);
        return ResponseEntity.created(new URI("/api/quote-items/" + quoteItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, quoteItemDTO.getId().toString()))
            .body(quoteItemDTO);
    }

    /**
     * {@code PUT  /quote-items/:id} : Updates an existing quoteItem.
     *
     * @param id the id of the quoteItemDTO to save.
     * @param quoteItemDTO the quoteItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quoteItemDTO,
     * or with status {@code 400 (Bad Request)} if the quoteItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quoteItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuoteItemDTO> updateQuoteItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuoteItemDTO quoteItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update QuoteItem : {}, {}", id, quoteItemDTO);
        if (quoteItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quoteItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quoteItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        quoteItemDTO = quoteItemService.update(quoteItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quoteItemDTO.getId().toString()))
            .body(quoteItemDTO);
    }

    /**
     * {@code PATCH  /quote-items/:id} : Partial updates given fields of an existing quoteItem, field will ignore if it is null
     *
     * @param id the id of the quoteItemDTO to save.
     * @param quoteItemDTO the quoteItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quoteItemDTO,
     * or with status {@code 400 (Bad Request)} if the quoteItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the quoteItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the quoteItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuoteItemDTO> partialUpdateQuoteItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuoteItemDTO quoteItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update QuoteItem partially : {}, {}", id, quoteItemDTO);
        if (quoteItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quoteItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quoteItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuoteItemDTO> result = quoteItemService.partialUpdate(quoteItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quoteItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /quote-items} : get all the Quote Items.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Quote Items in body.
     */
    @GetMapping("")
    public ResponseEntity<List<QuoteItemDTO>> getAllQuoteItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of QuoteItems");
        Page<QuoteItemDTO> page;
        if (eagerload) {
            page = quoteItemService.findAllWithEagerRelationships(pageable);
        } else {
            page = quoteItemService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /quote-items/:id} : get the "id" quoteItem.
     *
     * @param id the id of the quoteItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quoteItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuoteItemDTO> getQuoteItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get QuoteItem : {}", id);
        Optional<QuoteItemDTO> quoteItemDTO = quoteItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quoteItemDTO);
    }

    /**
     * {@code DELETE  /quote-items/:id} : delete the "id" quoteItem.
     *
     * @param id the id of the quoteItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuoteItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete QuoteItem : {}", id);
        quoteItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
