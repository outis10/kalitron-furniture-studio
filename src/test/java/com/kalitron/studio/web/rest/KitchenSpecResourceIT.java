package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.KitchenSpecAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import com.kalitron.studio.repository.KitchenSpecRepository;
import com.kalitron.studio.service.KitchenSpecService;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
import com.kalitron.studio.service.mapper.KitchenSpecMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link KitchenSpecResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class KitchenSpecResourceIT {

    private static final KitchenLayout DEFAULT_LAYOUT = KitchenLayout.LINEAR;
    private static final KitchenLayout UPDATED_LAYOUT = KitchenLayout.L_SHAPE;

    private static final Integer DEFAULT_TOTAL_WIDTH_MM = 1;
    private static final Integer UPDATED_TOTAL_WIDTH_MM = 2;
    private static final Integer SMALLER_TOTAL_WIDTH_MM = 1 - 1;

    private static final Integer DEFAULT_TOTAL_HEIGHT_MM = 1;
    private static final Integer UPDATED_TOTAL_HEIGHT_MM = 2;
    private static final Integer SMALLER_TOTAL_HEIGHT_MM = 1 - 1;

    private static final Integer DEFAULT_TOTAL_DEPTH_MM = 1;
    private static final Integer UPDATED_TOTAL_DEPTH_MM = 2;
    private static final Integer SMALLER_TOTAL_DEPTH_MM = 1 - 1;

    private static final String DEFAULT_STYLE = "AAAAAAAAAA";
    private static final String UPDATED_STYLE = "BBBBBBBBBB";

    private static final FinishType DEFAULT_PRIMARY_FINISH = FinishType.MATTE_WHITE;
    private static final FinishType UPDATED_PRIMARY_FINISH = FinishType.MATTE_GRAY;

    private static final String DEFAULT_HANDLE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTERTOP_MATERIAL = "AAAAAAAAAA";
    private static final String UPDATED_COUNTERTOP_MATERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_SINK_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_SINK_POSITION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CONFIRMED_BY_CLIENT = false;
    private static final Boolean UPDATED_CONFIRMED_BY_CLIENT = true;

    private static final String DEFAULT_EXTRACTED_JSON = "AAAAAAAAAA";
    private static final String UPDATED_EXTRACTED_JSON = "BBBBBBBBBB";

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/kitchen-specs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KitchenSpecRepository kitchenSpecRepository;

    @Mock
    private KitchenSpecRepository kitchenSpecRepositoryMock;

    @Autowired
    private KitchenSpecMapper kitchenSpecMapper;

    @Mock
    private KitchenSpecService kitchenSpecServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKitchenSpecMockMvc;

    private KitchenSpec kitchenSpec;

    private KitchenSpec insertedKitchenSpec;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KitchenSpec createEntity(EntityManager em) {
        KitchenSpec kitchenSpec = new KitchenSpec()
            .layout(DEFAULT_LAYOUT)
            .totalWidthMm(DEFAULT_TOTAL_WIDTH_MM)
            .totalHeightMm(DEFAULT_TOTAL_HEIGHT_MM)
            .totalDepthMm(DEFAULT_TOTAL_DEPTH_MM)
            .style(DEFAULT_STYLE)
            .primaryFinish(DEFAULT_PRIMARY_FINISH)
            .handleType(DEFAULT_HANDLE_TYPE)
            .countertopMaterial(DEFAULT_COUNTERTOP_MATERIAL)
            .sinkPosition(DEFAULT_SINK_POSITION)
            .confirmedByClient(DEFAULT_CONFIRMED_BY_CLIENT)
            .extractedJson(DEFAULT_EXTRACTED_JSON)
            .confirmedAt(DEFAULT_CONFIRMED_AT);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        kitchenSpec.setPrimaryMaterial(material);
        return kitchenSpec;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KitchenSpec createUpdatedEntity(EntityManager em) {
        KitchenSpec updatedKitchenSpec = new KitchenSpec()
            .layout(UPDATED_LAYOUT)
            .totalWidthMm(UPDATED_TOTAL_WIDTH_MM)
            .totalHeightMm(UPDATED_TOTAL_HEIGHT_MM)
            .totalDepthMm(UPDATED_TOTAL_DEPTH_MM)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .handleType(UPDATED_HANDLE_TYPE)
            .countertopMaterial(UPDATED_COUNTERTOP_MATERIAL)
            .sinkPosition(UPDATED_SINK_POSITION)
            .confirmedByClient(UPDATED_CONFIRMED_BY_CLIENT)
            .extractedJson(UPDATED_EXTRACTED_JSON)
            .confirmedAt(UPDATED_CONFIRMED_AT);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createUpdatedEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        updatedKitchenSpec.setPrimaryMaterial(material);
        return updatedKitchenSpec;
    }

    @BeforeEach
    void initTest() {
        kitchenSpec = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedKitchenSpec != null) {
            kitchenSpecRepository.delete(insertedKitchenSpec);
            insertedKitchenSpec = null;
        }
    }

    @Test
    @Transactional
    void createKitchenSpec() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);
        var returnedKitchenSpecDTO = om.readValue(
            restKitchenSpecMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KitchenSpecDTO.class
        );

        // Validate the KitchenSpec in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKitchenSpec = kitchenSpecMapper.toEntity(returnedKitchenSpecDTO);
        assertKitchenSpecUpdatableFieldsEquals(returnedKitchenSpec, getPersistedKitchenSpec(returnedKitchenSpec));

        insertedKitchenSpec = returnedKitchenSpec;
    }

    @Test
    @Transactional
    void createKitchenSpecWithExistingId() throws Exception {
        // Create the KitchenSpec with an existing ID
        kitchenSpec.setId(1L);
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKitchenSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLayoutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        kitchenSpec.setLayout(null);

        // Create the KitchenSpec, which fails.
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        restKitchenSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStyleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        kitchenSpec.setStyle(null);

        // Create the KitchenSpec, which fails.
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        restKitchenSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrimaryFinishIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        kitchenSpec.setPrimaryFinish(null);

        // Create the KitchenSpec, which fails.
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        restKitchenSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkConfirmedByClientIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        kitchenSpec.setConfirmedByClient(null);

        // Create the KitchenSpec, which fails.
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        restKitchenSpecMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllKitchenSpecs() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList
        restKitchenSpecMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kitchenSpec.getId().intValue())))
            .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT.toString())))
            .andExpect(jsonPath("$.[*].totalWidthMm").value(hasItem(DEFAULT_TOTAL_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].totalHeightMm").value(hasItem(DEFAULT_TOTAL_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].totalDepthMm").value(hasItem(DEFAULT_TOTAL_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].style").value(hasItem(DEFAULT_STYLE)))
            .andExpect(jsonPath("$.[*].primaryFinish").value(hasItem(DEFAULT_PRIMARY_FINISH.toString())))
            .andExpect(jsonPath("$.[*].handleType").value(hasItem(DEFAULT_HANDLE_TYPE)))
            .andExpect(jsonPath("$.[*].countertopMaterial").value(hasItem(DEFAULT_COUNTERTOP_MATERIAL)))
            .andExpect(jsonPath("$.[*].sinkPosition").value(hasItem(DEFAULT_SINK_POSITION)))
            .andExpect(jsonPath("$.[*].confirmedByClient").value(hasItem(DEFAULT_CONFIRMED_BY_CLIENT)))
            .andExpect(jsonPath("$.[*].extractedJson").value(hasItem(DEFAULT_EXTRACTED_JSON)))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKitchenSpecsWithEagerRelationshipsIsEnabled() throws Exception {
        when(kitchenSpecServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKitchenSpecMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(kitchenSpecServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllKitchenSpecsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(kitchenSpecServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restKitchenSpecMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(kitchenSpecRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getKitchenSpec() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get the kitchenSpec
        restKitchenSpecMockMvc
            .perform(get(ENTITY_API_URL_ID, kitchenSpec.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(kitchenSpec.getId().intValue()))
            .andExpect(jsonPath("$.layout").value(DEFAULT_LAYOUT.toString()))
            .andExpect(jsonPath("$.totalWidthMm").value(DEFAULT_TOTAL_WIDTH_MM))
            .andExpect(jsonPath("$.totalHeightMm").value(DEFAULT_TOTAL_HEIGHT_MM))
            .andExpect(jsonPath("$.totalDepthMm").value(DEFAULT_TOTAL_DEPTH_MM))
            .andExpect(jsonPath("$.style").value(DEFAULT_STYLE))
            .andExpect(jsonPath("$.primaryFinish").value(DEFAULT_PRIMARY_FINISH.toString()))
            .andExpect(jsonPath("$.handleType").value(DEFAULT_HANDLE_TYPE))
            .andExpect(jsonPath("$.countertopMaterial").value(DEFAULT_COUNTERTOP_MATERIAL))
            .andExpect(jsonPath("$.sinkPosition").value(DEFAULT_SINK_POSITION))
            .andExpect(jsonPath("$.confirmedByClient").value(DEFAULT_CONFIRMED_BY_CLIENT))
            .andExpect(jsonPath("$.extractedJson").value(DEFAULT_EXTRACTED_JSON))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()));
    }

    @Test
    @Transactional
    void getKitchenSpecsByIdFiltering() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        Long id = kitchenSpec.getId();

        defaultKitchenSpecFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultKitchenSpecFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultKitchenSpecFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByLayoutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where layout equals to
        defaultKitchenSpecFiltering("layout.equals=" + DEFAULT_LAYOUT, "layout.equals=" + UPDATED_LAYOUT);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByLayoutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where layout in
        defaultKitchenSpecFiltering("layout.in=" + DEFAULT_LAYOUT + "," + UPDATED_LAYOUT, "layout.in=" + UPDATED_LAYOUT);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByLayoutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where layout is not null
        defaultKitchenSpecFiltering("layout.specified=true", "layout.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm equals to
        defaultKitchenSpecFiltering("totalWidthMm.equals=" + DEFAULT_TOTAL_WIDTH_MM, "totalWidthMm.equals=" + UPDATED_TOTAL_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm in
        defaultKitchenSpecFiltering(
            "totalWidthMm.in=" + DEFAULT_TOTAL_WIDTH_MM + "," + UPDATED_TOTAL_WIDTH_MM,
            "totalWidthMm.in=" + UPDATED_TOTAL_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm is not null
        defaultKitchenSpecFiltering("totalWidthMm.specified=true", "totalWidthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm is greater than or equal to
        defaultKitchenSpecFiltering(
            "totalWidthMm.greaterThanOrEqual=" + DEFAULT_TOTAL_WIDTH_MM,
            "totalWidthMm.greaterThanOrEqual=" + UPDATED_TOTAL_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm is less than or equal to
        defaultKitchenSpecFiltering(
            "totalWidthMm.lessThanOrEqual=" + DEFAULT_TOTAL_WIDTH_MM,
            "totalWidthMm.lessThanOrEqual=" + SMALLER_TOTAL_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm is less than
        defaultKitchenSpecFiltering("totalWidthMm.lessThan=" + UPDATED_TOTAL_WIDTH_MM, "totalWidthMm.lessThan=" + DEFAULT_TOTAL_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalWidthMm is greater than
        defaultKitchenSpecFiltering(
            "totalWidthMm.greaterThan=" + SMALLER_TOTAL_WIDTH_MM,
            "totalWidthMm.greaterThan=" + DEFAULT_TOTAL_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm equals to
        defaultKitchenSpecFiltering("totalHeightMm.equals=" + DEFAULT_TOTAL_HEIGHT_MM, "totalHeightMm.equals=" + UPDATED_TOTAL_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm in
        defaultKitchenSpecFiltering(
            "totalHeightMm.in=" + DEFAULT_TOTAL_HEIGHT_MM + "," + UPDATED_TOTAL_HEIGHT_MM,
            "totalHeightMm.in=" + UPDATED_TOTAL_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm is not null
        defaultKitchenSpecFiltering("totalHeightMm.specified=true", "totalHeightMm.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm is greater than or equal to
        defaultKitchenSpecFiltering(
            "totalHeightMm.greaterThanOrEqual=" + DEFAULT_TOTAL_HEIGHT_MM,
            "totalHeightMm.greaterThanOrEqual=" + UPDATED_TOTAL_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm is less than or equal to
        defaultKitchenSpecFiltering(
            "totalHeightMm.lessThanOrEqual=" + DEFAULT_TOTAL_HEIGHT_MM,
            "totalHeightMm.lessThanOrEqual=" + SMALLER_TOTAL_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm is less than
        defaultKitchenSpecFiltering(
            "totalHeightMm.lessThan=" + UPDATED_TOTAL_HEIGHT_MM,
            "totalHeightMm.lessThan=" + DEFAULT_TOTAL_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalHeightMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalHeightMm is greater than
        defaultKitchenSpecFiltering(
            "totalHeightMm.greaterThan=" + SMALLER_TOTAL_HEIGHT_MM,
            "totalHeightMm.greaterThan=" + DEFAULT_TOTAL_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm equals to
        defaultKitchenSpecFiltering("totalDepthMm.equals=" + DEFAULT_TOTAL_DEPTH_MM, "totalDepthMm.equals=" + UPDATED_TOTAL_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm in
        defaultKitchenSpecFiltering(
            "totalDepthMm.in=" + DEFAULT_TOTAL_DEPTH_MM + "," + UPDATED_TOTAL_DEPTH_MM,
            "totalDepthMm.in=" + UPDATED_TOTAL_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm is not null
        defaultKitchenSpecFiltering("totalDepthMm.specified=true", "totalDepthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm is greater than or equal to
        defaultKitchenSpecFiltering(
            "totalDepthMm.greaterThanOrEqual=" + DEFAULT_TOTAL_DEPTH_MM,
            "totalDepthMm.greaterThanOrEqual=" + UPDATED_TOTAL_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm is less than or equal to
        defaultKitchenSpecFiltering(
            "totalDepthMm.lessThanOrEqual=" + DEFAULT_TOTAL_DEPTH_MM,
            "totalDepthMm.lessThanOrEqual=" + SMALLER_TOTAL_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm is less than
        defaultKitchenSpecFiltering("totalDepthMm.lessThan=" + UPDATED_TOTAL_DEPTH_MM, "totalDepthMm.lessThan=" + DEFAULT_TOTAL_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByTotalDepthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where totalDepthMm is greater than
        defaultKitchenSpecFiltering(
            "totalDepthMm.greaterThan=" + SMALLER_TOTAL_DEPTH_MM,
            "totalDepthMm.greaterThan=" + DEFAULT_TOTAL_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByStyleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where style equals to
        defaultKitchenSpecFiltering("style.equals=" + DEFAULT_STYLE, "style.equals=" + UPDATED_STYLE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByStyleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where style in
        defaultKitchenSpecFiltering("style.in=" + DEFAULT_STYLE + "," + UPDATED_STYLE, "style.in=" + UPDATED_STYLE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByStyleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where style is not null
        defaultKitchenSpecFiltering("style.specified=true", "style.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByStyleContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where style contains
        defaultKitchenSpecFiltering("style.contains=" + DEFAULT_STYLE, "style.contains=" + UPDATED_STYLE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByStyleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where style does not contain
        defaultKitchenSpecFiltering("style.doesNotContain=" + UPDATED_STYLE, "style.doesNotContain=" + DEFAULT_STYLE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByPrimaryFinishIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where primaryFinish equals to
        defaultKitchenSpecFiltering("primaryFinish.equals=" + DEFAULT_PRIMARY_FINISH, "primaryFinish.equals=" + UPDATED_PRIMARY_FINISH);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByPrimaryFinishIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where primaryFinish in
        defaultKitchenSpecFiltering(
            "primaryFinish.in=" + DEFAULT_PRIMARY_FINISH + "," + UPDATED_PRIMARY_FINISH,
            "primaryFinish.in=" + UPDATED_PRIMARY_FINISH
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByPrimaryFinishIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where primaryFinish is not null
        defaultKitchenSpecFiltering("primaryFinish.specified=true", "primaryFinish.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByHandleTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where handleType equals to
        defaultKitchenSpecFiltering("handleType.equals=" + DEFAULT_HANDLE_TYPE, "handleType.equals=" + UPDATED_HANDLE_TYPE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByHandleTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where handleType in
        defaultKitchenSpecFiltering(
            "handleType.in=" + DEFAULT_HANDLE_TYPE + "," + UPDATED_HANDLE_TYPE,
            "handleType.in=" + UPDATED_HANDLE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByHandleTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where handleType is not null
        defaultKitchenSpecFiltering("handleType.specified=true", "handleType.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByHandleTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where handleType contains
        defaultKitchenSpecFiltering("handleType.contains=" + DEFAULT_HANDLE_TYPE, "handleType.contains=" + UPDATED_HANDLE_TYPE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByHandleTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where handleType does not contain
        defaultKitchenSpecFiltering("handleType.doesNotContain=" + UPDATED_HANDLE_TYPE, "handleType.doesNotContain=" + DEFAULT_HANDLE_TYPE);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByCountertopMaterialIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where countertopMaterial equals to
        defaultKitchenSpecFiltering(
            "countertopMaterial.equals=" + DEFAULT_COUNTERTOP_MATERIAL,
            "countertopMaterial.equals=" + UPDATED_COUNTERTOP_MATERIAL
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByCountertopMaterialIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where countertopMaterial in
        defaultKitchenSpecFiltering(
            "countertopMaterial.in=" + DEFAULT_COUNTERTOP_MATERIAL + "," + UPDATED_COUNTERTOP_MATERIAL,
            "countertopMaterial.in=" + UPDATED_COUNTERTOP_MATERIAL
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByCountertopMaterialIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where countertopMaterial is not null
        defaultKitchenSpecFiltering("countertopMaterial.specified=true", "countertopMaterial.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByCountertopMaterialContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where countertopMaterial contains
        defaultKitchenSpecFiltering(
            "countertopMaterial.contains=" + DEFAULT_COUNTERTOP_MATERIAL,
            "countertopMaterial.contains=" + UPDATED_COUNTERTOP_MATERIAL
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByCountertopMaterialNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where countertopMaterial does not contain
        defaultKitchenSpecFiltering(
            "countertopMaterial.doesNotContain=" + UPDATED_COUNTERTOP_MATERIAL,
            "countertopMaterial.doesNotContain=" + DEFAULT_COUNTERTOP_MATERIAL
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsBySinkPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where sinkPosition equals to
        defaultKitchenSpecFiltering("sinkPosition.equals=" + DEFAULT_SINK_POSITION, "sinkPosition.equals=" + UPDATED_SINK_POSITION);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsBySinkPositionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where sinkPosition in
        defaultKitchenSpecFiltering(
            "sinkPosition.in=" + DEFAULT_SINK_POSITION + "," + UPDATED_SINK_POSITION,
            "sinkPosition.in=" + UPDATED_SINK_POSITION
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsBySinkPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where sinkPosition is not null
        defaultKitchenSpecFiltering("sinkPosition.specified=true", "sinkPosition.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsBySinkPositionContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where sinkPosition contains
        defaultKitchenSpecFiltering("sinkPosition.contains=" + DEFAULT_SINK_POSITION, "sinkPosition.contains=" + UPDATED_SINK_POSITION);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsBySinkPositionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where sinkPosition does not contain
        defaultKitchenSpecFiltering(
            "sinkPosition.doesNotContain=" + UPDATED_SINK_POSITION,
            "sinkPosition.doesNotContain=" + DEFAULT_SINK_POSITION
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByConfirmedByClientIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where confirmedByClient equals to
        defaultKitchenSpecFiltering(
            "confirmedByClient.equals=" + DEFAULT_CONFIRMED_BY_CLIENT,
            "confirmedByClient.equals=" + UPDATED_CONFIRMED_BY_CLIENT
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByConfirmedByClientIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where confirmedByClient in
        defaultKitchenSpecFiltering(
            "confirmedByClient.in=" + DEFAULT_CONFIRMED_BY_CLIENT + "," + UPDATED_CONFIRMED_BY_CLIENT,
            "confirmedByClient.in=" + UPDATED_CONFIRMED_BY_CLIENT
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByConfirmedByClientIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where confirmedByClient is not null
        defaultKitchenSpecFiltering("confirmedByClient.specified=true", "confirmedByClient.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where confirmedAt equals to
        defaultKitchenSpecFiltering("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT, "confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where confirmedAt in
        defaultKitchenSpecFiltering(
            "confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT,
            "confirmedAt.in=" + UPDATED_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        // Get all the kitchenSpecList where confirmedAt is not null
        defaultKitchenSpecFiltering("confirmedAt.specified=true", "confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllKitchenSpecsByPrimaryMaterialIsEqualToSomething() throws Exception {
        Material primaryMaterial;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            kitchenSpecRepository.saveAndFlush(kitchenSpec);
            primaryMaterial = MaterialResourceIT.createEntity();
        } else {
            primaryMaterial = TestUtil.findAll(em, Material.class).get(0);
        }
        em.persist(primaryMaterial);
        em.flush();
        kitchenSpec.setPrimaryMaterial(primaryMaterial);
        kitchenSpecRepository.saveAndFlush(kitchenSpec);
        Long primaryMaterialId = primaryMaterial.getId();
        // Get all the kitchenSpecList where primaryMaterial equals to primaryMaterialId
        defaultKitchenSpecShouldBeFound("primaryMaterialId.equals=" + primaryMaterialId);

        // Get all the kitchenSpecList where primaryMaterial equals to (primaryMaterialId + 1)
        defaultKitchenSpecShouldNotBeFound("primaryMaterialId.equals=" + (primaryMaterialId + 1));
    }

    private void defaultKitchenSpecFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultKitchenSpecShouldBeFound(shouldBeFound);
        defaultKitchenSpecShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKitchenSpecShouldBeFound(String filter) throws Exception {
        restKitchenSpecMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kitchenSpec.getId().intValue())))
            .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT.toString())))
            .andExpect(jsonPath("$.[*].totalWidthMm").value(hasItem(DEFAULT_TOTAL_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].totalHeightMm").value(hasItem(DEFAULT_TOTAL_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].totalDepthMm").value(hasItem(DEFAULT_TOTAL_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].style").value(hasItem(DEFAULT_STYLE)))
            .andExpect(jsonPath("$.[*].primaryFinish").value(hasItem(DEFAULT_PRIMARY_FINISH.toString())))
            .andExpect(jsonPath("$.[*].handleType").value(hasItem(DEFAULT_HANDLE_TYPE)))
            .andExpect(jsonPath("$.[*].countertopMaterial").value(hasItem(DEFAULT_COUNTERTOP_MATERIAL)))
            .andExpect(jsonPath("$.[*].sinkPosition").value(hasItem(DEFAULT_SINK_POSITION)))
            .andExpect(jsonPath("$.[*].confirmedByClient").value(hasItem(DEFAULT_CONFIRMED_BY_CLIENT)))
            .andExpect(jsonPath("$.[*].extractedJson").value(hasItem(DEFAULT_EXTRACTED_JSON)))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())));

        // Check, that the count call also returns 1
        restKitchenSpecMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKitchenSpecShouldNotBeFound(String filter) throws Exception {
        restKitchenSpecMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKitchenSpecMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKitchenSpec() throws Exception {
        // Get the kitchenSpec
        restKitchenSpecMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKitchenSpec() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchenSpec
        KitchenSpec updatedKitchenSpec = kitchenSpecRepository.findById(kitchenSpec.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKitchenSpec are not directly saved in db
        em.detach(updatedKitchenSpec);
        updatedKitchenSpec
            .layout(UPDATED_LAYOUT)
            .totalWidthMm(UPDATED_TOTAL_WIDTH_MM)
            .totalHeightMm(UPDATED_TOTAL_HEIGHT_MM)
            .totalDepthMm(UPDATED_TOTAL_DEPTH_MM)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .handleType(UPDATED_HANDLE_TYPE)
            .countertopMaterial(UPDATED_COUNTERTOP_MATERIAL)
            .sinkPosition(UPDATED_SINK_POSITION)
            .confirmedByClient(UPDATED_CONFIRMED_BY_CLIENT)
            .extractedJson(UPDATED_EXTRACTED_JSON)
            .confirmedAt(UPDATED_CONFIRMED_AT);
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(updatedKitchenSpec);

        restKitchenSpecMockMvc
            .perform(
                put(ENTITY_API_URL_ID, kitchenSpecDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kitchenSpecDTO))
            )
            .andExpect(status().isOk());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKitchenSpecToMatchAllProperties(updatedKitchenSpec);
    }

    @Test
    @Transactional
    void putNonExistingKitchenSpec() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSpec.setId(longCount.incrementAndGet());

        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKitchenSpecMockMvc
            .perform(
                put(ENTITY_API_URL_ID, kitchenSpecDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kitchenSpecDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKitchenSpec() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSpec.setId(longCount.incrementAndGet());

        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenSpecMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kitchenSpecDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKitchenSpec() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSpec.setId(longCount.incrementAndGet());

        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenSpecMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKitchenSpecWithPatch() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchenSpec using partial update
        KitchenSpec partialUpdatedKitchenSpec = new KitchenSpec();
        partialUpdatedKitchenSpec.setId(kitchenSpec.getId());

        partialUpdatedKitchenSpec
            .totalWidthMm(UPDATED_TOTAL_WIDTH_MM)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .handleType(UPDATED_HANDLE_TYPE)
            .countertopMaterial(UPDATED_COUNTERTOP_MATERIAL)
            .sinkPosition(UPDATED_SINK_POSITION)
            .confirmedByClient(UPDATED_CONFIRMED_BY_CLIENT);

        restKitchenSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKitchenSpec.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKitchenSpec))
            )
            .andExpect(status().isOk());

        // Validate the KitchenSpec in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenSpecUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedKitchenSpec, kitchenSpec),
            getPersistedKitchenSpec(kitchenSpec)
        );
    }

    @Test
    @Transactional
    void fullUpdateKitchenSpecWithPatch() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchenSpec using partial update
        KitchenSpec partialUpdatedKitchenSpec = new KitchenSpec();
        partialUpdatedKitchenSpec.setId(kitchenSpec.getId());

        partialUpdatedKitchenSpec
            .layout(UPDATED_LAYOUT)
            .totalWidthMm(UPDATED_TOTAL_WIDTH_MM)
            .totalHeightMm(UPDATED_TOTAL_HEIGHT_MM)
            .totalDepthMm(UPDATED_TOTAL_DEPTH_MM)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .handleType(UPDATED_HANDLE_TYPE)
            .countertopMaterial(UPDATED_COUNTERTOP_MATERIAL)
            .sinkPosition(UPDATED_SINK_POSITION)
            .confirmedByClient(UPDATED_CONFIRMED_BY_CLIENT)
            .extractedJson(UPDATED_EXTRACTED_JSON)
            .confirmedAt(UPDATED_CONFIRMED_AT);

        restKitchenSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKitchenSpec.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKitchenSpec))
            )
            .andExpect(status().isOk());

        // Validate the KitchenSpec in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenSpecUpdatableFieldsEquals(partialUpdatedKitchenSpec, getPersistedKitchenSpec(partialUpdatedKitchenSpec));
    }

    @Test
    @Transactional
    void patchNonExistingKitchenSpec() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSpec.setId(longCount.incrementAndGet());

        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKitchenSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, kitchenSpecDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(kitchenSpecDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKitchenSpec() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSpec.setId(longCount.incrementAndGet());

        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenSpecMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(kitchenSpecDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKitchenSpec() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSpec.setId(longCount.incrementAndGet());

        // Create the KitchenSpec
        KitchenSpecDTO kitchenSpecDTO = kitchenSpecMapper.toDto(kitchenSpec);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenSpecMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(kitchenSpecDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KitchenSpec in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKitchenSpec() throws Exception {
        // Initialize the database
        insertedKitchenSpec = kitchenSpecRepository.saveAndFlush(kitchenSpec);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the kitchenSpec
        restKitchenSpecMockMvc
            .perform(delete(ENTITY_API_URL_ID, kitchenSpec.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return kitchenSpecRepository.count();
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

    protected KitchenSpec getPersistedKitchenSpec(KitchenSpec kitchenSpec) {
        return kitchenSpecRepository.findById(kitchenSpec.getId()).orElseThrow();
    }

    protected void assertPersistedKitchenSpecToMatchAllProperties(KitchenSpec expectedKitchenSpec) {
        assertKitchenSpecAllPropertiesEquals(expectedKitchenSpec, getPersistedKitchenSpec(expectedKitchenSpec));
    }

    protected void assertPersistedKitchenSpecToMatchUpdatableProperties(KitchenSpec expectedKitchenSpec) {
        assertKitchenSpecAllUpdatablePropertiesEquals(expectedKitchenSpec, getPersistedKitchenSpec(expectedKitchenSpec));
    }
}
