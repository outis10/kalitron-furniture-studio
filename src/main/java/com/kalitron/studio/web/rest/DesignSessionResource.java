package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.service.DesignSessionQueryService;
import com.kalitron.studio.service.DesignSessionService;
import com.kalitron.studio.service.criteria.DesignSessionCriteria;
import com.kalitron.studio.service.dto.DesignSessionDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.DesignSession}.
 */
@RestController
@RequestMapping("/api/design-sessions")
public class DesignSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSessionResource.class);

    private static final String ENTITY_NAME = "designSession";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final DesignSessionService designSessionService;

    private final DesignSessionRepository designSessionRepository;

    private final DesignSessionQueryService designSessionQueryService;

    public DesignSessionResource(
        DesignSessionService designSessionService,
        DesignSessionRepository designSessionRepository,
        DesignSessionQueryService designSessionQueryService
    ) {
        this.designSessionService = designSessionService;
        this.designSessionRepository = designSessionRepository;
        this.designSessionQueryService = designSessionQueryService;
    }

    /**
     * {@code POST  /design-sessions} : Create a new designSession.
     *
     * @param designSessionDTO the designSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new designSessionDTO, or with status {@code 400 (Bad Request)} if the designSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DesignSessionDTO> createDesignSession(@Valid @RequestBody DesignSessionDTO designSessionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DesignSession : {}", designSessionDTO);
        if (designSessionDTO.getId() != null) {
            throw new BadRequestAlertException("A new designSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        designSessionDTO = designSessionService.save(designSessionDTO);
        return ResponseEntity.created(new URI("/api/design-sessions/" + designSessionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, designSessionDTO.getId().toString()))
            .body(designSessionDTO);
    }

    /**
     * {@code PUT  /design-sessions/:id} : Updates an existing designSession.
     *
     * @param id the id of the designSessionDTO to save.
     * @param designSessionDTO the designSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designSessionDTO,
     * or with status {@code 400 (Bad Request)} if the designSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the designSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DesignSessionDTO> updateDesignSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DesignSessionDTO designSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DesignSession : {}, {}", id, designSessionDTO);
        if (designSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!designSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        designSessionDTO = designSessionService.update(designSessionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, designSessionDTO.getId().toString()))
            .body(designSessionDTO);
    }

    /**
     * {@code PATCH  /design-sessions/:id} : Partial updates given fields of an existing designSession, field will ignore if it is null
     *
     * @param id the id of the designSessionDTO to save.
     * @param designSessionDTO the designSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designSessionDTO,
     * or with status {@code 400 (Bad Request)} if the designSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the designSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the designSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DesignSessionDTO> partialUpdateDesignSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DesignSessionDTO designSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DesignSession partially : {}, {}", id, designSessionDTO);
        if (designSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!designSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DesignSessionDTO> result = designSessionService.partialUpdate(designSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, designSessionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /design-sessions} : get all the Design Sessions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Design Sessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DesignSessionDTO>> getAllDesignSessions(
        DesignSessionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DesignSessions by criteria: {}", criteria);

        Page<DesignSessionDTO> page = designSessionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /design-sessions/count} : count all the designSessions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDesignSessions(DesignSessionCriteria criteria) {
        LOG.debug("REST request to count DesignSessions by criteria: {}", criteria);
        return ResponseEntity.ok().body(designSessionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /design-sessions/:id} : get the "id" designSession.
     *
     * @param id the id of the designSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the designSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DesignSessionDTO> getDesignSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DesignSession : {}", id);
        Optional<DesignSessionDTO> designSessionDTO = designSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(designSessionDTO);
    }

    /**
     * {@code DELETE  /design-sessions/:id} : delete the "id" designSession.
     *
     * @param id the id of the designSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DesignSession : {}", id);
        designSessionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
