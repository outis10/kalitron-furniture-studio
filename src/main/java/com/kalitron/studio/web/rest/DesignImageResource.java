package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.DesignImageRepository;
import com.kalitron.studio.service.DesignImageService;
import com.kalitron.studio.service.dto.DesignImageDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.DesignImage}.
 */
@RestController
@RequestMapping("/api/design-images")
public class DesignImageResource {

    private static final Logger LOG = LoggerFactory.getLogger(DesignImageResource.class);

    private static final String ENTITY_NAME = "designImage";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final DesignImageService designImageService;

    private final DesignImageRepository designImageRepository;

    public DesignImageResource(DesignImageService designImageService, DesignImageRepository designImageRepository) {
        this.designImageService = designImageService;
        this.designImageRepository = designImageRepository;
    }

    /**
     * {@code POST  /design-images} : Create a new designImage.
     *
     * @param designImageDTO the designImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new designImageDTO, or with status {@code 400 (Bad Request)} if the designImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DesignImageDTO> createDesignImage(@Valid @RequestBody DesignImageDTO designImageDTO) throws URISyntaxException {
        LOG.debug("REST request to save DesignImage : {}", designImageDTO);
        if (designImageDTO.getId() != null) {
            throw new BadRequestAlertException("A new designImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        designImageDTO = designImageService.save(designImageDTO);
        return ResponseEntity.created(new URI("/api/design-images/" + designImageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, designImageDTO.getId().toString()))
            .body(designImageDTO);
    }

    /**
     * {@code PUT  /design-images/:id} : Updates an existing designImage.
     *
     * @param id the id of the designImageDTO to save.
     * @param designImageDTO the designImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designImageDTO,
     * or with status {@code 400 (Bad Request)} if the designImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the designImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DesignImageDTO> updateDesignImage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DesignImageDTO designImageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DesignImage : {}, {}", id, designImageDTO);
        if (designImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!designImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        designImageDTO = designImageService.update(designImageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, designImageDTO.getId().toString()))
            .body(designImageDTO);
    }

    /**
     * {@code PATCH  /design-images/:id} : Partial updates given fields of an existing designImage, field will ignore if it is null
     *
     * @param id the id of the designImageDTO to save.
     * @param designImageDTO the designImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designImageDTO,
     * or with status {@code 400 (Bad Request)} if the designImageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the designImageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the designImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DesignImageDTO> partialUpdateDesignImage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DesignImageDTO designImageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DesignImage partially : {}, {}", id, designImageDTO);
        if (designImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!designImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DesignImageDTO> result = designImageService.partialUpdate(designImageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, designImageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /design-images} : get all the Design Images.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Design Images in body.
     */
    @GetMapping("")
    public List<DesignImageDTO> getAllDesignImages(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all DesignImages");
        return designImageService.findAll();
    }

    /**
     * {@code GET  /design-images/:id} : get the "id" designImage.
     *
     * @param id the id of the designImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the designImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DesignImageDTO> getDesignImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DesignImage : {}", id);
        Optional<DesignImageDTO> designImageDTO = designImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(designImageDTO);
    }

    /**
     * {@code DELETE  /design-images/:id} : delete the "id" designImage.
     *
     * @param id the id of the designImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DesignImage : {}", id);
        designImageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
