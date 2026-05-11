package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.CabinetTemplateRepository;
import com.kalitron.studio.service.CabinetTemplateQueryService;
import com.kalitron.studio.service.CabinetTemplateService;
import com.kalitron.studio.service.criteria.CabinetTemplateCriteria;
import com.kalitron.studio.service.dto.CabinetTemplateDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.CabinetTemplate}.
 */
@RestController
@RequestMapping("/api/cabinet-templates")
public class CabinetTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetTemplateResource.class);

    private static final String ENTITY_NAME = "cabinetTemplate";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final CabinetTemplateService cabinetTemplateService;

    private final CabinetTemplateRepository cabinetTemplateRepository;

    private final CabinetTemplateQueryService cabinetTemplateQueryService;

    public CabinetTemplateResource(
        CabinetTemplateService cabinetTemplateService,
        CabinetTemplateRepository cabinetTemplateRepository,
        CabinetTemplateQueryService cabinetTemplateQueryService
    ) {
        this.cabinetTemplateService = cabinetTemplateService;
        this.cabinetTemplateRepository = cabinetTemplateRepository;
        this.cabinetTemplateQueryService = cabinetTemplateQueryService;
    }

    /**
     * {@code POST  /cabinet-templates} : Create a new cabinetTemplate.
     *
     * @param cabinetTemplateDTO the cabinetTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cabinetTemplateDTO, or with status {@code 400 (Bad Request)} if the cabinetTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CabinetTemplateDTO> createCabinetTemplate(@Valid @RequestBody CabinetTemplateDTO cabinetTemplateDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CabinetTemplate : {}", cabinetTemplateDTO);
        if (cabinetTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new cabinetTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cabinetTemplateDTO = cabinetTemplateService.save(cabinetTemplateDTO);
        return ResponseEntity.created(new URI("/api/cabinet-templates/" + cabinetTemplateDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cabinetTemplateDTO.getId().toString()))
            .body(cabinetTemplateDTO);
    }

    /**
     * {@code PUT  /cabinet-templates/:id} : Updates an existing cabinetTemplate.
     *
     * @param id the id of the cabinetTemplateDTO to save.
     * @param cabinetTemplateDTO the cabinetTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cabinetTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the cabinetTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cabinetTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CabinetTemplateDTO> updateCabinetTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CabinetTemplateDTO cabinetTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CabinetTemplate : {}, {}", id, cabinetTemplateDTO);
        if (cabinetTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cabinetTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cabinetTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cabinetTemplateDTO = cabinetTemplateService.update(cabinetTemplateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cabinetTemplateDTO.getId().toString()))
            .body(cabinetTemplateDTO);
    }

    /**
     * {@code PATCH  /cabinet-templates/:id} : Partial updates given fields of an existing cabinetTemplate, field will ignore if it is null
     *
     * @param id the id of the cabinetTemplateDTO to save.
     * @param cabinetTemplateDTO the cabinetTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cabinetTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the cabinetTemplateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cabinetTemplateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cabinetTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CabinetTemplateDTO> partialUpdateCabinetTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CabinetTemplateDTO cabinetTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CabinetTemplate partially : {}, {}", id, cabinetTemplateDTO);
        if (cabinetTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cabinetTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cabinetTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CabinetTemplateDTO> result = cabinetTemplateService.partialUpdate(cabinetTemplateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cabinetTemplateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cabinet-templates} : get all the Cabinet Templates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cabinet Templates in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CabinetTemplateDTO>> getAllCabinetTemplates(CabinetTemplateCriteria criteria) {
        LOG.debug("REST request to get CabinetTemplates by criteria: {}", criteria);

        List<CabinetTemplateDTO> entityList = cabinetTemplateQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /cabinet-templates/count} : count all the cabinetTemplates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCabinetTemplates(CabinetTemplateCriteria criteria) {
        LOG.debug("REST request to count CabinetTemplates by criteria: {}", criteria);
        return ResponseEntity.ok().body(cabinetTemplateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cabinet-templates/:id} : get the "id" cabinetTemplate.
     *
     * @param id the id of the cabinetTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cabinetTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CabinetTemplateDTO> getCabinetTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CabinetTemplate : {}", id);
        Optional<CabinetTemplateDTO> cabinetTemplateDTO = cabinetTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cabinetTemplateDTO);
    }

    /**
     * {@code DELETE  /cabinet-templates/:id} : delete the "id" cabinetTemplate.
     *
     * @param id the id of the cabinetTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCabinetTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CabinetTemplate : {}", id);
        cabinetTemplateService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
