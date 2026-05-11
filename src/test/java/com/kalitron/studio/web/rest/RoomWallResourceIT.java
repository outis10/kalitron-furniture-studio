package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.RoomWallAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomWall;
import com.kalitron.studio.repository.RoomWallRepository;
import com.kalitron.studio.service.RoomWallService;
import com.kalitron.studio.service.dto.RoomWallDTO;
import com.kalitron.studio.service.mapper.RoomWallMapper;
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
 * Integration tests for the {@link RoomWallResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RoomWallResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_LENGTH_MM = 1;
    private static final Integer UPDATED_LENGTH_MM = 2;

    private static final Integer DEFAULT_HEIGHT_MM = 1;
    private static final Integer UPDATED_HEIGHT_MM = 2;

    private static final Integer DEFAULT_ANGLE_DEG = 1;
    private static final Integer UPDATED_ANGLE_DEG = 2;

    private static final Integer DEFAULT_POSITION_X = 1;
    private static final Integer UPDATED_POSITION_X = 2;

    private static final Integer DEFAULT_POSITION_Y = 1;
    private static final Integer UPDATED_POSITION_Y = 2;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/room-walls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RoomWallRepository roomWallRepository;

    @Mock
    private RoomWallRepository roomWallRepositoryMock;

    @Autowired
    private RoomWallMapper roomWallMapper;

    @Mock
    private RoomWallService roomWallServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoomWallMockMvc;

    private RoomWall roomWall;

    private RoomWall insertedRoomWall;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoomWall createEntity(EntityManager em) {
        RoomWall roomWall = new RoomWall()
            .name(DEFAULT_NAME)
            .lengthMm(DEFAULT_LENGTH_MM)
            .heightMm(DEFAULT_HEIGHT_MM)
            .angleDeg(DEFAULT_ANGLE_DEG)
            .positionX(DEFAULT_POSITION_X)
            .positionY(DEFAULT_POSITION_Y)
            .sortOrder(DEFAULT_SORT_ORDER);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        roomWall.setSession(designSession);
        return roomWall;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoomWall createUpdatedEntity(EntityManager em) {
        RoomWall updatedRoomWall = new RoomWall()
            .name(UPDATED_NAME)
            .lengthMm(UPDATED_LENGTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .angleDeg(UPDATED_ANGLE_DEG)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .sortOrder(UPDATED_SORT_ORDER);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedRoomWall.setSession(designSession);
        return updatedRoomWall;
    }

    @BeforeEach
    void initTest() {
        roomWall = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRoomWall != null) {
            roomWallRepository.delete(insertedRoomWall);
            insertedRoomWall = null;
        }
    }

    @Test
    @Transactional
    void createRoomWall() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);
        var returnedRoomWallDTO = om.readValue(
            restRoomWallMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomWallDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RoomWallDTO.class
        );

        // Validate the RoomWall in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRoomWall = roomWallMapper.toEntity(returnedRoomWallDTO);
        assertRoomWallUpdatableFieldsEquals(returnedRoomWall, getPersistedRoomWall(returnedRoomWall));

        insertedRoomWall = returnedRoomWall;
    }

    @Test
    @Transactional
    void createRoomWallWithExistingId() throws Exception {
        // Create the RoomWall with an existing ID
        roomWall.setId(1L);
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomWallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomWallDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        roomWall.setName(null);

        // Create the RoomWall, which fails.
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        restRoomWallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomWallDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLengthMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        roomWall.setLengthMm(null);

        // Create the RoomWall, which fails.
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        restRoomWallMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomWallDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoomWalls() throws Exception {
        // Initialize the database
        insertedRoomWall = roomWallRepository.saveAndFlush(roomWall);

        // Get all the roomWallList
        restRoomWallMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roomWall.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lengthMm").value(hasItem(DEFAULT_LENGTH_MM)))
            .andExpect(jsonPath("$.[*].heightMm").value(hasItem(DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].angleDeg").value(hasItem(DEFAULT_ANGLE_DEG)))
            .andExpect(jsonPath("$.[*].positionX").value(hasItem(DEFAULT_POSITION_X)))
            .andExpect(jsonPath("$.[*].positionY").value(hasItem(DEFAULT_POSITION_Y)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRoomWallsWithEagerRelationshipsIsEnabled() throws Exception {
        when(roomWallServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRoomWallMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(roomWallServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRoomWallsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(roomWallServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRoomWallMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(roomWallRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRoomWall() throws Exception {
        // Initialize the database
        insertedRoomWall = roomWallRepository.saveAndFlush(roomWall);

        // Get the roomWall
        restRoomWallMockMvc
            .perform(get(ENTITY_API_URL_ID, roomWall.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(roomWall.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.lengthMm").value(DEFAULT_LENGTH_MM))
            .andExpect(jsonPath("$.heightMm").value(DEFAULT_HEIGHT_MM))
            .andExpect(jsonPath("$.angleDeg").value(DEFAULT_ANGLE_DEG))
            .andExpect(jsonPath("$.positionX").value(DEFAULT_POSITION_X))
            .andExpect(jsonPath("$.positionY").value(DEFAULT_POSITION_Y))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingRoomWall() throws Exception {
        // Get the roomWall
        restRoomWallMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoomWall() throws Exception {
        // Initialize the database
        insertedRoomWall = roomWallRepository.saveAndFlush(roomWall);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the roomWall
        RoomWall updatedRoomWall = roomWallRepository.findById(roomWall.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRoomWall are not directly saved in db
        em.detach(updatedRoomWall);
        updatedRoomWall
            .name(UPDATED_NAME)
            .lengthMm(UPDATED_LENGTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .angleDeg(UPDATED_ANGLE_DEG)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .sortOrder(UPDATED_SORT_ORDER);
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(updatedRoomWall);

        restRoomWallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roomWallDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(roomWallDTO))
            )
            .andExpect(status().isOk());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRoomWallToMatchAllProperties(updatedRoomWall);
    }

    @Test
    @Transactional
    void putNonExistingRoomWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomWall.setId(longCount.incrementAndGet());

        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomWallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roomWallDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(roomWallDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoomWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomWall.setId(longCount.incrementAndGet());

        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomWallMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(roomWallDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoomWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomWall.setId(longCount.incrementAndGet());

        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomWallMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(roomWallDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRoomWallWithPatch() throws Exception {
        // Initialize the database
        insertedRoomWall = roomWallRepository.saveAndFlush(roomWall);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the roomWall using partial update
        RoomWall partialUpdatedRoomWall = new RoomWall();
        partialUpdatedRoomWall.setId(roomWall.getId());

        partialUpdatedRoomWall.heightMm(UPDATED_HEIGHT_MM).positionY(UPDATED_POSITION_Y);

        restRoomWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoomWall.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoomWall))
            )
            .andExpect(status().isOk());

        // Validate the RoomWall in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomWallUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRoomWall, roomWall), getPersistedRoomWall(roomWall));
    }

    @Test
    @Transactional
    void fullUpdateRoomWallWithPatch() throws Exception {
        // Initialize the database
        insertedRoomWall = roomWallRepository.saveAndFlush(roomWall);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the roomWall using partial update
        RoomWall partialUpdatedRoomWall = new RoomWall();
        partialUpdatedRoomWall.setId(roomWall.getId());

        partialUpdatedRoomWall
            .name(UPDATED_NAME)
            .lengthMm(UPDATED_LENGTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .angleDeg(UPDATED_ANGLE_DEG)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .sortOrder(UPDATED_SORT_ORDER);

        restRoomWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoomWall.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoomWall))
            )
            .andExpect(status().isOk());

        // Validate the RoomWall in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomWallUpdatableFieldsEquals(partialUpdatedRoomWall, getPersistedRoomWall(partialUpdatedRoomWall));
    }

    @Test
    @Transactional
    void patchNonExistingRoomWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomWall.setId(longCount.incrementAndGet());

        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, roomWallDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(roomWallDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoomWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomWall.setId(longCount.incrementAndGet());

        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomWallMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(roomWallDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoomWall() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        roomWall.setId(longCount.incrementAndGet());

        // Create the RoomWall
        RoomWallDTO roomWallDTO = roomWallMapper.toDto(roomWall);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomWallMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(roomWallDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoomWall in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRoomWall() throws Exception {
        // Initialize the database
        insertedRoomWall = roomWallRepository.saveAndFlush(roomWall);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the roomWall
        restRoomWallMockMvc
            .perform(delete(ENTITY_API_URL_ID, roomWall.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return roomWallRepository.count();
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

    protected RoomWall getPersistedRoomWall(RoomWall roomWall) {
        return roomWallRepository.findById(roomWall.getId()).orElseThrow();
    }

    protected void assertPersistedRoomWallToMatchAllProperties(RoomWall expectedRoomWall) {
        assertRoomWallAllPropertiesEquals(expectedRoomWall, getPersistedRoomWall(expectedRoomWall));
    }

    protected void assertPersistedRoomWallToMatchUpdatableProperties(RoomWall expectedRoomWall) {
        assertRoomWallAllUpdatablePropertiesEquals(expectedRoomWall, getPersistedRoomWall(expectedRoomWall));
    }
}
