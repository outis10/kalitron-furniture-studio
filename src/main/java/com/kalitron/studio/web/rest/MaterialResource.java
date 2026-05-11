package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.MaterialRepository;
import com.kalitron.studio.service.MaterialQueryService;
import com.kalitron.studio.service.MaterialService;
import com.kalitron.studio.service.criteria.MaterialCriteria;
import com.kalitron.studio.service.dto.MaterialDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.Material}.
 */
@RestController
@RequestMapping("/api/materials")
public class MaterialResource {

    private static final Logger LOG = LoggerFactory.getLogger(MaterialResource.class);

    private static final String ENTITY_NAME = "material";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final MaterialService materialService;

    private final MaterialRepository materialRepository;

    private final MaterialQueryService materialQueryService;

    public MaterialResource(
        MaterialService materialService,
        MaterialRepository materialRepository,
        MaterialQueryService materialQueryService
    ) {
        this.materialService = materialService;
        this.materialRepository = materialRepository;
        this.materialQueryService = materialQueryService;
    }

    /**
     * {@code POST  /materials} : Create a new material.
     *
     * @param materialDTO the materialDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new materialDTO, or with status {@code 400 (Bad Request)} if the material has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MaterialDTO> createMaterial(@Valid @RequestBody MaterialDTO materialDTO) throws URISyntaxException {
        LOG.debug("REST request to save Material : {}", materialDTO);
        if (materialDTO.getId() != null) {
            throw new BadRequestAlertException("A new material cannot already have an ID", ENTITY_NAME, "idexists");
        }
        materialDTO = materialService.save(materialDTO);
        return ResponseEntity.created(new URI("/api/materials/" + materialDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, materialDTO.getId().toString()))
            .body(materialDTO);
    }

    /**
     * {@code PUT  /materials/:id} : Updates an existing material.
     *
     * @param id the id of the materialDTO to save.
     * @param materialDTO the materialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated materialDTO,
     * or with status {@code 400 (Bad Request)} if the materialDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the materialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MaterialDTO> updateMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MaterialDTO materialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Material : {}, {}", id, materialDTO);
        if (materialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, materialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!materialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        materialDTO = materialService.update(materialDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialDTO.getId().toString()))
            .body(materialDTO);
    }

    /**
     * {@code PATCH  /materials/:id} : Partial updates given fields of an existing material, field will ignore if it is null
     *
     * @param id the id of the materialDTO to save.
     * @param materialDTO the materialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated materialDTO,
     * or with status {@code 400 (Bad Request)} if the materialDTO is not valid,
     * or with status {@code 404 (Not Found)} if the materialDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the materialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MaterialDTO> partialUpdateMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MaterialDTO materialDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Material partially : {}, {}", id, materialDTO);
        if (materialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, materialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!materialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MaterialDTO> result = materialService.partialUpdate(materialDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /materials} : get all the Materials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Materials in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MaterialDTO>> getAllMaterials(MaterialCriteria criteria) {
        LOG.debug("REST request to get Materials by criteria: {}", criteria);

        List<MaterialDTO> entityList = materialQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /materials/count} : count all the materials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMaterials(MaterialCriteria criteria) {
        LOG.debug("REST request to count Materials by criteria: {}", criteria);
        return ResponseEntity.ok().body(materialQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /materials/:id} : get the "id" material.
     *
     * @param id the id of the materialDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the materialDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> getMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Material : {}", id);
        Optional<MaterialDTO> materialDTO = materialService.findOne(id);
        return ResponseUtil.wrapOrNotFound(materialDTO);
    }

    /**
     * {@code DELETE  /materials/:id} : delete the "id" material.
     *
     * @param id the id of the materialDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Material : {}", id);
        materialService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
