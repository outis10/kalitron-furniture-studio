package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.GenerationJobRepository;
import com.kalitron.studio.service.GenerationJobService;
import com.kalitron.studio.service.dto.GenerationJobDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.GenerationJob}.
 */
@RestController
@RequestMapping("/api/generation-jobs")
public class GenerationJobResource {

    private static final Logger LOG = LoggerFactory.getLogger(GenerationJobResource.class);

    private static final String ENTITY_NAME = "generationJob";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final GenerationJobService generationJobService;

    private final GenerationJobRepository generationJobRepository;

    public GenerationJobResource(GenerationJobService generationJobService, GenerationJobRepository generationJobRepository) {
        this.generationJobService = generationJobService;
        this.generationJobRepository = generationJobRepository;
    }

    /**
     * {@code POST  /generation-jobs} : Create a new generationJob.
     *
     * @param generationJobDTO the generationJobDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new generationJobDTO, or with status {@code 400 (Bad Request)} if the generationJob has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GenerationJobDTO> createGenerationJob(@Valid @RequestBody GenerationJobDTO generationJobDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save GenerationJob : {}", generationJobDTO);
        if (generationJobDTO.getId() != null) {
            throw new BadRequestAlertException("A new generationJob cannot already have an ID", ENTITY_NAME, "idexists");
        }
        generationJobDTO = generationJobService.save(generationJobDTO);
        return ResponseEntity.created(new URI("/api/generation-jobs/" + generationJobDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, generationJobDTO.getId().toString()))
            .body(generationJobDTO);
    }

    /**
     * {@code PUT  /generation-jobs/:id} : Updates an existing generationJob.
     *
     * @param id the id of the generationJobDTO to save.
     * @param generationJobDTO the generationJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated generationJobDTO,
     * or with status {@code 400 (Bad Request)} if the generationJobDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the generationJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GenerationJobDTO> updateGenerationJob(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GenerationJobDTO generationJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update GenerationJob : {}, {}", id, generationJobDTO);
        if (generationJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, generationJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!generationJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        generationJobDTO = generationJobService.update(generationJobDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, generationJobDTO.getId().toString()))
            .body(generationJobDTO);
    }

    /**
     * {@code PATCH  /generation-jobs/:id} : Partial updates given fields of an existing generationJob, field will ignore if it is null
     *
     * @param id the id of the generationJobDTO to save.
     * @param generationJobDTO the generationJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated generationJobDTO,
     * or with status {@code 400 (Bad Request)} if the generationJobDTO is not valid,
     * or with status {@code 404 (Not Found)} if the generationJobDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the generationJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GenerationJobDTO> partialUpdateGenerationJob(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GenerationJobDTO generationJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update GenerationJob partially : {}, {}", id, generationJobDTO);
        if (generationJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, generationJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!generationJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GenerationJobDTO> result = generationJobService.partialUpdate(generationJobDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, generationJobDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /generation-jobs} : get all the Generation Jobs.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Generation Jobs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GenerationJobDTO>> getAllGenerationJobs(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of GenerationJobs");
        Page<GenerationJobDTO> page;
        if (eagerload) {
            page = generationJobService.findAllWithEagerRelationships(pageable);
        } else {
            page = generationJobService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /generation-jobs/:id} : get the "id" generationJob.
     *
     * @param id the id of the generationJobDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the generationJobDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenerationJobDTO> getGenerationJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to get GenerationJob : {}", id);
        Optional<GenerationJobDTO> generationJobDTO = generationJobService.findOne(id);
        return ResponseUtil.wrapOrNotFound(generationJobDTO);
    }

    /**
     * {@code DELETE  /generation-jobs/:id} : delete the "id" generationJob.
     *
     * @param id the id of the generationJobDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenerationJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete GenerationJob : {}", id);
        generationJobService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
