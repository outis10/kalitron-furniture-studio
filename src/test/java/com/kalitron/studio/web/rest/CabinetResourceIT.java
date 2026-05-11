package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.CabinetAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.repository.CabinetRepository;
import com.kalitron.studio.service.CabinetService;
import com.kalitron.studio.service.dto.CabinetDTO;
import com.kalitron.studio.service.mapper.CabinetMapper;
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
 * Integration tests for the {@link CabinetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CabinetResourceIT {

    private static final String DEFAULT_CABINET_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CABINET_CODE = "BBBBBBBBBB";

    private static final CabinetCategory DEFAULT_CATEGORY = CabinetCategory.UPPER;
    private static final CabinetCategory UPDATED_CATEGORY = CabinetCategory.LOWER;

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Integer DEFAULT_WIDTH_MM = 1;
    private static final Integer UPDATED_WIDTH_MM = 2;
    private static final Integer SMALLER_WIDTH_MM = 1 - 1;

    private static final Integer DEFAULT_HEIGHT_MM = 1;
    private static final Integer UPDATED_HEIGHT_MM = 2;
    private static final Integer SMALLER_HEIGHT_MM = 1 - 1;

    private static final Integer DEFAULT_DEPTH_MM = 1;
    private static final Integer UPDATED_DEPTH_MM = 2;
    private static final Integer SMALLER_DEPTH_MM = 1 - 1;

    private static final Integer DEFAULT_DOORS = 0;
    private static final Integer UPDATED_DOORS = 1;
    private static final Integer SMALLER_DOORS = 0 - 1;

    private static final Integer DEFAULT_DRAWERS = 0;
    private static final Integer UPDATED_DRAWERS = 1;
    private static final Integer SMALLER_DRAWERS = 0 - 1;

    private static final Integer DEFAULT_SHELVES = 0;
    private static final Integer UPDATED_SHELVES = 1;
    private static final Integer SMALLER_SHELVES = 0 - 1;

    private static final FinishType DEFAULT_FINISH = FinishType.MATTE_WHITE;
    private static final FinishType UPDATED_FINISH = FinishType.MATTE_GRAY;

    private static final Integer DEFAULT_POSITION_X = 1;
    private static final Integer UPDATED_POSITION_X = 2;
    private static final Integer SMALLER_POSITION_X = 1 - 1;

    private static final Integer DEFAULT_POSITION_Y = 1;
    private static final Integer UPDATED_POSITION_Y = 2;
    private static final Integer SMALLER_POSITION_Y = 1 - 1;

    private static final Integer DEFAULT_POSITION_Z = 1;
    private static final Integer UPDATED_POSITION_Z = 2;
    private static final Integer SMALLER_POSITION_Z = 1 - 1;

    private static final Integer DEFAULT_ROTATION_DEG = 1;
    private static final Integer UPDATED_ROTATION_DEG = 2;
    private static final Integer SMALLER_ROTATION_DEG = 1 - 1;

    private static final Integer DEFAULT_POSITION_SEQ = 1;
    private static final Integer UPDATED_POSITION_SEQ = 2;
    private static final Integer SMALLER_POSITION_SEQ = 1 - 1;

    private static final String DEFAULT_CSV_ROW_JSON = "AAAAAAAAAA";
    private static final String UPDATED_CSV_ROW_JSON = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cabinets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CabinetRepository cabinetRepository;

    @Mock
    private CabinetRepository cabinetRepositoryMock;

    @Autowired
    private CabinetMapper cabinetMapper;

    @Mock
    private CabinetService cabinetServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCabinetMockMvc;

    private Cabinet cabinet;

    private Cabinet insertedCabinet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cabinet createEntity(EntityManager em) {
        Cabinet cabinet = new Cabinet()
            .cabinetCode(DEFAULT_CABINET_CODE)
            .category(DEFAULT_CATEGORY)
            .label(DEFAULT_LABEL)
            .widthMm(DEFAULT_WIDTH_MM)
            .heightMm(DEFAULT_HEIGHT_MM)
            .depthMm(DEFAULT_DEPTH_MM)
            .doors(DEFAULT_DOORS)
            .drawers(DEFAULT_DRAWERS)
            .shelves(DEFAULT_SHELVES)
            .finish(DEFAULT_FINISH)
            .positionX(DEFAULT_POSITION_X)
            .positionY(DEFAULT_POSITION_Y)
            .positionZ(DEFAULT_POSITION_Z)
            .rotationDeg(DEFAULT_ROTATION_DEG)
            .positionSeq(DEFAULT_POSITION_SEQ)
            .csvRowJson(DEFAULT_CSV_ROW_JSON)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        cabinet.setMaterial(material);
        // Add required entity
        KitchenSpec kitchenSpec;
        if (TestUtil.findAll(em, KitchenSpec.class).isEmpty()) {
            kitchenSpec = KitchenSpecResourceIT.createEntity(em);
            em.persist(kitchenSpec);
            em.flush();
        } else {
            kitchenSpec = TestUtil.findAll(em, KitchenSpec.class).get(0);
        }
        cabinet.setSpec(kitchenSpec);
        return cabinet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cabinet createUpdatedEntity(EntityManager em) {
        Cabinet updatedCabinet = new Cabinet()
            .cabinetCode(UPDATED_CABINET_CODE)
            .category(UPDATED_CATEGORY)
            .label(UPDATED_LABEL)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .doors(UPDATED_DOORS)
            .drawers(UPDATED_DRAWERS)
            .shelves(UPDATED_SHELVES)
            .finish(UPDATED_FINISH)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .positionZ(UPDATED_POSITION_Z)
            .rotationDeg(UPDATED_ROTATION_DEG)
            .positionSeq(UPDATED_POSITION_SEQ)
            .csvRowJson(UPDATED_CSV_ROW_JSON)
            .notes(UPDATED_NOTES);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createUpdatedEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        updatedCabinet.setMaterial(material);
        // Add required entity
        KitchenSpec kitchenSpec;
        if (TestUtil.findAll(em, KitchenSpec.class).isEmpty()) {
            kitchenSpec = KitchenSpecResourceIT.createUpdatedEntity(em);
            em.persist(kitchenSpec);
            em.flush();
        } else {
            kitchenSpec = TestUtil.findAll(em, KitchenSpec.class).get(0);
        }
        updatedCabinet.setSpec(kitchenSpec);
        return updatedCabinet;
    }

    @BeforeEach
    void initTest() {
        cabinet = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCabinet != null) {
            cabinetRepository.delete(insertedCabinet);
            insertedCabinet = null;
        }
    }

    @Test
    @Transactional
    void createCabinet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);
        var returnedCabinetDTO = om.readValue(
            restCabinetMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CabinetDTO.class
        );

        // Validate the Cabinet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCabinet = cabinetMapper.toEntity(returnedCabinetDTO);
        assertCabinetUpdatableFieldsEquals(returnedCabinet, getPersistedCabinet(returnedCabinet));

        insertedCabinet = returnedCabinet;
    }

    @Test
    @Transactional
    void createCabinetWithExistingId() throws Exception {
        // Create the Cabinet with an existing ID
        cabinet.setId(1L);
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCabinetCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setCabinetCode(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setCategory(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setLabel(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWidthMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setWidthMm(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHeightMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setHeightMm(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDepthMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setDepthMm(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDoorsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setDoors(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDrawersIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setDrawers(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFinishIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinet.setFinish(null);

        // Create the Cabinet, which fails.
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        restCabinetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCabinets() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList
        restCabinetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cabinet.getId().intValue())))
            .andExpect(jsonPath("$.[*].cabinetCode").value(hasItem(DEFAULT_CABINET_CODE)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].widthMm").value(hasItem(DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].heightMm").value(hasItem(DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].depthMm").value(hasItem(DEFAULT_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].doors").value(hasItem(DEFAULT_DOORS)))
            .andExpect(jsonPath("$.[*].drawers").value(hasItem(DEFAULT_DRAWERS)))
            .andExpect(jsonPath("$.[*].shelves").value(hasItem(DEFAULT_SHELVES)))
            .andExpect(jsonPath("$.[*].finish").value(hasItem(DEFAULT_FINISH.toString())))
            .andExpect(jsonPath("$.[*].positionX").value(hasItem(DEFAULT_POSITION_X)))
            .andExpect(jsonPath("$.[*].positionY").value(hasItem(DEFAULT_POSITION_Y)))
            .andExpect(jsonPath("$.[*].positionZ").value(hasItem(DEFAULT_POSITION_Z)))
            .andExpect(jsonPath("$.[*].rotationDeg").value(hasItem(DEFAULT_ROTATION_DEG)))
            .andExpect(jsonPath("$.[*].positionSeq").value(hasItem(DEFAULT_POSITION_SEQ)))
            .andExpect(jsonPath("$.[*].csvRowJson").value(hasItem(DEFAULT_CSV_ROW_JSON)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCabinetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(cabinetServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCabinetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(cabinetServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCabinetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cabinetServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCabinetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(cabinetRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCabinet() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get the cabinet
        restCabinetMockMvc
            .perform(get(ENTITY_API_URL_ID, cabinet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cabinet.getId().intValue()))
            .andExpect(jsonPath("$.cabinetCode").value(DEFAULT_CABINET_CODE))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.widthMm").value(DEFAULT_WIDTH_MM))
            .andExpect(jsonPath("$.heightMm").value(DEFAULT_HEIGHT_MM))
            .andExpect(jsonPath("$.depthMm").value(DEFAULT_DEPTH_MM))
            .andExpect(jsonPath("$.doors").value(DEFAULT_DOORS))
            .andExpect(jsonPath("$.drawers").value(DEFAULT_DRAWERS))
            .andExpect(jsonPath("$.shelves").value(DEFAULT_SHELVES))
            .andExpect(jsonPath("$.finish").value(DEFAULT_FINISH.toString()))
            .andExpect(jsonPath("$.positionX").value(DEFAULT_POSITION_X))
            .andExpect(jsonPath("$.positionY").value(DEFAULT_POSITION_Y))
            .andExpect(jsonPath("$.positionZ").value(DEFAULT_POSITION_Z))
            .andExpect(jsonPath("$.rotationDeg").value(DEFAULT_ROTATION_DEG))
            .andExpect(jsonPath("$.positionSeq").value(DEFAULT_POSITION_SEQ))
            .andExpect(jsonPath("$.csvRowJson").value(DEFAULT_CSV_ROW_JSON))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getCabinetsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        Long id = cabinet.getId();

        defaultCabinetFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCabinetFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCabinetFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCabinetsByCabinetCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where cabinetCode equals to
        defaultCabinetFiltering("cabinetCode.equals=" + DEFAULT_CABINET_CODE, "cabinetCode.equals=" + UPDATED_CABINET_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetsByCabinetCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where cabinetCode in
        defaultCabinetFiltering(
            "cabinetCode.in=" + DEFAULT_CABINET_CODE + "," + UPDATED_CABINET_CODE,
            "cabinetCode.in=" + UPDATED_CABINET_CODE
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByCabinetCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where cabinetCode is not null
        defaultCabinetFiltering("cabinetCode.specified=true", "cabinetCode.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByCabinetCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where cabinetCode contains
        defaultCabinetFiltering("cabinetCode.contains=" + DEFAULT_CABINET_CODE, "cabinetCode.contains=" + UPDATED_CABINET_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetsByCabinetCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where cabinetCode does not contain
        defaultCabinetFiltering("cabinetCode.doesNotContain=" + UPDATED_CABINET_CODE, "cabinetCode.doesNotContain=" + DEFAULT_CABINET_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where category equals to
        defaultCabinetFiltering("category.equals=" + DEFAULT_CATEGORY, "category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllCabinetsByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where category in
        defaultCabinetFiltering("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY, "category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllCabinetsByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where category is not null
        defaultCabinetFiltering("category.specified=true", "category.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where label equals to
        defaultCabinetFiltering("label.equals=" + DEFAULT_LABEL, "label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllCabinetsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where label in
        defaultCabinetFiltering("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL, "label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllCabinetsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where label is not null
        defaultCabinetFiltering("label.specified=true", "label.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByLabelContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where label contains
        defaultCabinetFiltering("label.contains=" + DEFAULT_LABEL, "label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllCabinetsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where label does not contain
        defaultCabinetFiltering("label.doesNotContain=" + UPDATED_LABEL, "label.doesNotContain=" + DEFAULT_LABEL);
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm equals to
        defaultCabinetFiltering("widthMm.equals=" + DEFAULT_WIDTH_MM, "widthMm.equals=" + UPDATED_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm in
        defaultCabinetFiltering("widthMm.in=" + DEFAULT_WIDTH_MM + "," + UPDATED_WIDTH_MM, "widthMm.in=" + UPDATED_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm is not null
        defaultCabinetFiltering("widthMm.specified=true", "widthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm is greater than or equal to
        defaultCabinetFiltering("widthMm.greaterThanOrEqual=" + DEFAULT_WIDTH_MM, "widthMm.greaterThanOrEqual=" + UPDATED_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm is less than or equal to
        defaultCabinetFiltering("widthMm.lessThanOrEqual=" + DEFAULT_WIDTH_MM, "widthMm.lessThanOrEqual=" + SMALLER_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm is less than
        defaultCabinetFiltering("widthMm.lessThan=" + UPDATED_WIDTH_MM, "widthMm.lessThan=" + DEFAULT_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where widthMm is greater than
        defaultCabinetFiltering("widthMm.greaterThan=" + SMALLER_WIDTH_MM, "widthMm.greaterThan=" + DEFAULT_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm equals to
        defaultCabinetFiltering("heightMm.equals=" + DEFAULT_HEIGHT_MM, "heightMm.equals=" + UPDATED_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm in
        defaultCabinetFiltering("heightMm.in=" + DEFAULT_HEIGHT_MM + "," + UPDATED_HEIGHT_MM, "heightMm.in=" + UPDATED_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm is not null
        defaultCabinetFiltering("heightMm.specified=true", "heightMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm is greater than or equal to
        defaultCabinetFiltering("heightMm.greaterThanOrEqual=" + DEFAULT_HEIGHT_MM, "heightMm.greaterThanOrEqual=" + UPDATED_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm is less than or equal to
        defaultCabinetFiltering("heightMm.lessThanOrEqual=" + DEFAULT_HEIGHT_MM, "heightMm.lessThanOrEqual=" + SMALLER_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm is less than
        defaultCabinetFiltering("heightMm.lessThan=" + UPDATED_HEIGHT_MM, "heightMm.lessThan=" + DEFAULT_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByHeightMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where heightMm is greater than
        defaultCabinetFiltering("heightMm.greaterThan=" + SMALLER_HEIGHT_MM, "heightMm.greaterThan=" + DEFAULT_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm equals to
        defaultCabinetFiltering("depthMm.equals=" + DEFAULT_DEPTH_MM, "depthMm.equals=" + UPDATED_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm in
        defaultCabinetFiltering("depthMm.in=" + DEFAULT_DEPTH_MM + "," + UPDATED_DEPTH_MM, "depthMm.in=" + UPDATED_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm is not null
        defaultCabinetFiltering("depthMm.specified=true", "depthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm is greater than or equal to
        defaultCabinetFiltering("depthMm.greaterThanOrEqual=" + DEFAULT_DEPTH_MM, "depthMm.greaterThanOrEqual=" + UPDATED_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm is less than or equal to
        defaultCabinetFiltering("depthMm.lessThanOrEqual=" + DEFAULT_DEPTH_MM, "depthMm.lessThanOrEqual=" + SMALLER_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm is less than
        defaultCabinetFiltering("depthMm.lessThan=" + UPDATED_DEPTH_MM, "depthMm.lessThan=" + DEFAULT_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDepthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where depthMm is greater than
        defaultCabinetFiltering("depthMm.greaterThan=" + SMALLER_DEPTH_MM, "depthMm.greaterThan=" + DEFAULT_DEPTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors equals to
        defaultCabinetFiltering("doors.equals=" + DEFAULT_DOORS, "doors.equals=" + UPDATED_DOORS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors in
        defaultCabinetFiltering("doors.in=" + DEFAULT_DOORS + "," + UPDATED_DOORS, "doors.in=" + UPDATED_DOORS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors is not null
        defaultCabinetFiltering("doors.specified=true", "doors.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors is greater than or equal to
        defaultCabinetFiltering("doors.greaterThanOrEqual=" + DEFAULT_DOORS, "doors.greaterThanOrEqual=" + (DEFAULT_DOORS + 1));
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors is less than or equal to
        defaultCabinetFiltering("doors.lessThanOrEqual=" + DEFAULT_DOORS, "doors.lessThanOrEqual=" + SMALLER_DOORS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors is less than
        defaultCabinetFiltering("doors.lessThan=" + (DEFAULT_DOORS + 1), "doors.lessThan=" + DEFAULT_DOORS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDoorsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where doors is greater than
        defaultCabinetFiltering("doors.greaterThan=" + SMALLER_DOORS, "doors.greaterThan=" + DEFAULT_DOORS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers equals to
        defaultCabinetFiltering("drawers.equals=" + DEFAULT_DRAWERS, "drawers.equals=" + UPDATED_DRAWERS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers in
        defaultCabinetFiltering("drawers.in=" + DEFAULT_DRAWERS + "," + UPDATED_DRAWERS, "drawers.in=" + UPDATED_DRAWERS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers is not null
        defaultCabinetFiltering("drawers.specified=true", "drawers.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers is greater than or equal to
        defaultCabinetFiltering("drawers.greaterThanOrEqual=" + DEFAULT_DRAWERS, "drawers.greaterThanOrEqual=" + (DEFAULT_DRAWERS + 1));
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers is less than or equal to
        defaultCabinetFiltering("drawers.lessThanOrEqual=" + DEFAULT_DRAWERS, "drawers.lessThanOrEqual=" + SMALLER_DRAWERS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers is less than
        defaultCabinetFiltering("drawers.lessThan=" + (DEFAULT_DRAWERS + 1), "drawers.lessThan=" + DEFAULT_DRAWERS);
    }

    @Test
    @Transactional
    void getAllCabinetsByDrawersIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where drawers is greater than
        defaultCabinetFiltering("drawers.greaterThan=" + SMALLER_DRAWERS, "drawers.greaterThan=" + DEFAULT_DRAWERS);
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves equals to
        defaultCabinetFiltering("shelves.equals=" + DEFAULT_SHELVES, "shelves.equals=" + UPDATED_SHELVES);
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves in
        defaultCabinetFiltering("shelves.in=" + DEFAULT_SHELVES + "," + UPDATED_SHELVES, "shelves.in=" + UPDATED_SHELVES);
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves is not null
        defaultCabinetFiltering("shelves.specified=true", "shelves.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves is greater than or equal to
        defaultCabinetFiltering("shelves.greaterThanOrEqual=" + DEFAULT_SHELVES, "shelves.greaterThanOrEqual=" + (DEFAULT_SHELVES + 1));
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves is less than or equal to
        defaultCabinetFiltering("shelves.lessThanOrEqual=" + DEFAULT_SHELVES, "shelves.lessThanOrEqual=" + SMALLER_SHELVES);
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves is less than
        defaultCabinetFiltering("shelves.lessThan=" + (DEFAULT_SHELVES + 1), "shelves.lessThan=" + DEFAULT_SHELVES);
    }

    @Test
    @Transactional
    void getAllCabinetsByShelvesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where shelves is greater than
        defaultCabinetFiltering("shelves.greaterThan=" + SMALLER_SHELVES, "shelves.greaterThan=" + DEFAULT_SHELVES);
    }

    @Test
    @Transactional
    void getAllCabinetsByFinishIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where finish equals to
        defaultCabinetFiltering("finish.equals=" + DEFAULT_FINISH, "finish.equals=" + UPDATED_FINISH);
    }

    @Test
    @Transactional
    void getAllCabinetsByFinishIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where finish in
        defaultCabinetFiltering("finish.in=" + DEFAULT_FINISH + "," + UPDATED_FINISH, "finish.in=" + UPDATED_FINISH);
    }

    @Test
    @Transactional
    void getAllCabinetsByFinishIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where finish is not null
        defaultCabinetFiltering("finish.specified=true", "finish.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX equals to
        defaultCabinetFiltering("positionX.equals=" + DEFAULT_POSITION_X, "positionX.equals=" + UPDATED_POSITION_X);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX in
        defaultCabinetFiltering("positionX.in=" + DEFAULT_POSITION_X + "," + UPDATED_POSITION_X, "positionX.in=" + UPDATED_POSITION_X);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX is not null
        defaultCabinetFiltering("positionX.specified=true", "positionX.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX is greater than or equal to
        defaultCabinetFiltering("positionX.greaterThanOrEqual=" + DEFAULT_POSITION_X, "positionX.greaterThanOrEqual=" + UPDATED_POSITION_X);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX is less than or equal to
        defaultCabinetFiltering("positionX.lessThanOrEqual=" + DEFAULT_POSITION_X, "positionX.lessThanOrEqual=" + SMALLER_POSITION_X);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX is less than
        defaultCabinetFiltering("positionX.lessThan=" + UPDATED_POSITION_X, "positionX.lessThan=" + DEFAULT_POSITION_X);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionXIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionX is greater than
        defaultCabinetFiltering("positionX.greaterThan=" + SMALLER_POSITION_X, "positionX.greaterThan=" + DEFAULT_POSITION_X);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY equals to
        defaultCabinetFiltering("positionY.equals=" + DEFAULT_POSITION_Y, "positionY.equals=" + UPDATED_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY in
        defaultCabinetFiltering("positionY.in=" + DEFAULT_POSITION_Y + "," + UPDATED_POSITION_Y, "positionY.in=" + UPDATED_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY is not null
        defaultCabinetFiltering("positionY.specified=true", "positionY.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY is greater than or equal to
        defaultCabinetFiltering("positionY.greaterThanOrEqual=" + DEFAULT_POSITION_Y, "positionY.greaterThanOrEqual=" + UPDATED_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY is less than or equal to
        defaultCabinetFiltering("positionY.lessThanOrEqual=" + DEFAULT_POSITION_Y, "positionY.lessThanOrEqual=" + SMALLER_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY is less than
        defaultCabinetFiltering("positionY.lessThan=" + UPDATED_POSITION_Y, "positionY.lessThan=" + DEFAULT_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionYIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionY is greater than
        defaultCabinetFiltering("positionY.greaterThan=" + SMALLER_POSITION_Y, "positionY.greaterThan=" + DEFAULT_POSITION_Y);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ equals to
        defaultCabinetFiltering("positionZ.equals=" + DEFAULT_POSITION_Z, "positionZ.equals=" + UPDATED_POSITION_Z);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ in
        defaultCabinetFiltering("positionZ.in=" + DEFAULT_POSITION_Z + "," + UPDATED_POSITION_Z, "positionZ.in=" + UPDATED_POSITION_Z);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ is not null
        defaultCabinetFiltering("positionZ.specified=true", "positionZ.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ is greater than or equal to
        defaultCabinetFiltering("positionZ.greaterThanOrEqual=" + DEFAULT_POSITION_Z, "positionZ.greaterThanOrEqual=" + UPDATED_POSITION_Z);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ is less than or equal to
        defaultCabinetFiltering("positionZ.lessThanOrEqual=" + DEFAULT_POSITION_Z, "positionZ.lessThanOrEqual=" + SMALLER_POSITION_Z);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ is less than
        defaultCabinetFiltering("positionZ.lessThan=" + UPDATED_POSITION_Z, "positionZ.lessThan=" + DEFAULT_POSITION_Z);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionZIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionZ is greater than
        defaultCabinetFiltering("positionZ.greaterThan=" + SMALLER_POSITION_Z, "positionZ.greaterThan=" + DEFAULT_POSITION_Z);
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg equals to
        defaultCabinetFiltering("rotationDeg.equals=" + DEFAULT_ROTATION_DEG, "rotationDeg.equals=" + UPDATED_ROTATION_DEG);
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg in
        defaultCabinetFiltering(
            "rotationDeg.in=" + DEFAULT_ROTATION_DEG + "," + UPDATED_ROTATION_DEG,
            "rotationDeg.in=" + UPDATED_ROTATION_DEG
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg is not null
        defaultCabinetFiltering("rotationDeg.specified=true", "rotationDeg.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg is greater than or equal to
        defaultCabinetFiltering(
            "rotationDeg.greaterThanOrEqual=" + DEFAULT_ROTATION_DEG,
            "rotationDeg.greaterThanOrEqual=" + UPDATED_ROTATION_DEG
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg is less than or equal to
        defaultCabinetFiltering(
            "rotationDeg.lessThanOrEqual=" + DEFAULT_ROTATION_DEG,
            "rotationDeg.lessThanOrEqual=" + SMALLER_ROTATION_DEG
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg is less than
        defaultCabinetFiltering("rotationDeg.lessThan=" + UPDATED_ROTATION_DEG, "rotationDeg.lessThan=" + DEFAULT_ROTATION_DEG);
    }

    @Test
    @Transactional
    void getAllCabinetsByRotationDegIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where rotationDeg is greater than
        defaultCabinetFiltering("rotationDeg.greaterThan=" + SMALLER_ROTATION_DEG, "rotationDeg.greaterThan=" + DEFAULT_ROTATION_DEG);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq equals to
        defaultCabinetFiltering("positionSeq.equals=" + DEFAULT_POSITION_SEQ, "positionSeq.equals=" + UPDATED_POSITION_SEQ);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq in
        defaultCabinetFiltering(
            "positionSeq.in=" + DEFAULT_POSITION_SEQ + "," + UPDATED_POSITION_SEQ,
            "positionSeq.in=" + UPDATED_POSITION_SEQ
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq is not null
        defaultCabinetFiltering("positionSeq.specified=true", "positionSeq.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq is greater than or equal to
        defaultCabinetFiltering(
            "positionSeq.greaterThanOrEqual=" + DEFAULT_POSITION_SEQ,
            "positionSeq.greaterThanOrEqual=" + UPDATED_POSITION_SEQ
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq is less than or equal to
        defaultCabinetFiltering(
            "positionSeq.lessThanOrEqual=" + DEFAULT_POSITION_SEQ,
            "positionSeq.lessThanOrEqual=" + SMALLER_POSITION_SEQ
        );
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq is less than
        defaultCabinetFiltering("positionSeq.lessThan=" + UPDATED_POSITION_SEQ, "positionSeq.lessThan=" + DEFAULT_POSITION_SEQ);
    }

    @Test
    @Transactional
    void getAllCabinetsByPositionSeqIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where positionSeq is greater than
        defaultCabinetFiltering("positionSeq.greaterThan=" + SMALLER_POSITION_SEQ, "positionSeq.greaterThan=" + DEFAULT_POSITION_SEQ);
    }

    @Test
    @Transactional
    void getAllCabinetsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where notes equals to
        defaultCabinetFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where notes in
        defaultCabinetFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where notes is not null
        defaultCabinetFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where notes contains
        defaultCabinetFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        // Get all the cabinetList where notes does not contain
        defaultCabinetFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetsByTemplateIsEqualToSomething() throws Exception {
        CabinetTemplate template;
        if (TestUtil.findAll(em, CabinetTemplate.class).isEmpty()) {
            cabinetRepository.saveAndFlush(cabinet);
            template = CabinetTemplateResourceIT.createEntity();
        } else {
            template = TestUtil.findAll(em, CabinetTemplate.class).get(0);
        }
        em.persist(template);
        em.flush();
        cabinet.setTemplate(template);
        cabinetRepository.saveAndFlush(cabinet);
        Long templateId = template.getId();
        // Get all the cabinetList where template equals to templateId
        defaultCabinetShouldBeFound("templateId.equals=" + templateId);

        // Get all the cabinetList where template equals to (templateId + 1)
        defaultCabinetShouldNotBeFound("templateId.equals=" + (templateId + 1));
    }

    @Test
    @Transactional
    void getAllCabinetsByMaterialIsEqualToSomething() throws Exception {
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            cabinetRepository.saveAndFlush(cabinet);
            material = MaterialResourceIT.createEntity();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        em.persist(material);
        em.flush();
        cabinet.setMaterial(material);
        cabinetRepository.saveAndFlush(cabinet);
        Long materialId = material.getId();
        // Get all the cabinetList where material equals to materialId
        defaultCabinetShouldBeFound("materialId.equals=" + materialId);

        // Get all the cabinetList where material equals to (materialId + 1)
        defaultCabinetShouldNotBeFound("materialId.equals=" + (materialId + 1));
    }

    @Test
    @Transactional
    void getAllCabinetsBySpecIsEqualToSomething() throws Exception {
        KitchenSpec spec;
        if (TestUtil.findAll(em, KitchenSpec.class).isEmpty()) {
            cabinetRepository.saveAndFlush(cabinet);
            spec = KitchenSpecResourceIT.createEntity(em);
        } else {
            spec = TestUtil.findAll(em, KitchenSpec.class).get(0);
        }
        em.persist(spec);
        em.flush();
        cabinet.setSpec(spec);
        cabinetRepository.saveAndFlush(cabinet);
        Long specId = spec.getId();
        // Get all the cabinetList where spec equals to specId
        defaultCabinetShouldBeFound("specId.equals=" + specId);

        // Get all the cabinetList where spec equals to (specId + 1)
        defaultCabinetShouldNotBeFound("specId.equals=" + (specId + 1));
    }

    private void defaultCabinetFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCabinetShouldBeFound(shouldBeFound);
        defaultCabinetShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCabinetShouldBeFound(String filter) throws Exception {
        restCabinetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cabinet.getId().intValue())))
            .andExpect(jsonPath("$.[*].cabinetCode").value(hasItem(DEFAULT_CABINET_CODE)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].widthMm").value(hasItem(DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].heightMm").value(hasItem(DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].depthMm").value(hasItem(DEFAULT_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].doors").value(hasItem(DEFAULT_DOORS)))
            .andExpect(jsonPath("$.[*].drawers").value(hasItem(DEFAULT_DRAWERS)))
            .andExpect(jsonPath("$.[*].shelves").value(hasItem(DEFAULT_SHELVES)))
            .andExpect(jsonPath("$.[*].finish").value(hasItem(DEFAULT_FINISH.toString())))
            .andExpect(jsonPath("$.[*].positionX").value(hasItem(DEFAULT_POSITION_X)))
            .andExpect(jsonPath("$.[*].positionY").value(hasItem(DEFAULT_POSITION_Y)))
            .andExpect(jsonPath("$.[*].positionZ").value(hasItem(DEFAULT_POSITION_Z)))
            .andExpect(jsonPath("$.[*].rotationDeg").value(hasItem(DEFAULT_ROTATION_DEG)))
            .andExpect(jsonPath("$.[*].positionSeq").value(hasItem(DEFAULT_POSITION_SEQ)))
            .andExpect(jsonPath("$.[*].csvRowJson").value(hasItem(DEFAULT_CSV_ROW_JSON)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restCabinetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCabinetShouldNotBeFound(String filter) throws Exception {
        restCabinetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCabinetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCabinet() throws Exception {
        // Get the cabinet
        restCabinetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCabinet() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinet
        Cabinet updatedCabinet = cabinetRepository.findById(cabinet.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCabinet are not directly saved in db
        em.detach(updatedCabinet);
        updatedCabinet
            .cabinetCode(UPDATED_CABINET_CODE)
            .category(UPDATED_CATEGORY)
            .label(UPDATED_LABEL)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .doors(UPDATED_DOORS)
            .drawers(UPDATED_DRAWERS)
            .shelves(UPDATED_SHELVES)
            .finish(UPDATED_FINISH)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .positionZ(UPDATED_POSITION_Z)
            .rotationDeg(UPDATED_ROTATION_DEG)
            .positionSeq(UPDATED_POSITION_SEQ)
            .csvRowJson(UPDATED_CSV_ROW_JSON)
            .notes(UPDATED_NOTES);
        CabinetDTO cabinetDTO = cabinetMapper.toDto(updatedCabinet);

        restCabinetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cabinetDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCabinetToMatchAllProperties(updatedCabinet);
    }

    @Test
    @Transactional
    void putNonExistingCabinet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinet.setId(longCount.incrementAndGet());

        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCabinetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cabinetDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCabinet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinet.setId(longCount.incrementAndGet());

        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCabinet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinet.setId(longCount.incrementAndGet());

        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCabinetWithPatch() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinet using partial update
        Cabinet partialUpdatedCabinet = new Cabinet();
        partialUpdatedCabinet.setId(cabinet.getId());

        partialUpdatedCabinet
            .cabinetCode(UPDATED_CABINET_CODE)
            .category(UPDATED_CATEGORY)
            .label(UPDATED_LABEL)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .finish(UPDATED_FINISH)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .positionSeq(UPDATED_POSITION_SEQ)
            .csvRowJson(UPDATED_CSV_ROW_JSON);

        restCabinetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCabinet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCabinet))
            )
            .andExpect(status().isOk());

        // Validate the Cabinet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCabinetUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCabinet, cabinet), getPersistedCabinet(cabinet));
    }

    @Test
    @Transactional
    void fullUpdateCabinetWithPatch() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinet using partial update
        Cabinet partialUpdatedCabinet = new Cabinet();
        partialUpdatedCabinet.setId(cabinet.getId());

        partialUpdatedCabinet
            .cabinetCode(UPDATED_CABINET_CODE)
            .category(UPDATED_CATEGORY)
            .label(UPDATED_LABEL)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .depthMm(UPDATED_DEPTH_MM)
            .doors(UPDATED_DOORS)
            .drawers(UPDATED_DRAWERS)
            .shelves(UPDATED_SHELVES)
            .finish(UPDATED_FINISH)
            .positionX(UPDATED_POSITION_X)
            .positionY(UPDATED_POSITION_Y)
            .positionZ(UPDATED_POSITION_Z)
            .rotationDeg(UPDATED_ROTATION_DEG)
            .positionSeq(UPDATED_POSITION_SEQ)
            .csvRowJson(UPDATED_CSV_ROW_JSON)
            .notes(UPDATED_NOTES);

        restCabinetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCabinet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCabinet))
            )
            .andExpect(status().isOk());

        // Validate the Cabinet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCabinetUpdatableFieldsEquals(partialUpdatedCabinet, getPersistedCabinet(partialUpdatedCabinet));
    }

    @Test
    @Transactional
    void patchNonExistingCabinet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinet.setId(longCount.incrementAndGet());

        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCabinetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cabinetDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cabinetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCabinet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinet.setId(longCount.incrementAndGet());

        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cabinetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCabinet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinet.setId(longCount.incrementAndGet());

        // Create the Cabinet
        CabinetDTO cabinetDTO = cabinetMapper.toDto(cabinet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cabinetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cabinet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCabinet() throws Exception {
        // Initialize the database
        insertedCabinet = cabinetRepository.saveAndFlush(cabinet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cabinet
        restCabinetMockMvc
            .perform(delete(ENTITY_API_URL_ID, cabinet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cabinetRepository.count();
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

    protected Cabinet getPersistedCabinet(Cabinet cabinet) {
        return cabinetRepository.findById(cabinet.getId()).orElseThrow();
    }

    protected void assertPersistedCabinetToMatchAllProperties(Cabinet expectedCabinet) {
        assertCabinetAllPropertiesEquals(expectedCabinet, getPersistedCabinet(expectedCabinet));
    }

    protected void assertPersistedCabinetToMatchUpdatableProperties(Cabinet expectedCabinet) {
        assertCabinetAllUpdatablePropertiesEquals(expectedCabinet, getPersistedCabinet(expectedCabinet));
    }
}
