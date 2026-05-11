package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.KitchenSpecRepository;
import com.kalitron.studio.service.KitchenSpecQueryService;
import com.kalitron.studio.service.KitchenSpecService;
import com.kalitron.studio.service.criteria.KitchenSpecCriteria;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.KitchenSpec}.
 */
@RestController
@RequestMapping("/api/kitchen-specs")
public class KitchenSpecResource {

    private static final Logger LOG = LoggerFactory.getLogger(KitchenSpecResource.class);

    private static final String ENTITY_NAME = "kitchenSpec";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final KitchenSpecService kitchenSpecService;

    private final KitchenSpecRepository kitchenSpecRepository;

    private final KitchenSpecQueryService kitchenSpecQueryService;

    public KitchenSpecResource(
        KitchenSpecService kitchenSpecService,
        KitchenSpecRepository kitchenSpecRepository,
        KitchenSpecQueryService kitchenSpecQueryService
    ) {
        this.kitchenSpecService = kitchenSpecService;
        this.kitchenSpecRepository = kitchenSpecRepository;
        this.kitchenSpecQueryService = kitchenSpecQueryService;
    }

    /**
     * {@code POST  /kitchen-specs} : Create a new kitchenSpec.
     *
     * @param kitchenSpecDTO the kitchenSpecDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new kitchenSpecDTO, or with status {@code 400 (Bad Request)} if the kitchenSpec has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KitchenSpecDTO> createKitchenSpec(@Valid @RequestBody KitchenSpecDTO kitchenSpecDTO) throws URISyntaxException {
        LOG.debug("REST request to save KitchenSpec : {}", kitchenSpecDTO);
        if (kitchenSpecDTO.getId() != null) {
            throw new BadRequestAlertException("A new kitchenSpec cannot already have an ID", ENTITY_NAME, "idexists");
        }
        kitchenSpecDTO = kitchenSpecService.save(kitchenSpecDTO);
        return ResponseEntity.created(new URI("/api/kitchen-specs/" + kitchenSpecDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, kitchenSpecDTO.getId().toString()))
            .body(kitchenSpecDTO);
    }

    /**
     * {@code PUT  /kitchen-specs/:id} : Updates an existing kitchenSpec.
     *
     * @param id the id of the kitchenSpecDTO to save.
     * @param kitchenSpecDTO the kitchenSpecDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kitchenSpecDTO,
     * or with status {@code 400 (Bad Request)} if the kitchenSpecDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the kitchenSpecDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KitchenSpecDTO> updateKitchenSpec(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody KitchenSpecDTO kitchenSpecDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KitchenSpec : {}, {}", id, kitchenSpecDTO);
        if (kitchenSpecDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kitchenSpecDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!kitchenSpecRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        kitchenSpecDTO = kitchenSpecService.update(kitchenSpecDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, kitchenSpecDTO.getId().toString()))
            .body(kitchenSpecDTO);
    }

    /**
     * {@code PATCH  /kitchen-specs/:id} : Partial updates given fields of an existing kitchenSpec, field will ignore if it is null
     *
     * @param id the id of the kitchenSpecDTO to save.
     * @param kitchenSpecDTO the kitchenSpecDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kitchenSpecDTO,
     * or with status {@code 400 (Bad Request)} if the kitchenSpecDTO is not valid,
     * or with status {@code 404 (Not Found)} if the kitchenSpecDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the kitchenSpecDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KitchenSpecDTO> partialUpdateKitchenSpec(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KitchenSpecDTO kitchenSpecDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update KitchenSpec partially : {}, {}", id, kitchenSpecDTO);
        if (kitchenSpecDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kitchenSpecDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!kitchenSpecRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KitchenSpecDTO> result = kitchenSpecService.partialUpdate(kitchenSpecDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, kitchenSpecDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /kitchen-specs} : get all the Kitchen Specs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Kitchen Specs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KitchenSpecDTO>> getAllKitchenSpecs(KitchenSpecCriteria criteria) {
        LOG.debug("REST request to get KitchenSpecs by criteria: {}", criteria);

        List<KitchenSpecDTO> entityList = kitchenSpecQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /kitchen-specs/count} : count all the kitchenSpecs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countKitchenSpecs(KitchenSpecCriteria criteria) {
        LOG.debug("REST request to count KitchenSpecs by criteria: {}", criteria);
        return ResponseEntity.ok().body(kitchenSpecQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /kitchen-specs/:id} : get the "id" kitchenSpec.
     *
     * @param id the id of the kitchenSpecDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the kitchenSpecDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KitchenSpecDTO> getKitchenSpec(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KitchenSpec : {}", id);
        Optional<KitchenSpecDTO> kitchenSpecDTO = kitchenSpecService.findOne(id);
        return ResponseUtil.wrapOrNotFound(kitchenSpecDTO);
    }

    /**
     * {@code DELETE  /kitchen-specs/:id} : delete the "id" kitchenSpec.
     *
     * @param id the id of the kitchenSpecDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKitchenSpec(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KitchenSpec : {}", id);
        kitchenSpecService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
