package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.RoomObstacleAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomObstacle;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import com.kalitron.studio.repository.RoomObstacleRepository;
import com.kalitron.studio.service.RoomObstacleService;
import com.kalitron.studio.service.dto.RoomObstacleDTO;
import com.kalitron.studio.service.mapper.RoomObstacleMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RoomObstacleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RoomObstacleResourceIT {

    private static final RoomObstacleType DEFAULT_OBSTACLE_TYPE = RoomObstacleType.WINDOW;
    private static final RoomObstacleType UPDATED_OBSTACLE_TYPE = RoomObstacleType.DOOR;

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Integer DEFAULT_X_MM = 1;
    private static final Integer UPDATED_X_MM = 2;

    private static final Integer DEFAULT_Y_MM = 1;
    private static final Integer UPDATED_Y_MM = 2;

    private static final Integer DEFAULT_Z_MM = 1;
    private static final Integer UPDATED_Z_MM = 2;

    private static final Integer DEFAULT_WIDTH_MM = 1;
    private static final Integer UPDATED_WIDTH_MM = 2;

    private static final Integer DEFAULT_HEIGHT_MM = 1;
    private static final Integer UPDATED_HEIGHT_MM = 2;

    private static final Integer DEFAULT_DEPTH_MM = 1;
    private static final Integer UPDATED_DEPTH_MM = 2;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/room-obstacles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RoomObstacleRepository roomObstacleRepository;

    @Mock
    private RoomObstacleRepository roomObstacleRepositoryMock;

    @Autowired
    private RoomObstacleMapper roomObstacleMapper;

    @Mock
    private RoomObstacleService roomObstacleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoomObstacleMockMvc;

    private RoomObstacle roomObstacle;

    private RoomObstacle insertedRoomObstacle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoomObstacle createEntity(EntityManager em) {
        RoomObstacle roomObstacle = new RoomObstacle()
            .obstacleType(DEFAULT_OBSTACLE_TYPE)
            .label(DEFAULT_LABEL)
            .xMm(DEFAULT_X_MM)
            .yMm(DEFAULT_Y_MM)
            .zMm(DEFAULT_Z_MM)
            .widthMm(DEFAULT_WIDTH_MM)
            .heightMm(DEFAULT_HEIGHT_MM)
            .depthMm(DEFAULT_DEPTH_MM)
            .notes(DEFAULT_NOTES);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        roomObstacle.setSession(designSession);
        return roomObstacle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoomObstacle createUpdatedEntity(EntityManager em) {
        RoomObstacle updatedRoomObstacle = new RoomObstacle()
            .obstacleType(UPDATED_OBSTACLE_TYPE)
            .label(UPDATED_LABEL)
            .xMm(UPDATED_X_MM)
            .yMm(UPDATED_Y_MM)
            .zMm(UPDATED_Z_MM)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .notes(UPDATED_NOTES);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedRoomObstacle.setSession(designSession);
        return updatedRoomObstacle;
    }

    @BeforeEach
    void initTest() {
        roomObstacle = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRoomObstacle != null) {
            roomObstacleRepository.delete(insertedRoomObstacle);
            insertedRoomObstacle = null;
        }
    }

    @Test
    @Transactional
    void createRoomObstacle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);
        var returnedRoomObstacleDTO = om.readValue(
            restRoomObstacleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomObstacleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RoomObstacleDTO.class
        );

        // Validate the RoomObstacle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRoomObstacle = roomObstacleMapper.toEntity(returnedRoomObstacleDTO);
        assertRoomObstacleUpdatableFieldsEquals(returnedRoomObstacle, getPersistedRoomObstacle(returnedRoomObstacle));

        insertedRoomObstacle = returnedRoomObstacle;
    }

    @Test
    @Transactional
    void createRoomObstacleWithExistingId() throws Exception {
        // Create the RoomObstacle with an existing ID
        roomObstacle.setId(1L);
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomObstacleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomObstacleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkObstacleTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        roomObstacle.setObstacleType(null);

        // Create the RoomObstacle, which fails.
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        restRoomObstacleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomObstacleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkxMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        roomObstacle.setxMm(null);

        // Create the RoomObstacle, which fails.
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        restRoomObstacleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomObstacleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoomObstacles() throws Exception {
        // Initialize the database
        insertedRoomObstacle = roomObstacleRepository.saveAndFlush(roomObstacle);

        // Get all the roomObstacleList
        restRoomObstacleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roomObstacle.getId().intValue())))
            .andExpect(jsonPath("$.[*].obstacleType").value(hasItem(DEFAULT_OBSTACLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].xMm").value(hasItem(DEFAULT_X_MM)))
            .andExpect(jsonPath("$.[*].yMm").value(hasItem(DEFAULT_Y_MM)))
            .andExpect(jsonPath("$.[*].zMm").value(hasItem(DEFAULT_Z_MM)))
            .andExpect(jsonPath("$.[*].widthMm").value(hasItem(DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].heightMm").value(hasItem(DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].depthMm").value(hasItem(DEFAULT_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRoomObstaclesWithEagerRelationshipsIsEnabled() throws Exception {
        when(roomObstacleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRoomObstacleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(roomObstacleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRoomObstaclesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(roomObstacleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRoomObstacleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(roomObstacleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRoomObstacle() throws Exception {
        // Initialize the database
        insertedRoomObstacle = roomObstacleRepository.saveAndFlush(roomObstacle);

        // Get the roomObstacle
        restRoomObstacleMockMvc
            .perform(get(ENTITY_API_URL_ID, roomObstacle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(roomObstacle.getId().intValue()))
            .andExpect(jsonPath("$.obstacleType").value(DEFAULT_OBSTACLE_TYPE.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.xMm").value(DEFAULT_X_MM))
            .andExpect(jsonPath("$.yMm").value(DEFAULT_Y_MM))
            .andExpect(jsonPath("$.zMm").value(DEFAULT_Z_MM))
            .andExpect(jsonPath("$.widthMm").value(DEFAULT_WIDTH_MM))
            .andExpect(jsonPath("$.heightMm").value(DEFAULT_HEIGHT_MM))
            .andExpect(jsonPath("$.depthMm").value(DEFAULT_DEPTH_MM))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingRoomObstacle() throws Exception {
        // Get the roomObstacle
        restRoomObstacleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoomObstacle() throws Exception {
        // Initialize the database
        insertedRoomObstacle = roomObstacleRepository.saveAndFlush(roomObstacle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the roomObstacle
        RoomObstacle updatedRoomObstacle = roomObstacleRepository.findById(roomObstacle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRoomObstacle are not directly saved in db
        em.detach(updatedRoomObstacle);
        updatedRoomObstacle
            .obstacleType(UPDATED_OBSTACLE_TYPE)
            .label(UPDATED_LABEL)
            .xMm(UPDATED_X_MM)
            .yMm(UPDATED_Y_MM)
            .zMm(UPDATED_Z_MM)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .notes(UPDATED_NOTES);
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(updatedRoomObstacle);

        restRoomObstacleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roomObstacleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(roomObstacleDTO))
            )
            .andExpect(status().isOk());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRoomObstacleToMatchAllProperties(updatedRoomObstacle);
    }

    @Test
    @Transactional
    void putNonExistingRoomObstacle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomObstacle.setId(longCount.incrementAndGet());

        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomObstacleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roomObstacleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(roomObstacleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoomObstacle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomObstacle.setId(longCount.incrementAndGet());

        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomObstacleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(roomObstacleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoomObstacle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomObstacle.setId(longCount.incrementAndGet());

        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomObstacleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomObstacleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRoomObstacleWithPatch() throws Exception {
        // Initialize the database
        insertedRoomObstacle = roomObstacleRepository.saveAndFlush(roomObstacle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the roomObstacle using partial update
        RoomObstacle partialUpdatedRoomObstacle = new RoomObstacle();
        partialUpdatedRoomObstacle.setId(roomObstacle.getId());

        partialUpdatedRoomObstacle
            .obstacleType(UPDATED_OBSTACLE_TYPE)
            .label(UPDATED_LABEL)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM);

        restRoomObstacleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoomObstacle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoomObstacle))
            )
            .andExpect(status().isOk());

        // Validate the RoomObstacle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomObstacleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRoomObstacle, roomObstacle),
            getPersistedRoomObstacle(roomObstacle)
        );
    }

    @Test
    @Transactional
    void fullUpdateRoomObstacleWithPatch() throws Exception {
        // Initialize the database
        insertedRoomObstacle = roomObstacleRepository.saveAndFlush(roomObstacle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the roomObstacle using partial update
        RoomObstacle partialUpdatedRoomObstacle = new RoomObstacle();
        partialUpdatedRoomObstacle.setId(roomObstacle.getId());

        partialUpdatedRoomObstacle
            .obstacleType(UPDATED_OBSTACLE_TYPE)
            .label(UPDATED_LABEL)
            .xMm(UPDATED_X_MM)
            .yMm(UPDATED_Y_MM)
            .zMm(UPDATED_Z_MM)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .notes(UPDATED_NOTES);

        restRoomObstacleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoomObstacle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoomObstacle))
            )
            .andExpect(status().isOk());

        // Validate the RoomObstacle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomObstacleUpdatableFieldsEquals(partialUpdatedRoomObstacle, getPersistedRoomObstacle(partialUpdatedRoomObstacle));
    }

    @Test
    @Transactional
    void patchNonExistingRoomObstacle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomObstacle.setId(longCount.incrementAndGet());

        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomObstacleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, roomObstacleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(roomObstacleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoomObstacle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomObstacle.setId(longCount.incrementAndGet());

        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomObstacleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(roomObstacleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoomObstacle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomObstacle.setId(longCount.incrementAndGet());

        // Create the RoomObstacle
        RoomObstacleDTO roomObstacleDTO = roomObstacleMapper.toDto(roomObstacle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomObstacleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(roomObstacleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoomObstacle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRoomObstacle() throws Exception {
        // Initialize the database
        insertedRoomObstacle = roomObstacleRepository.saveAndFlush(roomObstacle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the roomObstacle
        restRoomObstacleMockMvc
            .perform(delete(ENTITY_API_URL_ID, roomObstacle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return roomObstacleRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected RoomObstacle getPersistedRoomObstacle(RoomObstacle roomObstacle) {
        return roomObstacleRepository.findById(roomObstacle.getId()).orElseThrow();
    }

    protected void assertPersistedRoomObstacleToMatchAllProperties(RoomObstacle expectedRoomObstacle) {
        assertRoomObstacleAllPropertiesEquals(expectedRoomObstacle, getPersistedRoomObstacle(expectedRoomObstacle));
    }

    protected void assertPersistedRoomObstacleToMatchUpdatableProperties(RoomObstacle expectedRoomObstacle) {
        assertRoomObstacleAllUpdatablePropertiesEquals(expectedRoomObstacle, getPersistedRoomObstacle(expectedRoomObstacle));
    }
}
