package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.CatalogStyleRepository;
import com.kalitron.studio.service.CatalogStyleService;
import com.kalitron.studio.service.dto.CatalogStyleDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.kalitron.studio.domain.CatalogStyle}.
 */
@RestController
@RequestMapping("/api/catalog-styles")
public class CatalogStyleResource {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogStyleResource.class);

    private static final String ENTITY_NAME = "catalogStyle";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final CatalogStyleService catalogStyleService;

    private final CatalogStyleRepository catalogStyleRepository;

    public CatalogStyleResource(CatalogStyleService catalogStyleService, CatalogStyleRepository catalogStyleRepository) {
        this.catalogStyleService = catalogStyleService;
        this.catalogStyleRepository = catalogStyleRepository;
    }

    /**
     * {@code POST  /catalog-styles} : Create a new catalogStyle.
     *
     * @param catalogStyleDTO the catalogStyleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new catalogStyleDTO, or with status {@code 400 (Bad Request)} if the catalogStyle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CatalogStyleDTO> createCatalogStyle(@Valid @RequestBody CatalogStyleDTO catalogStyleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CatalogStyle : {}", catalogStyleDTO);
        if (catalogStyleDTO.getId() != null) {
            throw new BadRequestAlertException("A new catalogStyle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        catalogStyleDTO = catalogStyleService.save(catalogStyleDTO);
        return ResponseEntity.created(new URI("/api/catalog-styles/" + catalogStyleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, catalogStyleDTO.getId().toString()))
            .body(catalogStyleDTO);
    }

    /**
     * {@code PUT  /catalog-styles/:id} : Updates an existing catalogStyle.
     *
     * @param id the id of the catalogStyleDTO to save.
     * @param catalogStyleDTO the catalogStyleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogStyleDTO,
     * or with status {@code 400 (Bad Request)} if the catalogStyleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the catalogStyleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CatalogStyleDTO> updateCatalogStyle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CatalogStyleDTO catalogStyleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CatalogStyle : {}, {}", id, catalogStyleDTO);
        if (catalogStyleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogStyleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!catalogStyleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        catalogStyleDTO = catalogStyleService.update(catalogStyleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, catalogStyleDTO.getId().toString()))
            .body(catalogStyleDTO);
    }

    /**
     * {@code PATCH  /catalog-styles/:id} : Partial updates given fields of an existing catalogStyle, field will ignore if it is null
     *
     * @param id the id of the catalogStyleDTO to save.
     * @param catalogStyleDTO the catalogStyleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogStyleDTO,
     * or with status {@code 400 (Bad Request)} if the catalogStyleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the catalogStyleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the catalogStyleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CatalogStyleDTO> partialUpdateCatalogStyle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CatalogStyleDTO catalogStyleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CatalogStyle partially : {}, {}", id, catalogStyleDTO);
        if (catalogStyleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogStyleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!catalogStyleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CatalogStyleDTO> result = catalogStyleService.partialUpdate(catalogStyleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, catalogStyleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /catalog-styles} : get all the Catalog Styles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Catalog Styles in body.
     */
    @GetMapping("")
    public List<CatalogStyleDTO> getAllCatalogStyles() {
        LOG.debug("REST request to get all CatalogStyles");
        return catalogStyleService.findAll();
    }

    /**
     * {@code GET  /catalog-styles/:id} : get the "id" catalogStyle.
     *
     * @param id the id of the catalogStyleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the catalogStyleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CatalogStyleDTO> getCatalogStyle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CatalogStyle : {}", id);
        Optional<CatalogStyleDTO> catalogStyleDTO = catalogStyleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(catalogStyleDTO);
    }

    /**
     * {@code DELETE  /catalog-styles/:id} : delete the "id" catalogStyle.
     *
     * @param id the id of the catalogStyleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalogStyle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CatalogStyle : {}", id);
        catalogStyleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
