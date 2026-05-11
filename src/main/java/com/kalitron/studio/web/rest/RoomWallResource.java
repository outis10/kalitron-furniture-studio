package com.kalitron.studio.web.rest;

import com.kalitron.studio.repository.RoomWallRepository;
import com.kalitron.studio.service.RoomWallService;
import com.kalitron.studio.service.dto.RoomWallDTO;
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
 * REST controller for managing {@link com.kalitron.studio.domain.RoomWall}.
 */
@RestController
@RequestMapping("/api/room-walls")
public class RoomWallResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoomWallResource.class);

    private static final String ENTITY_NAME = "roomWall";

    @Value("${jhipster.clientApp.name:kalitronfurniturestudio}")
    private String applicationName;

    private final RoomWallService roomWallService;

    private final RoomWallRepository roomWallRepository;

    public RoomWallResource(RoomWallService roomWallService, RoomWallRepository roomWallRepository) {
        this.roomWallService = roomWallService;
        this.roomWallRepository = roomWallRepository;
    }

    /**
     * {@code POST  /room-walls} : Create a new roomWall.
     *
     * @param roomWallDTO the roomWallDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roomWallDTO, or with status {@code 400 (Bad Request)} if the roomWall has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RoomWallDTO> createRoomWall(@Valid @RequestBody RoomWallDTO roomWallDTO) throws URISyntaxException {
        LOG.debug("REST request to save RoomWall : {}", roomWallDTO);
        if (roomWallDTO.getId() != null) {
            throw new BadRequestAlertException("A new roomWall cannot already have an ID", ENTITY_NAME, "idexists");
        }
        roomWallDTO = roomWallService.save(roomWallDTO);
        return ResponseEntity.created(new URI("/api/room-walls/" + roomWallDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, roomWallDTO.getId().toString()))
            .body(roomWallDTO);
    }

    /**
     * {@code PUT  /room-walls/:id} : Updates an existing roomWall.
     *
     * @param id the id of the roomWallDTO to save.
     * @param roomWallDTO the roomWallDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomWallDTO,
     * or with status {@code 400 (Bad Request)} if the roomWallDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roomWallDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomWallDTO> updateRoomWall(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoomWallDTO roomWallDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RoomWall : {}, {}", id, roomWallDTO);
        if (roomWallDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomWallDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomWallRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        roomWallDTO = roomWallService.update(roomWallDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomWallDTO.getId().toString()))
            .body(roomWallDTO);
    }

    /**
     * {@code PATCH  /room-walls/:id} : Partial updates given fields of an existing roomWall, field will ignore if it is null
     *
     * @param id the id of the roomWallDTO to save.
     * @param roomWallDTO the roomWallDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomWallDTO,
     * or with status {@code 400 (Bad Request)} if the roomWallDTO is not valid,
     * or with status {@code 404 (Not Found)} if the roomWallDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the roomWallDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoomWallDTO> partialUpdateRoomWall(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoomWallDTO roomWallDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RoomWall partially : {}, {}", id, roomWallDTO);
        if (roomWallDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomWallDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomWallRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoomWallDTO> result = roomWallService.partialUpdate(roomWallDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roomWallDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /room-walls} : get all the Room Walls.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Room Walls in body.
     */
    @GetMapping("")
    public List<RoomWallDTO> getAllRoomWalls(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all RoomWalls");
        return roomWallService.findAll();
    }

    /**
     * {@code GET  /room-walls/:id} : get the "id" roomWall.
     *
     * @param id the id of the roomWallDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roomWallDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomWallDTO> getRoomWall(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RoomWall : {}", id);
        Optional<RoomWallDTO> roomWallDTO = roomWallService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roomWallDTO);
    }

    /**
     * {@code DELETE  /room-walls/:id} : delete the "id" roomWall.
     *
     * @param id the id of the roomWallDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomWall(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RoomWall : {}", id);
        roomWallService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
