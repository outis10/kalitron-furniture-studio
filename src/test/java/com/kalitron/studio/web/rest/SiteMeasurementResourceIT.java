package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.SiteMeasurementAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SiteMeasurementStatus;
import com.kalitron.studio.repository.SiteMeasurementRepository;
import com.kalitron.studio.repository.UserRepository;
import com.kalitron.studio.service.SiteMeasurementService;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import com.kalitron.studio.service.mapper.SiteMeasurementMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
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
 * Integration tests for the {@link SiteMeasurementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SiteMeasurementResourceIT {

    private static final UUID DEFAULT_MEASUREMENT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_MEASUREMENT_UUID = UUID.randomUUID();

    private static final ProjectType DEFAULT_PROJECT_TYPE = ProjectType.KITCHEN;
    private static final ProjectType UPDATED_PROJECT_TYPE = ProjectType.CLOSET;

    private static final Integer DEFAULT_REVISION = 1;
    private static final Integer UPDATED_REVISION = 2;

    private static final SiteMeasurementStatus DEFAULT_STATUS = SiteMeasurementStatus.DRAFT;
    private static final SiteMeasurementStatus UPDATED_STATUS = SiteMeasurementStatus.CONFIRMED;

    private static final Integer DEFAULT_SCHEMA_VERSION = 1;
    private static final Integer UPDATED_SCHEMA_VERSION = 2;

    private static final String DEFAULT_CATALOG_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_CATALOG_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_PAYLOAD_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD_SHA_256 = "BBBBBBBBBB";

    private static final Integer DEFAULT_FLOOR_OUT_OF_LEVEL_MM = 1;
    private static final Integer UPDATED_FLOOR_OUT_OF_LEVEL_MM = 2;

    private static final String DEFAULT_FLOOR_OUT_OF_LEVEL_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_FLOOR_OUT_OF_LEVEL_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_DEVICE_ID = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_APP_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_APP_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_LASER_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_LASER_MODEL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CAPTURED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CAPTURED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RECEIVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECEIVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/site-measurements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SiteMeasurementRepository siteMeasurementRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SiteMeasurementRepository siteMeasurementRepositoryMock;

    @Autowired
    private SiteMeasurementMapper siteMeasurementMapper;

    @Mock
    private SiteMeasurementService siteMeasurementServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSiteMeasurementMockMvc;

    private SiteMeasurement siteMeasurement;

    private SiteMeasurement insertedSiteMeasurement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SiteMeasurement createEntity(EntityManager em) {
        SiteMeasurement siteMeasurement = new SiteMeasurement()
            .measurementUuid(DEFAULT_MEASUREMENT_UUID)
            .projectType(DEFAULT_PROJECT_TYPE)
            .revision(DEFAULT_REVISION)
            .status(DEFAULT_STATUS)
            .schemaVersion(DEFAULT_SCHEMA_VERSION)
            .catalogVersion(DEFAULT_CATALOG_VERSION)
            .payload(DEFAULT_PAYLOAD)
            .payloadSha256(DEFAULT_PAYLOAD_SHA_256)
            .floorOutOfLevelMm(DEFAULT_FLOOR_OUT_OF_LEVEL_MM)
            .floorOutOfLevelNote(DEFAULT_FLOOR_OUT_OF_LEVEL_NOTE)
            .deviceId(DEFAULT_DEVICE_ID)
            .appVersion(DEFAULT_APP_VERSION)
            .laserModel(DEFAULT_LASER_MODEL)
            .capturedAt(DEFAULT_CAPTURED_AT)
            .receivedAt(DEFAULT_RECEIVED_AT)
            .confirmedAt(DEFAULT_CONFIRMED_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        siteMeasurement.setSession(designSession);
        return siteMeasurement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SiteMeasurement createUpdatedEntity(EntityManager em) {
        SiteMeasurement updatedSiteMeasurement = new SiteMeasurement()
            .measurementUuid(UPDATED_MEASUREMENT_UUID)
            .projectType(UPDATED_PROJECT_TYPE)
            .revision(UPDATED_REVISION)
            .status(UPDATED_STATUS)
            .schemaVersion(UPDATED_SCHEMA_VERSION)
            .catalogVersion(UPDATED_CATALOG_VERSION)
            .payload(UPDATED_PAYLOAD)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .floorOutOfLevelMm(UPDATED_FLOOR_OUT_OF_LEVEL_MM)
            .floorOutOfLevelNote(UPDATED_FLOOR_OUT_OF_LEVEL_NOTE)
            .deviceId(UPDATED_DEVICE_ID)
            .appVersion(UPDATED_APP_VERSION)
            .laserModel(UPDATED_LASER_MODEL)
            .capturedAt(UPDATED_CAPTURED_AT)
            .receivedAt(UPDATED_RECEIVED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedSiteMeasurement.setSession(designSession);
        return updatedSiteMeasurement;
    }

    @BeforeEach
    void initTest() {
        siteMeasurement = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSiteMeasurement != null) {
            siteMeasurementRepository.delete(insertedSiteMeasurement);
            insertedSiteMeasurement = null;
        }
    }

    @Test
    @Transactional
    void createSiteMeasurement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);
        var returnedSiteMeasurementDTO = om.readValue(
            restSiteMeasurementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SiteMeasurementDTO.class
        );

        // Validate the SiteMeasurement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSiteMeasurement = siteMeasurementMapper.toEntity(returnedSiteMeasurementDTO);
        assertSiteMeasurementUpdatableFieldsEquals(returnedSiteMeasurement, getPersistedSiteMeasurement(returnedSiteMeasurement));

        insertedSiteMeasurement = returnedSiteMeasurement;
    }

    @Test
    @Transactional
    void createSiteMeasurementWithExistingId() throws Exception {
        // Create the SiteMeasurement with an existing ID
        siteMeasurement.setId(1L);
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMeasurementUuidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setMeasurementUuid(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProjectTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setProjectType(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRevisionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setRevision(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setStatus(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSchemaVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setSchemaVersion(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCatalogVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setCatalogVersion(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPayloadSha256IsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setPayloadSha256(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceivedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteMeasurement.setReceivedAt(null);

        // Create the SiteMeasurement, which fails.
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        restSiteMeasurementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSiteMeasurements() throws Exception {
        // Initialize the database
        insertedSiteMeasurement = siteMeasurementRepository.saveAndFlush(siteMeasurement);

        // Get all the siteMeasurementList
        restSiteMeasurementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(siteMeasurement.getId().intValue())))
            .andExpect(jsonPath("$.[*].measurementUuid").value(hasItem(DEFAULT_MEASUREMENT_UUID.toString())))
            .andExpect(jsonPath("$.[*].projectType").value(hasItem(DEFAULT_PROJECT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].revision").value(hasItem(DEFAULT_REVISION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].schemaVersion").value(hasItem(DEFAULT_SCHEMA_VERSION)))
            .andExpect(jsonPath("$.[*].catalogVersion").value(hasItem(DEFAULT_CATALOG_VERSION)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].payloadSha256").value(hasItem(DEFAULT_PAYLOAD_SHA_256)))
            .andExpect(jsonPath("$.[*].floorOutOfLevelMm").value(hasItem(DEFAULT_FLOOR_OUT_OF_LEVEL_MM)))
            .andExpect(jsonPath("$.[*].floorOutOfLevelNote").value(hasItem(DEFAULT_FLOOR_OUT_OF_LEVEL_NOTE)))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID)))
            .andExpect(jsonPath("$.[*].appVersion").value(hasItem(DEFAULT_APP_VERSION)))
            .andExpect(jsonPath("$.[*].laserModel").value(hasItem(DEFAULT_LASER_MODEL)))
            .andExpect(jsonPath("$.[*].capturedAt").value(hasItem(DEFAULT_CAPTURED_AT.toString())))
            .andExpect(jsonPath("$.[*].receivedAt").value(hasItem(DEFAULT_RECEIVED_AT.toString())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSiteMeasurementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(siteMeasurementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSiteMeasurementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(siteMeasurementServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSiteMeasurementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(siteMeasurementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSiteMeasurementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(siteMeasurementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSiteMeasurement() throws Exception {
        // Initialize the database
        insertedSiteMeasurement = siteMeasurementRepository.saveAndFlush(siteMeasurement);

        // Get the siteMeasurement
        restSiteMeasurementMockMvc
            .perform(get(ENTITY_API_URL_ID, siteMeasurement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(siteMeasurement.getId().intValue()))
            .andExpect(jsonPath("$.measurementUuid").value(DEFAULT_MEASUREMENT_UUID.toString()))
            .andExpect(jsonPath("$.projectType").value(DEFAULT_PROJECT_TYPE.toString()))
            .andExpect(jsonPath("$.revision").value(DEFAULT_REVISION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.schemaVersion").value(DEFAULT_SCHEMA_VERSION))
            .andExpect(jsonPath("$.catalogVersion").value(DEFAULT_CATALOG_VERSION))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.payloadSha256").value(DEFAULT_PAYLOAD_SHA_256))
            .andExpect(jsonPath("$.floorOutOfLevelMm").value(DEFAULT_FLOOR_OUT_OF_LEVEL_MM))
            .andExpect(jsonPath("$.floorOutOfLevelNote").value(DEFAULT_FLOOR_OUT_OF_LEVEL_NOTE))
            .andExpect(jsonPath("$.deviceId").value(DEFAULT_DEVICE_ID))
            .andExpect(jsonPath("$.appVersion").value(DEFAULT_APP_VERSION))
            .andExpect(jsonPath("$.laserModel").value(DEFAULT_LASER_MODEL))
            .andExpect(jsonPath("$.capturedAt").value(DEFAULT_CAPTURED_AT.toString()))
            .andExpect(jsonPath("$.receivedAt").value(DEFAULT_RECEIVED_AT.toString()))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSiteMeasurement() throws Exception {
        // Get the siteMeasurement
        restSiteMeasurementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSiteMeasurement() throws Exception {
        // Initialize the database
        insertedSiteMeasurement = siteMeasurementRepository.saveAndFlush(siteMeasurement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the siteMeasurement
        SiteMeasurement updatedSiteMeasurement = siteMeasurementRepository.findById(siteMeasurement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSiteMeasurement are not directly saved in db
        em.detach(updatedSiteMeasurement);
        updatedSiteMeasurement
            .measurementUuid(UPDATED_MEASUREMENT_UUID)
            .projectType(UPDATED_PROJECT_TYPE)
            .revision(UPDATED_REVISION)
            .status(UPDATED_STATUS)
            .schemaVersion(UPDATED_SCHEMA_VERSION)
            .catalogVersion(UPDATED_CATALOG_VERSION)
            .payload(UPDATED_PAYLOAD)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .floorOutOfLevelMm(UPDATED_FLOOR_OUT_OF_LEVEL_MM)
            .floorOutOfLevelNote(UPDATED_FLOOR_OUT_OF_LEVEL_NOTE)
            .deviceId(UPDATED_DEVICE_ID)
            .appVersion(UPDATED_APP_VERSION)
            .laserModel(UPDATED_LASER_MODEL)
            .capturedAt(UPDATED_CAPTURED_AT)
            .receivedAt(UPDATED_RECEIVED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT);
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(updatedSiteMeasurement);

        restSiteMeasurementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, siteMeasurementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(siteMeasurementDTO))
            )
            .andExpect(status().isOk());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSiteMeasurementToMatchAllProperties(updatedSiteMeasurement);
    }

    @Test
    @Transactional
    void putNonExistingSiteMeasurement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteMeasurement.setId(longCount.incrementAndGet());

        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSiteMeasurementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, siteMeasurementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(siteMeasurementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSiteMeasurement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteMeasurement.setId(longCount.incrementAndGet());

        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteMeasurementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(siteMeasurementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSiteMeasurement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteMeasurement.setId(longCount.incrementAndGet());

        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteMeasurementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSiteMeasurementWithPatch() throws Exception {
        // Initialize the database
        insertedSiteMeasurement = siteMeasurementRepository.saveAndFlush(siteMeasurement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the siteMeasurement using partial update
        SiteMeasurement partialUpdatedSiteMeasurement = new SiteMeasurement();
        partialUpdatedSiteMeasurement.setId(siteMeasurement.getId());

        partialUpdatedSiteMeasurement
            .projectType(UPDATED_PROJECT_TYPE)
            .catalogVersion(UPDATED_CATALOG_VERSION)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT)
            .receivedAt(UPDATED_RECEIVED_AT);

        restSiteMeasurementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSiteMeasurement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSiteMeasurement))
            )
            .andExpect(status().isOk());

        // Validate the SiteMeasurement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSiteMeasurementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSiteMeasurement, siteMeasurement),
            getPersistedSiteMeasurement(siteMeasurement)
        );
    }

    @Test
    @Transactional
    void fullUpdateSiteMeasurementWithPatch() throws Exception {
        // Initialize the database
        insertedSiteMeasurement = siteMeasurementRepository.saveAndFlush(siteMeasurement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the siteMeasurement using partial update
        SiteMeasurement partialUpdatedSiteMeasurement = new SiteMeasurement();
        partialUpdatedSiteMeasurement.setId(siteMeasurement.getId());

        partialUpdatedSiteMeasurement
            .measurementUuid(UPDATED_MEASUREMENT_UUID)
            .projectType(UPDATED_PROJECT_TYPE)
            .revision(UPDATED_REVISION)
            .status(UPDATED_STATUS)
            .schemaVersion(UPDATED_SCHEMA_VERSION)
            .catalogVersion(UPDATED_CATALOG_VERSION)
            .payload(UPDATED_PAYLOAD)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .floorOutOfLevelMm(UPDATED_FLOOR_OUT_OF_LEVEL_MM)
            .floorOutOfLevelNote(UPDATED_FLOOR_OUT_OF_LEVEL_NOTE)
            .deviceId(UPDATED_DEVICE_ID)
            .appVersion(UPDATED_APP_VERSION)
            .laserModel(UPDATED_LASER_MODEL)
            .capturedAt(UPDATED_CAPTURED_AT)
            .receivedAt(UPDATED_RECEIVED_AT)
            .confirmedAt(UPDATED_CONFIRMED_AT);

        restSiteMeasurementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSiteMeasurement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSiteMeasurement))
            )
            .andExpect(status().isOk());

        // Validate the SiteMeasurement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSiteMeasurementUpdatableFieldsEquals(
            partialUpdatedSiteMeasurement,
            getPersistedSiteMeasurement(partialUpdatedSiteMeasurement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSiteMeasurement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteMeasurement.setId(longCount.incrementAndGet());

        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSiteMeasurementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, siteMeasurementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(siteMeasurementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSiteMeasurement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteMeasurement.setId(longCount.incrementAndGet());

        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteMeasurementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(siteMeasurementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSiteMeasurement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteMeasurement.setId(longCount.incrementAndGet());

        // Create the SiteMeasurement
        SiteMeasurementDTO siteMeasurementDTO = siteMeasurementMapper.toDto(siteMeasurement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteMeasurementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(siteMeasurementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SiteMeasurement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSiteMeasurement() throws Exception {
        // Initialize the database
        insertedSiteMeasurement = siteMeasurementRepository.saveAndFlush(siteMeasurement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the siteMeasurement
        restSiteMeasurementMockMvc
            .perform(delete(ENTITY_API_URL_ID, siteMeasurement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return siteMeasurementRepository.count();
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

    protected SiteMeasurement getPersistedSiteMeasurement(SiteMeasurement siteMeasurement) {
        return siteMeasurementRepository.findById(siteMeasurement.getId()).orElseThrow();
    }

    protected void assertPersistedSiteMeasurementToMatchAllProperties(SiteMeasurement expectedSiteMeasurement) {
        assertSiteMeasurementAllPropertiesEquals(expectedSiteMeasurement, getPersistedSiteMeasurement(expectedSiteMeasurement));
    }

    protected void assertPersistedSiteMeasurementToMatchUpdatableProperties(SiteMeasurement expectedSiteMeasurement) {
        assertSiteMeasurementAllUpdatablePropertiesEquals(expectedSiteMeasurement, getPersistedSiteMeasurement(expectedSiteMeasurement));
    }
}
