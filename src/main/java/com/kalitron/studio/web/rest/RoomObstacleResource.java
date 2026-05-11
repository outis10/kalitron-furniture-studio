package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.RoomObstacleRepository;
import com.kalitron.studio.service.RoomObstacleService;
import com.kalitron.studio.service.dto.RoomObstacleDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.RoomObstacle}.
 */
@RestController
@RequestMapping("/api/room-obstacles")
public class RoomObstacleResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoomObstacleResource.class);

    private static final String ENTITY_NAME = "roomObstacle";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final RoomObstacleService roomObstacleService;

    private final RoomObstacleRepository roomObstacleRepository;

    public RoomObstacleResource(RoomObstacleService roomObstacleService, RoomObstacleRepository roomObstacleRepository) {
        this.roomObstacleService = roomObstacleService;
        this.roomObstacleRepository = roomObstacleRepository;
    }

    /**
     * {@code POST  /room-obstacles} : Create a new roomObstacle.
     *
     * @param roomObstacleDTO the roomObstacleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roomObstacleDTO, or with status {@code 400 (Bad Request)} if the roomObstacle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RoomObstacleDTO> createRoomObstacle(@Valid @RequestBody RoomObstacleDTO roomObstacleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RoomObstacle : {}", roomObstacleDTO);
        if (roomObstacleDTO.getId() != null) {
            throw new BadRequestAlertException("A new roomObstacle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        roomObstacleDTO = roomObstacleService.save(roomObstacleDTO);
        return ResponseEntity.created(new URI("/api/room-obstacles/" + roomObstacleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, roomObstacleDTO.getId().toString()))
            .body(roomObstacleDTO);
    }

    /**
     * {@code PUT  /room-obstacles/:id} : Updates an existing roomObstacle.
     *
     * @param id the id of the roomObstacleDTO to save.
     * @param roomObstacleDTO the roomObstacleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomObstacleDTO,
     * or with status {@code 400 (Bad Request)} if the roomObstacleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roomObstacleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomObstacleDTO> updateRoomObstacle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoomObstacleDTO roomObstacleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RoomObstacle : {}, {}", id, roomObstacleDTO);
        if (roomObstacleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomObstacleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomObstacleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        roomObstacleDTO = roomObstacleService.update(roomObstacleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomObstacleDTO.getId().toString()))
            .body(roomObstacleDTO);
    }

    /**
     * {@code PATCH  /room-obstacles/:id} : Partial updates given fields of an existing roomObstacle, field will ignore if it is null
     *
     * @param id the id of the roomObstacleDTO to save.
     * @param roomObstacleDTO the roomObstacleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomObstacleDTO,
     * or with status {@code 400 (Bad Request)} if the roomObstacleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the roomObstacleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the roomObstacleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoomObstacleDTO> partialUpdateRoomObstacle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoomObstacleDTO roomObstacleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RoomObstacle partially : {}, {}", id, roomObstacleDTO);
        if (roomObstacleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomObstacleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomObstacleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoomObstacleDTO> result = roomObstacleService.partialUpdate(roomObstacleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomObstacleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /room-obstacles} : get all the Room Obstacles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Room Obstacles in body.
     */
    @GetMapping("")
    public List<RoomObstacleDTO> getAllRoomObstacles(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all RoomObstacles");
        return roomObstacleService.findAll();
    }

    /**
     * {@code GET  /room-obstacles/:id} : get the "id" roomObstacle.
     *
     * @param id the id of the roomObstacleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roomObstacleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomObstacleDTO> getRoomObstacle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RoomObstacle : {}", id);
        Optional<RoomObstacleDTO> roomObstacleDTO = roomObstacleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roomObstacleDTO);
    }

    /**
     * {@code DELETE  /room-obstacles/:id} : delete the "id" roomObstacle.
     *
     * @param id the id of the roomObstacleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomObstacle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RoomObstacle : {}", id);
        roomObstacleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
