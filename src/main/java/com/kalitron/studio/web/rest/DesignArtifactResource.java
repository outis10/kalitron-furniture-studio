package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.service.DesignArtifactService;
import com.kalitron.studio.service.dto.DesignArtifactDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.DesignArtifact}.
 */
@RestController
@RequestMapping("/api/design-artifacts")
public class DesignArtifactResource {

    private static final Logger LOG = LoggerFactory.getLogger(DesignArtifactResource.class);

    private static final String ENTITY_NAME = "designArtifact";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final DesignArtifactService designArtifactService;

    private final DesignArtifactRepository designArtifactRepository;

    public DesignArtifactResource(DesignArtifactService designArtifactService, DesignArtifactRepository designArtifactRepository) {
        this.designArtifactService = designArtifactService;
        this.designArtifactRepository = designArtifactRepository;
    }

    /**
     * {@code POST  /design-artifacts} : Create a new designArtifact.
     *
     * @param designArtifactDTO the designArtifactDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new designArtifactDTO, or with status {@code 400 (Bad Request)} if the designArtifact has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DesignArtifactDTO> createDesignArtifact(@Valid @RequestBody DesignArtifactDTO designArtifactDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DesignArtifact : {}", designArtifactDTO);
        if (designArtifactDTO.getId() != null) {
            throw new BadRequestAlertException("A new designArtifact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        designArtifactDTO = designArtifactService.save(designArtifactDTO);
        return ResponseEntity.created(new URI("/api/design-artifacts/" + designArtifactDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, designArtifactDTO.getId().toString()))
            .body(designArtifactDTO);
    }

    /**
     * {@code PUT  /design-artifacts/:id} : Updates an existing designArtifact.
     *
     * @param id the id of the designArtifactDTO to save.
     * @param designArtifactDTO the designArtifactDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designArtifactDTO,
     * or with status {@code 400 (Bad Request)} if the designArtifactDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the designArtifactDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DesignArtifactDTO> updateDesignArtifact(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DesignArtifactDTO designArtifactDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DesignArtifact : {}, {}", id, designArtifactDTO);
        if (designArtifactDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designArtifactDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!designArtifactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        designArtifactDTO = designArtifactService.update(designArtifactDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, designArtifactDTO.getId().toString()))
            .body(designArtifactDTO);
    }

    /**
     * {@code PATCH  /design-artifacts/:id} : Partial updates given fields of an existing designArtifact, field will ignore if it is null
     *
     * @param id the id of the designArtifactDTO to save.
     * @param designArtifactDTO the designArtifactDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designArtifactDTO,
     * or with status {@code 400 (Bad Request)} if the designArtifactDTO is not valid,
     * or with status {@code 404 (Not Found)} if the designArtifactDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the designArtifactDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DesignArtifactDTO> partialUpdateDesignArtifact(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DesignArtifactDTO designArtifactDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DesignArtifact partially : {}, {}", id, designArtifactDTO);
        if (designArtifactDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designArtifactDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!designArtifactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DesignArtifactDTO> result = designArtifactService.partialUpdate(designArtifactDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, designArtifactDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /design-artifacts} : get all the Design Artifacts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Design Artifacts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DesignArtifactDTO>> getAllDesignArtifacts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of DesignArtifacts");
        Page<DesignArtifactDTO> page;
        if (eagerload) {
            page = designArtifactService.findAllWithEagerRelationships(pageable);
        } else {
            page = designArtifactService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /design-artifacts/:id} : get the "id" designArtifact.
     *
     * @param id the id of the designArtifactDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the designArtifactDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DesignArtifactDTO> getDesignArtifact(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DesignArtifact : {}", id);
        Optional<DesignArtifactDTO> designArtifactDTO = designArtifactService.findOne(id);
        return ResponseUtil.wrapOrNotFound(designArtifactDTO);
    }

    /**
     * {@code DELETE  /design-artifacts/:id} : delete the "id" designArtifact.
     *
     * @param id the id of the designArtifactDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignArtifact(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DesignArtifact : {}", id);
        designArtifactService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
