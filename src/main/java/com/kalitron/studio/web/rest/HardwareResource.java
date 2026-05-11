package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.HardwareRepository;
import com.kalitron.studio.service.HardwareQueryService;
import com.kalitron.studio.service.HardwareService;
import com.kalitron.studio.service.criteria.HardwareCriteria;
import com.kalitron.studio.service.dto.HardwareDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.Hardware}.
 */
@RestController
@RequestMapping("/api/hardware")
public class HardwareResource {

    private static final Logger LOG = LoggerFactory.getLogger(HardwareResource.class);

    private static final String ENTITY_NAME = "hardware";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final HardwareService hardwareService;

    private final HardwareRepository hardwareRepository;

    private final HardwareQueryService hardwareQueryService;

    public HardwareResource(
        HardwareService hardwareService,
        HardwareRepository hardwareRepository,
        HardwareQueryService hardwareQueryService
    ) {
        this.hardwareService = hardwareService;
        this.hardwareRepository = hardwareRepository;
        this.hardwareQueryService = hardwareQueryService;
    }

    /**
     * {@code POST  /hardware} : Create a new hardware.
     *
     * @param hardwareDTO the hardwareDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hardwareDTO, or with status {@code 400 (Bad Request)} if the hardware has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HardwareDTO> createHardware(@Valid @RequestBody HardwareDTO hardwareDTO) throws URISyntaxException {
        LOG.debug("REST request to save Hardware : {}", hardwareDTO);
        if (hardwareDTO.getId() != null) {
            throw new BadRequestAlertException("A new hardware cannot already have an ID", ENTITY_NAME, "idexists");
        }
        hardwareDTO = hardwareService.save(hardwareDTO);
        return ResponseEntity.created(new URI("/api/hardware/" + hardwareDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, hardwareDTO.getId().toString()))
            .body(hardwareDTO);
    }

    /**
     * {@code PUT  /hardware/:id} : Updates an existing hardware.
     *
     * @param id the id of the hardwareDTO to save.
     * @param hardwareDTO the hardwareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hardwareDTO,
     * or with status {@code 400 (Bad Request)} if the hardwareDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hardwareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HardwareDTO> updateHardware(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HardwareDTO hardwareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Hardware : {}, {}", id, hardwareDTO);
        if (hardwareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hardwareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hardwareRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        hardwareDTO = hardwareService.update(hardwareDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hardwareDTO.getId().toString()))
            .body(hardwareDTO);
    }

    /**
     * {@code PATCH  /hardware/:id} : Partial updates given fields of an existing hardware, field will ignore if it is null
     *
     * @param id the id of the hardwareDTO to save.
     * @param hardwareDTO the hardwareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hardwareDTO,
     * or with status {@code 400 (Bad Request)} if the hardwareDTO is not valid,
     * or with status {@code 404 (Not Found)} if the hardwareDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the hardwareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HardwareDTO> partialUpdateHardware(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HardwareDTO hardwareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Hardware partially : {}, {}", id, hardwareDTO);
        if (hardwareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hardwareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hardwareRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HardwareDTO> result = hardwareService.partialUpdate(hardwareDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hardwareDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /hardware} : get all the Hardware.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Hardware in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HardwareDTO>> getAllHardwares(HardwareCriteria criteria) {
        LOG.debug("REST request to get Hardwares by criteria: {}", criteria);

        List<HardwareDTO> entityList = hardwareQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /hardware/count} : count all the hardwares.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countHardwares(HardwareCriteria criteria) {
        LOG.debug("REST request to count Hardwares by criteria: {}", criteria);
        return ResponseEntity.ok().body(hardwareQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /hardware/:id} : get the "id" hardware.
     *
     * @param id the id of the hardwareDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hardwareDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HardwareDTO> getHardware(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Hardware : {}", id);
        Optional<HardwareDTO> hardwareDTO = hardwareService.findOne(id);
        return ResponseUtil.wrapOrNotFound(hardwareDTO);
    }

    /**
     * {@code DELETE  /hardware/:id} : delete the "id" hardware.
     *
     * @param id the id of the hardwareDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHardware(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Hardware : {}", id);
        hardwareService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
