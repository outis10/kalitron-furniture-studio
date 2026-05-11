package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.CabinetPartAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.domain.CabinetPart;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.repository.CabinetPartRepository;
import com.kalitron.studio.service.CabinetPartService;
import com.kalitron.studio.service.dto.CabinetPartDTO;
import com.kalitron.studio.service.mapper.CabinetPartMapper;
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
 * Integration tests for the {@link CabinetPartResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CabinetPartResourceIT {

    private static final String DEFAULT_PART_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PART_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_WIDTH_MM = 1;
    private static final Integer UPDATED_WIDTH_MM = 2;
    private static final Integer SMALLER_WIDTH_MM = 1 - 1;

    private static final Integer DEFAULT_HEIGHT_MM = 1;
    private static final Integer UPDATED_HEIGHT_MM = 2;
    private static final Integer SMALLER_HEIGHT_MM = 1 - 1;

    private static final Integer DEFAULT_THICKNESS_MM = 1;
    private static final Integer UPDATED_THICKNESS_MM = 2;
    private static final Integer SMALLER_THICKNESS_MM = 1 - 1;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String DEFAULT_EDGE_BANDING = "AAAAAAAAAA";
    private static final String UPDATED_EDGE_BANDING = "BBBBBBBBBB";

    private static final String DEFAULT_GRAIN_DIRECTION = "AAAAAAAAAA";
    private static final String UPDATED_GRAIN_DIRECTION = "BBBBBBBBBB";

    private static final String DEFAULT_CNC_OPERATION = "AAAAAAAAAA";
    private static final String UPDATED_CNC_OPERATION = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cabinet-parts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CabinetPartRepository cabinetPartRepository;

    @Mock
    private CabinetPartRepository cabinetPartRepositoryMock;

    @Autowired
    private CabinetPartMapper cabinetPartMapper;

    @Mock
    private CabinetPartService cabinetPartServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCabinetPartMockMvc;

    private CabinetPart cabinetPart;

    private CabinetPart insertedCabinetPart;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CabinetPart createEntity(EntityManager em) {
        CabinetPart cabinetPart = new CabinetPart()
            .partCode(DEFAULT_PART_CODE)
            .name(DEFAULT_NAME)
            .widthMm(DEFAULT_WIDTH_MM)
            .heightMm(DEFAULT_HEIGHT_MM)
            .thicknessMm(DEFAULT_THICKNESS_MM)
            .quantity(DEFAULT_QUANTITY)
            .edgeBanding(DEFAULT_EDGE_BANDING)
            .grainDirection(DEFAULT_GRAIN_DIRECTION)
            .cncOperation(DEFAULT_CNC_OPERATION)
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
        cabinetPart.setMaterial(material);
        // Add required entity
        Cabinet cabinet;
        if (TestUtil.findAll(em, Cabinet.class).isEmpty()) {
            cabinet = CabinetResourceIT.createEntity(em);
            em.persist(cabinet);
            em.flush();
        } else {
            cabinet = TestUtil.findAll(em, Cabinet.class).get(0);
        }
        cabinetPart.setCabinet(cabinet);
        return cabinetPart;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CabinetPart createUpdatedEntity(EntityManager em) {
        CabinetPart updatedCabinetPart = new CabinetPart()
            .partCode(UPDATED_PART_CODE)
            .name(UPDATED_NAME)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .quantity(UPDATED_QUANTITY)
            .edgeBanding(UPDATED_EDGE_BANDING)
            .grainDirection(UPDATED_GRAIN_DIRECTION)
            .cncOperation(UPDATED_CNC_OPERATION)
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
        updatedCabinetPart.setMaterial(material);
        // Add required entity
        Cabinet cabinet;
        if (TestUtil.findAll(em, Cabinet.class).isEmpty()) {
            cabinet = CabinetResourceIT.createUpdatedEntity(em);
            em.persist(cabinet);
            em.flush();
        } else {
            cabinet = TestUtil.findAll(em, Cabinet.class).get(0);
        }
        updatedCabinetPart.setCabinet(cabinet);
        return updatedCabinetPart;
    }

    @BeforeEach
    void initTest() {
        cabinetPart = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCabinetPart != null) {
            cabinetPartRepository.delete(insertedCabinetPart);
            insertedCabinetPart = null;
        }
    }

    @Test
    @Transactional
    void createCabinetPart() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);
        var returnedCabinetPartDTO = om.readValue(
            restCabinetPartMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CabinetPartDTO.class
        );

        // Validate the CabinetPart in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCabinetPart = cabinetPartMapper.toEntity(returnedCabinetPartDTO);
        assertCabinetPartUpdatableFieldsEquals(returnedCabinetPart, getPersistedCabinetPart(returnedCabinetPart));

        insertedCabinetPart = returnedCabinetPart;
    }

    @Test
    @Transactional
    void createCabinetPartWithExistingId() throws Exception {
        // Create the CabinetPart with an existing ID
        cabinetPart.setId(1L);
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPartCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetPart.setPartCode(null);

        // Create the CabinetPart, which fails.
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetPart.setName(null);

        // Create the CabinetPart, which fails.
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWidthMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetPart.setWidthMm(null);

        // Create the CabinetPart, which fails.
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHeightMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetPart.setHeightMm(null);

        // Create the CabinetPart, which fails.
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkThicknessMmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetPart.setThicknessMm(null);

        // Create the CabinetPart, which fails.
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetPart.setQuantity(null);

        // Create the CabinetPart, which fails.
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        restCabinetPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCabinetParts() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList
        restCabinetPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cabinetPart.getId().intValue())))
            .andExpect(jsonPath("$.[*].partCode").value(hasItem(DEFAULT_PART_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].widthMm").value(hasItem(DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].heightMm").value(hasItem(DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].thicknessMm").value(hasItem(DEFAULT_THICKNESS_MM)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].edgeBanding").value(hasItem(DEFAULT_EDGE_BANDING)))
            .andExpect(jsonPath("$.[*].grainDirection").value(hasItem(DEFAULT_GRAIN_DIRECTION)))
            .andExpect(jsonPath("$.[*].cncOperation").value(hasItem(DEFAULT_CNC_OPERATION)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCabinetPartsWithEagerRelationshipsIsEnabled() throws Exception {
        when(cabinetPartServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCabinetPartMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(cabinetPartServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCabinetPartsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cabinetPartServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCabinetPartMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(cabinetPartRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCabinetPart() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get the cabinetPart
        restCabinetPartMockMvc
            .perform(get(ENTITY_API_URL_ID, cabinetPart.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cabinetPart.getId().intValue()))
            .andExpect(jsonPath("$.partCode").value(DEFAULT_PART_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.widthMm").value(DEFAULT_WIDTH_MM))
            .andExpect(jsonPath("$.heightMm").value(DEFAULT_HEIGHT_MM))
            .andExpect(jsonPath("$.thicknessMm").value(DEFAULT_THICKNESS_MM))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.edgeBanding").value(DEFAULT_EDGE_BANDING))
            .andExpect(jsonPath("$.grainDirection").value(DEFAULT_GRAIN_DIRECTION))
            .andExpect(jsonPath("$.cncOperation").value(DEFAULT_CNC_OPERATION))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getCabinetPartsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        Long id = cabinetPart.getId();

        defaultCabinetPartFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCabinetPartFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCabinetPartFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByPartCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where partCode equals to
        defaultCabinetPartFiltering("partCode.equals=" + DEFAULT_PART_CODE, "partCode.equals=" + UPDATED_PART_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByPartCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where partCode in
        defaultCabinetPartFiltering("partCode.in=" + DEFAULT_PART_CODE + "," + UPDATED_PART_CODE, "partCode.in=" + UPDATED_PART_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByPartCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where partCode is not null
        defaultCabinetPartFiltering("partCode.specified=true", "partCode.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByPartCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where partCode contains
        defaultCabinetPartFiltering("partCode.contains=" + DEFAULT_PART_CODE, "partCode.contains=" + UPDATED_PART_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByPartCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where partCode does not contain
        defaultCabinetPartFiltering("partCode.doesNotContain=" + UPDATED_PART_CODE, "partCode.doesNotContain=" + DEFAULT_PART_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where name equals to
        defaultCabinetPartFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where name in
        defaultCabinetPartFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where name is not null
        defaultCabinetPartFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where name contains
        defaultCabinetPartFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where name does not contain
        defaultCabinetPartFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm equals to
        defaultCabinetPartFiltering("widthMm.equals=" + DEFAULT_WIDTH_MM, "widthMm.equals=" + UPDATED_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm in
        defaultCabinetPartFiltering("widthMm.in=" + DEFAULT_WIDTH_MM + "," + UPDATED_WIDTH_MM, "widthMm.in=" + UPDATED_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm is not null
        defaultCabinetPartFiltering("widthMm.specified=true", "widthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm is greater than or equal to
        defaultCabinetPartFiltering("widthMm.greaterThanOrEqual=" + DEFAULT_WIDTH_MM, "widthMm.greaterThanOrEqual=" + UPDATED_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm is less than or equal to
        defaultCabinetPartFiltering("widthMm.lessThanOrEqual=" + DEFAULT_WIDTH_MM, "widthMm.lessThanOrEqual=" + SMALLER_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm is less than
        defaultCabinetPartFiltering("widthMm.lessThan=" + UPDATED_WIDTH_MM, "widthMm.lessThan=" + DEFAULT_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where widthMm is greater than
        defaultCabinetPartFiltering("widthMm.greaterThan=" + SMALLER_WIDTH_MM, "widthMm.greaterThan=" + DEFAULT_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm equals to
        defaultCabinetPartFiltering("heightMm.equals=" + DEFAULT_HEIGHT_MM, "heightMm.equals=" + UPDATED_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm in
        defaultCabinetPartFiltering("heightMm.in=" + DEFAULT_HEIGHT_MM + "," + UPDATED_HEIGHT_MM, "heightMm.in=" + UPDATED_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm is not null
        defaultCabinetPartFiltering("heightMm.specified=true", "heightMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm is greater than or equal to
        defaultCabinetPartFiltering("heightMm.greaterThanOrEqual=" + DEFAULT_HEIGHT_MM, "heightMm.greaterThanOrEqual=" + UPDATED_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm is less than or equal to
        defaultCabinetPartFiltering("heightMm.lessThanOrEqual=" + DEFAULT_HEIGHT_MM, "heightMm.lessThanOrEqual=" + SMALLER_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm is less than
        defaultCabinetPartFiltering("heightMm.lessThan=" + UPDATED_HEIGHT_MM, "heightMm.lessThan=" + DEFAULT_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByHeightMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where heightMm is greater than
        defaultCabinetPartFiltering("heightMm.greaterThan=" + SMALLER_HEIGHT_MM, "heightMm.greaterThan=" + DEFAULT_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm equals to
        defaultCabinetPartFiltering("thicknessMm.equals=" + DEFAULT_THICKNESS_MM, "thicknessMm.equals=" + UPDATED_THICKNESS_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm in
        defaultCabinetPartFiltering(
            "thicknessMm.in=" + DEFAULT_THICKNESS_MM + "," + UPDATED_THICKNESS_MM,
            "thicknessMm.in=" + UPDATED_THICKNESS_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm is not null
        defaultCabinetPartFiltering("thicknessMm.specified=true", "thicknessMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm is greater than or equal to
        defaultCabinetPartFiltering(
            "thicknessMm.greaterThanOrEqual=" + DEFAULT_THICKNESS_MM,
            "thicknessMm.greaterThanOrEqual=" + UPDATED_THICKNESS_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm is less than or equal to
        defaultCabinetPartFiltering(
            "thicknessMm.lessThanOrEqual=" + DEFAULT_THICKNESS_MM,
            "thicknessMm.lessThanOrEqual=" + SMALLER_THICKNESS_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm is less than
        defaultCabinetPartFiltering("thicknessMm.lessThan=" + UPDATED_THICKNESS_MM, "thicknessMm.lessThan=" + DEFAULT_THICKNESS_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByThicknessMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where thicknessMm is greater than
        defaultCabinetPartFiltering("thicknessMm.greaterThan=" + SMALLER_THICKNESS_MM, "thicknessMm.greaterThan=" + DEFAULT_THICKNESS_MM);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity equals to
        defaultCabinetPartFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity in
        defaultCabinetPartFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity is not null
        defaultCabinetPartFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity is greater than or equal to
        defaultCabinetPartFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity is less than or equal to
        defaultCabinetPartFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity is less than
        defaultCabinetPartFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where quantity is greater than
        defaultCabinetPartFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByEdgeBandingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where edgeBanding equals to
        defaultCabinetPartFiltering("edgeBanding.equals=" + DEFAULT_EDGE_BANDING, "edgeBanding.equals=" + UPDATED_EDGE_BANDING);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByEdgeBandingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where edgeBanding in
        defaultCabinetPartFiltering(
            "edgeBanding.in=" + DEFAULT_EDGE_BANDING + "," + UPDATED_EDGE_BANDING,
            "edgeBanding.in=" + UPDATED_EDGE_BANDING
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByEdgeBandingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where edgeBanding is not null
        defaultCabinetPartFiltering("edgeBanding.specified=true", "edgeBanding.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByEdgeBandingContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where edgeBanding contains
        defaultCabinetPartFiltering("edgeBanding.contains=" + DEFAULT_EDGE_BANDING, "edgeBanding.contains=" + UPDATED_EDGE_BANDING);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByEdgeBandingNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where edgeBanding does not contain
        defaultCabinetPartFiltering(
            "edgeBanding.doesNotContain=" + UPDATED_EDGE_BANDING,
            "edgeBanding.doesNotContain=" + DEFAULT_EDGE_BANDING
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByGrainDirectionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where grainDirection equals to
        defaultCabinetPartFiltering("grainDirection.equals=" + DEFAULT_GRAIN_DIRECTION, "grainDirection.equals=" + UPDATED_GRAIN_DIRECTION);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByGrainDirectionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where grainDirection in
        defaultCabinetPartFiltering(
            "grainDirection.in=" + DEFAULT_GRAIN_DIRECTION + "," + UPDATED_GRAIN_DIRECTION,
            "grainDirection.in=" + UPDATED_GRAIN_DIRECTION
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByGrainDirectionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where grainDirection is not null
        defaultCabinetPartFiltering("grainDirection.specified=true", "grainDirection.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByGrainDirectionContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where grainDirection contains
        defaultCabinetPartFiltering(
            "grainDirection.contains=" + DEFAULT_GRAIN_DIRECTION,
            "grainDirection.contains=" + UPDATED_GRAIN_DIRECTION
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByGrainDirectionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where grainDirection does not contain
        defaultCabinetPartFiltering(
            "grainDirection.doesNotContain=" + UPDATED_GRAIN_DIRECTION,
            "grainDirection.doesNotContain=" + DEFAULT_GRAIN_DIRECTION
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByCncOperationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where cncOperation equals to
        defaultCabinetPartFiltering("cncOperation.equals=" + DEFAULT_CNC_OPERATION, "cncOperation.equals=" + UPDATED_CNC_OPERATION);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByCncOperationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where cncOperation in
        defaultCabinetPartFiltering(
            "cncOperation.in=" + DEFAULT_CNC_OPERATION + "," + UPDATED_CNC_OPERATION,
            "cncOperation.in=" + UPDATED_CNC_OPERATION
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByCncOperationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where cncOperation is not null
        defaultCabinetPartFiltering("cncOperation.specified=true", "cncOperation.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByCncOperationContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where cncOperation contains
        defaultCabinetPartFiltering("cncOperation.contains=" + DEFAULT_CNC_OPERATION, "cncOperation.contains=" + UPDATED_CNC_OPERATION);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByCncOperationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where cncOperation does not contain
        defaultCabinetPartFiltering(
            "cncOperation.doesNotContain=" + UPDATED_CNC_OPERATION,
            "cncOperation.doesNotContain=" + DEFAULT_CNC_OPERATION
        );
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where notes equals to
        defaultCabinetPartFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where notes in
        defaultCabinetPartFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where notes is not null
        defaultCabinetPartFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where notes contains
        defaultCabinetPartFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        // Get all the cabinetPartList where notes does not contain
        defaultCabinetPartFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllCabinetPartsByMaterialIsEqualToSomething() throws Exception {
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            cabinetPartRepository.saveAndFlush(cabinetPart);
            material = MaterialResourceIT.createEntity();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        em.persist(material);
        em.flush();
        cabinetPart.setMaterial(material);
        cabinetPartRepository.saveAndFlush(cabinetPart);
        Long materialId = material.getId();
        // Get all the cabinetPartList where material equals to materialId
        defaultCabinetPartShouldBeFound("materialId.equals=" + materialId);

        // Get all the cabinetPartList where material equals to (materialId + 1)
        defaultCabinetPartShouldNotBeFound("materialId.equals=" + (materialId + 1));
    }

    @Test
    @Transactional
    void getAllCabinetPartsByCabinetIsEqualToSomething() throws Exception {
        Cabinet cabinet;
        if (TestUtil.findAll(em, Cabinet.class).isEmpty()) {
            cabinetPartRepository.saveAndFlush(cabinetPart);
            cabinet = CabinetResourceIT.createEntity(em);
        } else {
            cabinet = TestUtil.findAll(em, Cabinet.class).get(0);
        }
        em.persist(cabinet);
        em.flush();
        cabinetPart.setCabinet(cabinet);
        cabinetPartRepository.saveAndFlush(cabinetPart);
        Long cabinetId = cabinet.getId();
        // Get all the cabinetPartList where cabinet equals to cabinetId
        defaultCabinetPartShouldBeFound("cabinetId.equals=" + cabinetId);

        // Get all the cabinetPartList where cabinet equals to (cabinetId + 1)
        defaultCabinetPartShouldNotBeFound("cabinetId.equals=" + (cabinetId + 1));
    }

    private void defaultCabinetPartFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCabinetPartShouldBeFound(shouldBeFound);
        defaultCabinetPartShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCabinetPartShouldBeFound(String filter) throws Exception {
        restCabinetPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cabinetPart.getId().intValue())))
            .andExpect(jsonPath("$.[*].partCode").value(hasItem(DEFAULT_PART_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].widthMm").value(hasItem(DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].heightMm").value(hasItem(DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].thicknessMm").value(hasItem(DEFAULT_THICKNESS_MM)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].edgeBanding").value(hasItem(DEFAULT_EDGE_BANDING)))
            .andExpect(jsonPath("$.[*].grainDirection").value(hasItem(DEFAULT_GRAIN_DIRECTION)))
            .andExpect(jsonPath("$.[*].cncOperation").value(hasItem(DEFAULT_CNC_OPERATION)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restCabinetPartMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCabinetPartShouldNotBeFound(String filter) throws Exception {
        restCabinetPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCabinetPartMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCabinetPart() throws Exception {
        // Get the cabinetPart
        restCabinetPartMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCabinetPart() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinetPart
        CabinetPart updatedCabinetPart = cabinetPartRepository.findById(cabinetPart.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCabinetPart are not directly saved in db
        em.detach(updatedCabinetPart);
        updatedCabinetPart
            .partCode(UPDATED_PART_CODE)
            .name(UPDATED_NAME)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .quantity(UPDATED_QUANTITY)
            .edgeBanding(UPDATED_EDGE_BANDING)
            .grainDirection(UPDATED_GRAIN_DIRECTION)
            .cncOperation(UPDATED_CNC_OPERATION)
            .notes(UPDATED_NOTES);
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(updatedCabinetPart);

        restCabinetPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cabinetPartDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetPartDTO))
            )
            .andExpect(status().isOk());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCabinetPartToMatchAllProperties(updatedCabinetPart);
    }

    @Test
    @Transactional
    void putNonExistingCabinetPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetPart.setId(longCount.incrementAndGet());

        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCabinetPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cabinetPartDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetPartDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCabinetPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetPart.setId(longCount.incrementAndGet());

        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetPartDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCabinetPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetPart.setId(longCount.incrementAndGet());

        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetPartMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCabinetPartWithPatch() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinetPart using partial update
        CabinetPart partialUpdatedCabinetPart = new CabinetPart();
        partialUpdatedCabinetPart.setId(cabinetPart.getId());

        partialUpdatedCabinetPart
            .name(UPDATED_NAME)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .quantity(UPDATED_QUANTITY)
            .grainDirection(UPDATED_GRAIN_DIRECTION)
            .cncOperation(UPDATED_CNC_OPERATION)
            .notes(UPDATED_NOTES);

        restCabinetPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCabinetPart.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCabinetPart))
            )
            .andExpect(status().isOk());

        // Validate the CabinetPart in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCabinetPartUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCabinetPart, cabinetPart),
            getPersistedCabinetPart(cabinetPart)
        );
    }

    @Test
    @Transactional
    void fullUpdateCabinetPartWithPatch() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinetPart using partial update
        CabinetPart partialUpdatedCabinetPart = new CabinetPart();
        partialUpdatedCabinetPart.setId(cabinetPart.getId());

        partialUpdatedCabinetPart
            .partCode(UPDATED_PART_CODE)
            .name(UPDATED_NAME)
            .widthMm(UPDATED_WIDTH_MM)
            .heightMm(UPDATED_HEIGHT_MM)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .quantity(UPDATED_QUANTITY)
            .edgeBanding(UPDATED_EDGE_BANDING)
            .grainDirection(UPDATED_GRAIN_DIRECTION)
            .cncOperation(UPDATED_CNC_OPERATION)
            .notes(UPDATED_NOTES);

        restCabinetPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCabinetPart.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCabinetPart))
            )
            .andExpect(status().isOk());

        // Validate the CabinetPart in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCabinetPartUpdatableFieldsEquals(partialUpdatedCabinetPart, getPersistedCabinetPart(partialUpdatedCabinetPart));
    }

    @Test
    @Transactional
    void patchNonExistingCabinetPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetPart.setId(longCount.incrementAndGet());

        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCabinetPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cabinetPartDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cabinetPartDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCabinetPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetPart.setId(longCount.incrementAndGet());

        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cabinetPartDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCabinetPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetPart.setId(longCount.incrementAndGet());

        // Create the CabinetPart
        CabinetPartDTO cabinetPartDTO = cabinetPartMapper.toDto(cabinetPart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetPartMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cabinetPartDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CabinetPart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCabinetPart() throws Exception {
        // Initialize the database
        insertedCabinetPart = cabinetPartRepository.saveAndFlush(cabinetPart);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cabinetPart
        restCabinetPartMockMvc
            .perform(delete(ENTITY_API_URL_ID, cabinetPart.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cabinetPartRepository.count();
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

    protected CabinetPart getPersistedCabinetPart(CabinetPart cabinetPart) {
        return cabinetPartRepository.findById(cabinetPart.getId()).orElseThrow();
    }

    protected void assertPersistedCabinetPartToMatchAllProperties(CabinetPart expectedCabinetPart) {
        assertCabinetPartAllPropertiesEquals(expectedCabinetPart, getPersistedCabinetPart(expectedCabinetPart));
    }

    protected void assertPersistedCabinetPartToMatchUpdatableProperties(CabinetPart expectedCabinetPart) {
        assertCabinetPartAllUpdatablePropertiesEquals(expectedCabinetPart, getPersistedCabinetPart(expectedCabinetPart));
    }
}
