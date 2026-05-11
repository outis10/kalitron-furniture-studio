package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.CabinetPartRepository;
import com.kalitron.studio.service.CabinetPartQueryService;
import com.kalitron.studio.service.CabinetPartService;
import com.kalitron.studio.service.criteria.CabinetPartCriteria;
import com.kalitron.studio.service.dto.CabinetPartDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.CabinetPart}.
 */
@RestController
@RequestMapping("/api/cabinet-parts")
public class CabinetPartResource {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetPartResource.class);

    private static final String ENTITY_NAME = "cabinetPart";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final CabinetPartService cabinetPartService;

    private final CabinetPartRepository cabinetPartRepository;

    private final CabinetPartQueryService cabinetPartQueryService;

    public CabinetPartResource(
        CabinetPartService cabinetPartService,
        CabinetPartRepository cabinetPartRepository,
        CabinetPartQueryService cabinetPartQueryService
    ) {
        this.cabinetPartService = cabinetPartService;
        this.cabinetPartRepository = cabinetPartRepository;
        this.cabinetPartQueryService = cabinetPartQueryService;
    }

    /**
     * {@code POST  /cabinet-parts} : Create a new cabinetPart.
     *
     * @param cabinetPartDTO the cabinetPartDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cabinetPartDTO, or with status {@code 400 (Bad Request)} if the cabinetPart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CabinetPartDTO> createCabinetPart(@Valid @RequestBody CabinetPartDTO cabinetPartDTO) throws URISyntaxException {
        LOG.debug("REST request to save CabinetPart : {}", cabinetPartDTO);
        if (cabinetPartDTO.getId() != null) {
            throw new BadRequestAlertException("A new cabinetPart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cabinetPartDTO = cabinetPartService.save(cabinetPartDTO);
        return ResponseEntity.created(new URI("/api/cabinet-parts/" + cabinetPartDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cabinetPartDTO.getId().toString()))
            .body(cabinetPartDTO);
    }

    /**
     * {@code PUT  /cabinet-parts/:id} : Updates an existing cabinetPart.
     *
     * @param id the id of the cabinetPartDTO to save.
     * @param cabinetPartDTO the cabinetPartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cabinetPartDTO,
     * or with status {@code 400 (Bad Request)} if the cabinetPartDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cabinetPartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CabinetPartDTO> updateCabinetPart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CabinetPartDTO cabinetPartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CabinetPart : {}, {}", id, cabinetPartDTO);
        if (cabinetPartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cabinetPartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cabinetPartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cabinetPartDTO = cabinetPartService.update(cabinetPartDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cabinetPartDTO.getId().toString()))
            .body(cabinetPartDTO);
    }

    /**
     * {@code PATCH  /cabinet-parts/:id} : Partial updates given fields of an existing cabinetPart, field will ignore if it is null
     *
     * @param id the id of the cabinetPartDTO to save.
     * @param cabinetPartDTO the cabinetPartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cabinetPartDTO,
     * or with status {@code 400 (Bad Request)} if the cabinetPartDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cabinetPartDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cabinetPartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CabinetPartDTO> partialUpdateCabinetPart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CabinetPartDTO cabinetPartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CabinetPart partially : {}, {}", id, cabinetPartDTO);
        if (cabinetPartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cabinetPartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cabinetPartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CabinetPartDTO> result = cabinetPartService.partialUpdate(cabinetPartDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cabinetPartDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cabinet-parts} : get all the Cabinet Parts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cabinet Parts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CabinetPartDTO>> getAllCabinetParts(
        CabinetPartCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CabinetParts by criteria: {}", criteria);

        Page<CabinetPartDTO> page = cabinetPartQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cabinet-parts/count} : count all the cabinetParts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCabinetParts(CabinetPartCriteria criteria) {
        LOG.debug("REST request to count CabinetParts by criteria: {}", criteria);
        return ResponseEntity.ok().body(cabinetPartQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cabinet-parts/:id} : get the "id" cabinetPart.
     *
     * @param id the id of the cabinetPartDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cabinetPartDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CabinetPartDTO> getCabinetPart(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CabinetPart : {}", id);
        Optional<CabinetPartDTO> cabinetPartDTO = cabinetPartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cabinetPartDTO);
    }

    /**
     * {@code DELETE  /cabinet-parts/:id} : delete the "id" cabinetPart.
     *
     * @param id the id of the cabinetPartDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCabinetPart(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CabinetPart : {}", id);
        cabinetPartService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
