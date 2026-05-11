package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.CabinetRepository;
import com.kalitron.studio.service.CabinetQueryService;
import com.kalitron.studio.service.CabinetService;
import com.kalitron.studio.service.criteria.CabinetCriteria;
import com.kalitron.studio.service.dto.CabinetDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.Cabinet}.
 */
@RestController
@RequestMapping("/api/cabinets")
public class CabinetResource {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetResource.class);

    private static final String ENTITY_NAME = "cabinet";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final CabinetService cabinetService;

    private final CabinetRepository cabinetRepository;

    private final CabinetQueryService cabinetQueryService;

    public CabinetResource(CabinetService cabinetService, CabinetRepository cabinetRepository, CabinetQueryService cabinetQueryService) {
        this.cabinetService = cabinetService;
        this.cabinetRepository = cabinetRepository;
        this.cabinetQueryService = cabinetQueryService;
    }

    /**
     * {@code POST  /cabinets} : Create a new cabinet.
     *
     * @param cabinetDTO the cabinetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cabinetDTO, or with status {@code 400 (Bad Request)} if the cabinet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CabinetDTO> createCabinet(@Valid @RequestBody CabinetDTO cabinetDTO) throws URISyntaxException {
        LOG.debug("REST request to save Cabinet : {}", cabinetDTO);
        if (cabinetDTO.getId() != null) {
            throw new BadRequestAlertException("A new cabinet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cabinetDTO = cabinetService.save(cabinetDTO);
        return ResponseEntity.created(new URI("/api/cabinets/" + cabinetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cabinetDTO.getId().toString()))
            .body(cabinetDTO);
    }

    /**
     * {@code PUT  /cabinets/:id} : Updates an existing cabinet.
     *
     * @param id the id of the cabinetDTO to save.
     * @param cabinetDTO the cabinetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cabinetDTO,
     * or with status {@code 400 (Bad Request)} if the cabinetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cabinetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CabinetDTO> updateCabinet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CabinetDTO cabinetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Cabinet : {}, {}", id, cabinetDTO);
        if (cabinetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cabinetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cabinetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cabinetDTO = cabinetService.update(cabinetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cabinetDTO.getId().toString()))
            .body(cabinetDTO);
    }

    /**
     * {@code PATCH  /cabinets/:id} : Partial updates given fields of an existing cabinet, field will ignore if it is null
     *
     * @param id the id of the cabinetDTO to save.
     * @param cabinetDTO the cabinetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cabinetDTO,
     * or with status {@code 400 (Bad Request)} if the cabinetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cabinetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cabinetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CabinetDTO> partialUpdateCabinet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CabinetDTO cabinetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Cabinet partially : {}, {}", id, cabinetDTO);
        if (cabinetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cabinetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cabinetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CabinetDTO> result = cabinetService.partialUpdate(cabinetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cabinetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cabinets} : get all the Cabinets.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cabinets in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CabinetDTO>> getAllCabinets(
        CabinetCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Cabinets by criteria: {}", criteria);

        Page<CabinetDTO> page = cabinetQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cabinets/count} : count all the cabinets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCabinets(CabinetCriteria criteria) {
        LOG.debug("REST request to count Cabinets by criteria: {}", criteria);
        return ResponseEntity.ok().body(cabinetQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cabinets/:id} : get the "id" cabinet.
     *
     * @param id the id of the cabinetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cabinetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CabinetDTO> getCabinet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Cabinet : {}", id);
        Optional<CabinetDTO> cabinetDTO = cabinetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cabinetDTO);
    }

    /**
     * {@code DELETE  /cabinets/:id} : delete the "id" cabinet.
     *
     * @param id the id of the cabinetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCabinet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Cabinet : {}", id);
        cabinetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
