package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.SiteMeasurementRepository;
import com.kalitron.studio.service.SiteMeasurementService;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.SiteMeasurement}.
 */
@RestController
@RequestMapping("/api/site-measurements")
public class SiteMeasurementResource {

    private static final Logger LOG = LoggerFactory.getLogger(SiteMeasurementResource.class);

    private static final String ENTITY_NAME = "siteMeasurement";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final SiteMeasurementService siteMeasurementService;

    private final SiteMeasurementRepository siteMeasurementRepository;

    public SiteMeasurementResource(SiteMeasurementService siteMeasurementService, SiteMeasurementRepository siteMeasurementRepository) {
        this.siteMeasurementService = siteMeasurementService;
        this.siteMeasurementRepository = siteMeasurementRepository;
    }

    /**
     * {@code POST  /site-measurements} : Create a new siteMeasurement.
     *
     * @param siteMeasurementDTO the siteMeasurementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new siteMeasurementDTO, or with status {@code 400 (Bad Request)} if the siteMeasurement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SiteMeasurementDTO> createSiteMeasurement(@Valid @RequestBody SiteMeasurementDTO siteMeasurementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SiteMeasurement : {}", siteMeasurementDTO);
        if (siteMeasurementDTO.getId() != null) {
            throw new BadRequestAlertException("A new siteMeasurement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        siteMeasurementDTO = siteMeasurementService.save(siteMeasurementDTO);
        return ResponseEntity.created(new URI("/api/site-measurements/" + siteMeasurementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, siteMeasurementDTO.getId().toString()))
            .body(siteMeasurementDTO);
    }

    /**
     * {@code PUT  /site-measurements/:id} : Updates an existing siteMeasurement.
     *
     * @param id the id of the siteMeasurementDTO to save.
     * @param siteMeasurementDTO the siteMeasurementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteMeasurementDTO,
     * or with status {@code 400 (Bad Request)} if the siteMeasurementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the siteMeasurementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SiteMeasurementDTO> updateSiteMeasurement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SiteMeasurementDTO siteMeasurementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SiteMeasurement : {}, {}", id, siteMeasurementDTO);
        if (siteMeasurementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, siteMeasurementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!siteMeasurementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        siteMeasurementDTO = siteMeasurementService.update(siteMeasurementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, siteMeasurementDTO.getId().toString()))
            .body(siteMeasurementDTO);
    }

    /**
     * {@code PATCH  /site-measurements/:id} : Partial updates given fields of an existing siteMeasurement, field will ignore if it is null
     *
     * @param id the id of the siteMeasurementDTO to save.
     * @param siteMeasurementDTO the siteMeasurementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteMeasurementDTO,
     * or with status {@code 400 (Bad Request)} if the siteMeasurementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the siteMeasurementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the siteMeasurementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SiteMeasurementDTO> partialUpdateSiteMeasurement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SiteMeasurementDTO siteMeasurementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SiteMeasurement partially : {}, {}", id, siteMeasurementDTO);
        if (siteMeasurementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, siteMeasurementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!siteMeasurementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SiteMeasurementDTO> result = siteMeasurementService.partialUpdate(siteMeasurementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, siteMeasurementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /site-measurements} : get all the Site Measurements.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Site Measurements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SiteMeasurementDTO>> getAllSiteMeasurements(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SiteMeasurements");
        Page<SiteMeasurementDTO> page;
        if (eagerload) {
            page = siteMeasurementService.findAllWithEagerRelationships(pageable);
        } else {
            page = siteMeasurementService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /site-measurements/:id} : get the "id" siteMeasurement.
     *
     * @param id the id of the siteMeasurementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the siteMeasurementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SiteMeasurementDTO> getSiteMeasurement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SiteMeasurement : {}", id);
        Optional<SiteMeasurementDTO> siteMeasurementDTO = siteMeasurementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(siteMeasurementDTO);
    }

    /**
     * {@code DELETE  /site-measurements/:id} : delete the "id" siteMeasurement.
     *
     * @param id the id of the siteMeasurementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSiteMeasurement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SiteMeasurement : {}", id);
        siteMeasurementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
