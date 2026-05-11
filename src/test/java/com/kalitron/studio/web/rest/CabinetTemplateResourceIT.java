package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.CabinetTemplateAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.repository.CabinetTemplateRepository;
import com.kalitron.studio.service.dto.CabinetTemplateDTO;
import com.kalitron.studio.service.mapper.CabinetTemplateMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CabinetTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CabinetTemplateResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final CabinetCategory DEFAULT_CATEGORY = CabinetCategory.UPPER;
    private static final CabinetCategory UPDATED_CATEGORY = CabinetCategory.LOWER;

    private static final Integer DEFAULT_DEFAULT_WIDTH_MM = 1;
    private static final Integer UPDATED_DEFAULT_WIDTH_MM = 2;
    private static final Integer SMALLER_DEFAULT_WIDTH_MM = 1 - 1;

    private static final Integer DEFAULT_DEFAULT_HEIGHT_MM = 1;
    private static final Integer UPDATED_DEFAULT_HEIGHT_MM = 2;
    private static final Integer SMALLER_DEFAULT_HEIGHT_MM = 1 - 1;

    private static final Integer DEFAULT_DEFAULT_DEPTH_MM = 1;
    private static final Integer UPDATED_DEFAULT_DEPTH_MM = 2;
    private static final Integer SMALLER_DEFAULT_DEPTH_MM = 1 - 1;

    private static final Integer DEFAULT_MIN_WIDTH_MM = 1;
    private static final Integer UPDATED_MIN_WIDTH_MM = 2;
    private static final Integer SMALLER_MIN_WIDTH_MM = 1 - 1;

    private static final Integer DEFAULT_MAX_WIDTH_MM = 1;
    private static final Integer UPDATED_MAX_WIDTH_MM = 2;
    private static final Integer SMALLER_MAX_WIDTH_MM = 1 - 1;

    private static final Boolean DEFAULT_SUPPORTS_DOORS = false;
    private static final Boolean UPDATED_SUPPORTS_DOORS = true;

    private static final Boolean DEFAULT_SUPPORTS_DRAWERS = false;
    private static final Boolean UPDATED_SUPPORTS_DRAWERS = true;

    private static final Boolean DEFAULT_SUPPORTS_SHELVES = false;
    private static final Boolean UPDATED_SUPPORTS_SHELVES = true;

    private static final String DEFAULT_FUSION_TEMPLATE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FUSION_TEMPLATE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CSV_PROFILE_JSON = "AAAAAAAAAA";
    private static final String UPDATED_CSV_PROFILE_JSON = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;
    private static final Integer SMALLER_SORT_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/cabinet-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CabinetTemplateRepository cabinetTemplateRepository;

    @Autowired
    private CabinetTemplateMapper cabinetTemplateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCabinetTemplateMockMvc;

    private CabinetTemplate cabinetTemplate;

    private CabinetTemplate insertedCabinetTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CabinetTemplate createEntity() {
        return new CabinetTemplate()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .category(DEFAULT_CATEGORY)
            .defaultWidthMm(DEFAULT_DEFAULT_WIDTH_MM)
            .defaultHeightMm(DEFAULT_DEFAULT_HEIGHT_MM)
            .defaultDepthMm(DEFAULT_DEFAULT_DEPTH_MM)
            .minWidthMm(DEFAULT_MIN_WIDTH_MM)
            .maxWidthMm(DEFAULT_MAX_WIDTH_MM)
            .supportsDoors(DEFAULT_SUPPORTS_DOORS)
            .supportsDrawers(DEFAULT_SUPPORTS_DRAWERS)
            .supportsShelves(DEFAULT_SUPPORTS_SHELVES)
            .fusionTemplateName(DEFAULT_FUSION_TEMPLATE_NAME)
            .csvProfileJson(DEFAULT_CSV_PROFILE_JSON)
            .isActive(DEFAULT_IS_ACTIVE)
            .sortOrder(DEFAULT_SORT_ORDER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CabinetTemplate createUpdatedEntity() {
        return new CabinetTemplate()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultWidthMm(UPDATED_DEFAULT_WIDTH_MM)
            .defaultHeightMm(UPDATED_DEFAULT_HEIGHT_MM)
            .defaultDepthMm(UPDATED_DEFAULT_DEPTH_MM)
            .minWidthMm(UPDATED_MIN_WIDTH_MM)
            .maxWidthMm(UPDATED_MAX_WIDTH_MM)
            .supportsDoors(UPDATED_SUPPORTS_DOORS)
            .supportsDrawers(UPDATED_SUPPORTS_DRAWERS)
            .supportsShelves(UPDATED_SUPPORTS_SHELVES)
            .fusionTemplateName(UPDATED_FUSION_TEMPLATE_NAME)
            .csvProfileJson(UPDATED_CSV_PROFILE_JSON)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);
    }

    @BeforeEach
    void initTest() {
        cabinetTemplate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCabinetTemplate != null) {
            cabinetTemplateRepository.delete(insertedCabinetTemplate);
            insertedCabinetTemplate = null;
        }
    }

    @Test
    @Transactional
    void createCabinetTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);
        var returnedCabinetTemplateDTO = om.readValue(
            restCabinetTemplateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CabinetTemplateDTO.class
        );

        // Validate the CabinetTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCabinetTemplate = cabinetTemplateMapper.toEntity(returnedCabinetTemplateDTO);
        assertCabinetTemplateUpdatableFieldsEquals(returnedCabinetTemplate, getPersistedCabinetTemplate(returnedCabinetTemplate));

        insertedCabinetTemplate = returnedCabinetTemplate;
    }

    @Test
    @Transactional
    void createCabinetTemplateWithExistingId() throws Exception {
        // Create the CabinetTemplate with an existing ID
        cabinetTemplate.setId(1L);
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setCode(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setName(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setCategory(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSupportsDoorsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setSupportsDoors(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSupportsDrawersIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setSupportsDrawers(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSupportsShelvesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setSupportsShelves(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cabinetTemplate.setIsActive(null);

        // Create the CabinetTemplate, which fails.
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCabinetTemplates() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList
        restCabinetTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cabinetTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].defaultWidthMm").value(hasItem(DEFAULT_DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].defaultHeightMm").value(hasItem(DEFAULT_DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].defaultDepthMm").value(hasItem(DEFAULT_DEFAULT_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].minWidthMm").value(hasItem(DEFAULT_MIN_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].maxWidthMm").value(hasItem(DEFAULT_MAX_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].supportsDoors").value(hasItem(DEFAULT_SUPPORTS_DOORS)))
            .andExpect(jsonPath("$.[*].supportsDrawers").value(hasItem(DEFAULT_SUPPORTS_DRAWERS)))
            .andExpect(jsonPath("$.[*].supportsShelves").value(hasItem(DEFAULT_SUPPORTS_SHELVES)))
            .andExpect(jsonPath("$.[*].fusionTemplateName").value(hasItem(DEFAULT_FUSION_TEMPLATE_NAME)))
            .andExpect(jsonPath("$.[*].csvProfileJson").value(hasItem(DEFAULT_CSV_PROFILE_JSON)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));
    }

    @Test
    @Transactional
    void getCabinetTemplate() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get the cabinetTemplate
        restCabinetTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, cabinetTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cabinetTemplate.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.defaultWidthMm").value(DEFAULT_DEFAULT_WIDTH_MM))
            .andExpect(jsonPath("$.defaultHeightMm").value(DEFAULT_DEFAULT_HEIGHT_MM))
            .andExpect(jsonPath("$.defaultDepthMm").value(DEFAULT_DEFAULT_DEPTH_MM))
            .andExpect(jsonPath("$.minWidthMm").value(DEFAULT_MIN_WIDTH_MM))
            .andExpect(jsonPath("$.maxWidthMm").value(DEFAULT_MAX_WIDTH_MM))
            .andExpect(jsonPath("$.supportsDoors").value(DEFAULT_SUPPORTS_DOORS))
            .andExpect(jsonPath("$.supportsDrawers").value(DEFAULT_SUPPORTS_DRAWERS))
            .andExpect(jsonPath("$.supportsShelves").value(DEFAULT_SUPPORTS_SHELVES))
            .andExpect(jsonPath("$.fusionTemplateName").value(DEFAULT_FUSION_TEMPLATE_NAME))
            .andExpect(jsonPath("$.csvProfileJson").value(DEFAULT_CSV_PROFILE_JSON))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER));
    }

    @Test
    @Transactional
    void getCabinetTemplatesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        Long id = cabinetTemplate.getId();

        defaultCabinetTemplateFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCabinetTemplateFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCabinetTemplateFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where code equals to
        defaultCabinetTemplateFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where code in
        defaultCabinetTemplateFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where code is not null
        defaultCabinetTemplateFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where code contains
        defaultCabinetTemplateFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where code does not contain
        defaultCabinetTemplateFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where name equals to
        defaultCabinetTemplateFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where name in
        defaultCabinetTemplateFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where name is not null
        defaultCabinetTemplateFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where name contains
        defaultCabinetTemplateFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where name does not contain
        defaultCabinetTemplateFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where category equals to
        defaultCabinetTemplateFiltering("category.equals=" + DEFAULT_CATEGORY, "category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where category in
        defaultCabinetTemplateFiltering("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY, "category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where category is not null
        defaultCabinetTemplateFiltering("category.specified=true", "category.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm equals to
        defaultCabinetTemplateFiltering(
            "defaultWidthMm.equals=" + DEFAULT_DEFAULT_WIDTH_MM,
            "defaultWidthMm.equals=" + UPDATED_DEFAULT_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm in
        defaultCabinetTemplateFiltering(
            "defaultWidthMm.in=" + DEFAULT_DEFAULT_WIDTH_MM + "," + UPDATED_DEFAULT_WIDTH_MM,
            "defaultWidthMm.in=" + UPDATED_DEFAULT_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm is not null
        defaultCabinetTemplateFiltering("defaultWidthMm.specified=true", "defaultWidthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm is greater than or equal to
        defaultCabinetTemplateFiltering(
            "defaultWidthMm.greaterThanOrEqual=" + DEFAULT_DEFAULT_WIDTH_MM,
            "defaultWidthMm.greaterThanOrEqual=" + UPDATED_DEFAULT_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm is less than or equal to
        defaultCabinetTemplateFiltering(
            "defaultWidthMm.lessThanOrEqual=" + DEFAULT_DEFAULT_WIDTH_MM,
            "defaultWidthMm.lessThanOrEqual=" + SMALLER_DEFAULT_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm is less than
        defaultCabinetTemplateFiltering(
            "defaultWidthMm.lessThan=" + UPDATED_DEFAULT_WIDTH_MM,
            "defaultWidthMm.lessThan=" + DEFAULT_DEFAULT_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultWidthMm is greater than
        defaultCabinetTemplateFiltering(
            "defaultWidthMm.greaterThan=" + SMALLER_DEFAULT_WIDTH_MM,
            "defaultWidthMm.greaterThan=" + DEFAULT_DEFAULT_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm equals to
        defaultCabinetTemplateFiltering(
            "defaultHeightMm.equals=" + DEFAULT_DEFAULT_HEIGHT_MM,
            "defaultHeightMm.equals=" + UPDATED_DEFAULT_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm in
        defaultCabinetTemplateFiltering(
            "defaultHeightMm.in=" + DEFAULT_DEFAULT_HEIGHT_MM + "," + UPDATED_DEFAULT_HEIGHT_MM,
            "defaultHeightMm.in=" + UPDATED_DEFAULT_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm is not null
        defaultCabinetTemplateFiltering("defaultHeightMm.specified=true", "defaultHeightMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm is greater than or equal to
        defaultCabinetTemplateFiltering(
            "defaultHeightMm.greaterThanOrEqual=" + DEFAULT_DEFAULT_HEIGHT_MM,
            "defaultHeightMm.greaterThanOrEqual=" + UPDATED_DEFAULT_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm is less than or equal to
        defaultCabinetTemplateFiltering(
            "defaultHeightMm.lessThanOrEqual=" + DEFAULT_DEFAULT_HEIGHT_MM,
            "defaultHeightMm.lessThanOrEqual=" + SMALLER_DEFAULT_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm is less than
        defaultCabinetTemplateFiltering(
            "defaultHeightMm.lessThan=" + UPDATED_DEFAULT_HEIGHT_MM,
            "defaultHeightMm.lessThan=" + DEFAULT_DEFAULT_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultHeightMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultHeightMm is greater than
        defaultCabinetTemplateFiltering(
            "defaultHeightMm.greaterThan=" + SMALLER_DEFAULT_HEIGHT_MM,
            "defaultHeightMm.greaterThan=" + DEFAULT_DEFAULT_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm equals to
        defaultCabinetTemplateFiltering(
            "defaultDepthMm.equals=" + DEFAULT_DEFAULT_DEPTH_MM,
            "defaultDepthMm.equals=" + UPDATED_DEFAULT_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm in
        defaultCabinetTemplateFiltering(
            "defaultDepthMm.in=" + DEFAULT_DEFAULT_DEPTH_MM + "," + UPDATED_DEFAULT_DEPTH_MM,
            "defaultDepthMm.in=" + UPDATED_DEFAULT_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm is not null
        defaultCabinetTemplateFiltering("defaultDepthMm.specified=true", "defaultDepthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm is greater than or equal to
        defaultCabinetTemplateFiltering(
            "defaultDepthMm.greaterThanOrEqual=" + DEFAULT_DEFAULT_DEPTH_MM,
            "defaultDepthMm.greaterThanOrEqual=" + UPDATED_DEFAULT_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm is less than or equal to
        defaultCabinetTemplateFiltering(
            "defaultDepthMm.lessThanOrEqual=" + DEFAULT_DEFAULT_DEPTH_MM,
            "defaultDepthMm.lessThanOrEqual=" + SMALLER_DEFAULT_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm is less than
        defaultCabinetTemplateFiltering(
            "defaultDepthMm.lessThan=" + UPDATED_DEFAULT_DEPTH_MM,
            "defaultDepthMm.lessThan=" + DEFAULT_DEFAULT_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByDefaultDepthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where defaultDepthMm is greater than
        defaultCabinetTemplateFiltering(
            "defaultDepthMm.greaterThan=" + SMALLER_DEFAULT_DEPTH_MM,
            "defaultDepthMm.greaterThan=" + DEFAULT_DEFAULT_DEPTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm equals to
        defaultCabinetTemplateFiltering("minWidthMm.equals=" + DEFAULT_MIN_WIDTH_MM, "minWidthMm.equals=" + UPDATED_MIN_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm in
        defaultCabinetTemplateFiltering(
            "minWidthMm.in=" + DEFAULT_MIN_WIDTH_MM + "," + UPDATED_MIN_WIDTH_MM,
            "minWidthMm.in=" + UPDATED_MIN_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm is not null
        defaultCabinetTemplateFiltering("minWidthMm.specified=true", "minWidthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm is greater than or equal to
        defaultCabinetTemplateFiltering(
            "minWidthMm.greaterThanOrEqual=" + DEFAULT_MIN_WIDTH_MM,
            "minWidthMm.greaterThanOrEqual=" + UPDATED_MIN_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm is less than or equal to
        defaultCabinetTemplateFiltering(
            "minWidthMm.lessThanOrEqual=" + DEFAULT_MIN_WIDTH_MM,
            "minWidthMm.lessThanOrEqual=" + SMALLER_MIN_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm is less than
        defaultCabinetTemplateFiltering("minWidthMm.lessThan=" + UPDATED_MIN_WIDTH_MM, "minWidthMm.lessThan=" + DEFAULT_MIN_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMinWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where minWidthMm is greater than
        defaultCabinetTemplateFiltering("minWidthMm.greaterThan=" + SMALLER_MIN_WIDTH_MM, "minWidthMm.greaterThan=" + DEFAULT_MIN_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm equals to
        defaultCabinetTemplateFiltering("maxWidthMm.equals=" + DEFAULT_MAX_WIDTH_MM, "maxWidthMm.equals=" + UPDATED_MAX_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm in
        defaultCabinetTemplateFiltering(
            "maxWidthMm.in=" + DEFAULT_MAX_WIDTH_MM + "," + UPDATED_MAX_WIDTH_MM,
            "maxWidthMm.in=" + UPDATED_MAX_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm is not null
        defaultCabinetTemplateFiltering("maxWidthMm.specified=true", "maxWidthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm is greater than or equal to
        defaultCabinetTemplateFiltering(
            "maxWidthMm.greaterThanOrEqual=" + DEFAULT_MAX_WIDTH_MM,
            "maxWidthMm.greaterThanOrEqual=" + UPDATED_MAX_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm is less than or equal to
        defaultCabinetTemplateFiltering(
            "maxWidthMm.lessThanOrEqual=" + DEFAULT_MAX_WIDTH_MM,
            "maxWidthMm.lessThanOrEqual=" + SMALLER_MAX_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm is less than
        defaultCabinetTemplateFiltering("maxWidthMm.lessThan=" + UPDATED_MAX_WIDTH_MM, "maxWidthMm.lessThan=" + DEFAULT_MAX_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByMaxWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where maxWidthMm is greater than
        defaultCabinetTemplateFiltering("maxWidthMm.greaterThan=" + SMALLER_MAX_WIDTH_MM, "maxWidthMm.greaterThan=" + DEFAULT_MAX_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsDoorsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsDoors equals to
        defaultCabinetTemplateFiltering("supportsDoors.equals=" + DEFAULT_SUPPORTS_DOORS, "supportsDoors.equals=" + UPDATED_SUPPORTS_DOORS);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsDoorsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsDoors in
        defaultCabinetTemplateFiltering(
            "supportsDoors.in=" + DEFAULT_SUPPORTS_DOORS + "," + UPDATED_SUPPORTS_DOORS,
            "supportsDoors.in=" + UPDATED_SUPPORTS_DOORS
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsDoorsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsDoors is not null
        defaultCabinetTemplateFiltering("supportsDoors.specified=true", "supportsDoors.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsDrawersIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsDrawers equals to
        defaultCabinetTemplateFiltering(
            "supportsDrawers.equals=" + DEFAULT_SUPPORTS_DRAWERS,
            "supportsDrawers.equals=" + UPDATED_SUPPORTS_DRAWERS
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsDrawersIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsDrawers in
        defaultCabinetTemplateFiltering(
            "supportsDrawers.in=" + DEFAULT_SUPPORTS_DRAWERS + "," + UPDATED_SUPPORTS_DRAWERS,
            "supportsDrawers.in=" + UPDATED_SUPPORTS_DRAWERS
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsDrawersIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsDrawers is not null
        defaultCabinetTemplateFiltering("supportsDrawers.specified=true", "supportsDrawers.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsShelvesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsShelves equals to
        defaultCabinetTemplateFiltering(
            "supportsShelves.equals=" + DEFAULT_SUPPORTS_SHELVES,
            "supportsShelves.equals=" + UPDATED_SUPPORTS_SHELVES
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsShelvesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsShelves in
        defaultCabinetTemplateFiltering(
            "supportsShelves.in=" + DEFAULT_SUPPORTS_SHELVES + "," + UPDATED_SUPPORTS_SHELVES,
            "supportsShelves.in=" + UPDATED_SUPPORTS_SHELVES
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySupportsShelvesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where supportsShelves is not null
        defaultCabinetTemplateFiltering("supportsShelves.specified=true", "supportsShelves.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByFusionTemplateNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where fusionTemplateName equals to
        defaultCabinetTemplateFiltering(
            "fusionTemplateName.equals=" + DEFAULT_FUSION_TEMPLATE_NAME,
            "fusionTemplateName.equals=" + UPDATED_FUSION_TEMPLATE_NAME
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByFusionTemplateNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where fusionTemplateName in
        defaultCabinetTemplateFiltering(
            "fusionTemplateName.in=" + DEFAULT_FUSION_TEMPLATE_NAME + "," + UPDATED_FUSION_TEMPLATE_NAME,
            "fusionTemplateName.in=" + UPDATED_FUSION_TEMPLATE_NAME
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByFusionTemplateNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where fusionTemplateName is not null
        defaultCabinetTemplateFiltering("fusionTemplateName.specified=true", "fusionTemplateName.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByFusionTemplateNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where fusionTemplateName contains
        defaultCabinetTemplateFiltering(
            "fusionTemplateName.contains=" + DEFAULT_FUSION_TEMPLATE_NAME,
            "fusionTemplateName.contains=" + UPDATED_FUSION_TEMPLATE_NAME
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByFusionTemplateNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where fusionTemplateName does not contain
        defaultCabinetTemplateFiltering(
            "fusionTemplateName.doesNotContain=" + UPDATED_FUSION_TEMPLATE_NAME,
            "fusionTemplateName.doesNotContain=" + DEFAULT_FUSION_TEMPLATE_NAME
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where isActive equals to
        defaultCabinetTemplateFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where isActive in
        defaultCabinetTemplateFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where isActive is not null
        defaultCabinetTemplateFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder equals to
        defaultCabinetTemplateFiltering("sortOrder.equals=" + DEFAULT_SORT_ORDER, "sortOrder.equals=" + UPDATED_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder in
        defaultCabinetTemplateFiltering(
            "sortOrder.in=" + DEFAULT_SORT_ORDER + "," + UPDATED_SORT_ORDER,
            "sortOrder.in=" + UPDATED_SORT_ORDER
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder is not null
        defaultCabinetTemplateFiltering("sortOrder.specified=true", "sortOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder is greater than or equal to
        defaultCabinetTemplateFiltering(
            "sortOrder.greaterThanOrEqual=" + DEFAULT_SORT_ORDER,
            "sortOrder.greaterThanOrEqual=" + UPDATED_SORT_ORDER
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder is less than or equal to
        defaultCabinetTemplateFiltering(
            "sortOrder.lessThanOrEqual=" + DEFAULT_SORT_ORDER,
            "sortOrder.lessThanOrEqual=" + SMALLER_SORT_ORDER
        );
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder is less than
        defaultCabinetTemplateFiltering("sortOrder.lessThan=" + UPDATED_SORT_ORDER, "sortOrder.lessThan=" + DEFAULT_SORT_ORDER);
    }

    @Test
    @Transactional
    void getAllCabinetTemplatesBySortOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        // Get all the cabinetTemplateList where sortOrder is greater than
        defaultCabinetTemplateFiltering("sortOrder.greaterThan=" + SMALLER_SORT_ORDER, "sortOrder.greaterThan=" + DEFAULT_SORT_ORDER);
    }

    private void defaultCabinetTemplateFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCabinetTemplateShouldBeFound(shouldBeFound);
        defaultCabinetTemplateShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCabinetTemplateShouldBeFound(String filter) throws Exception {
        restCabinetTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cabinetTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].defaultWidthMm").value(hasItem(DEFAULT_DEFAULT_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].defaultHeightMm").value(hasItem(DEFAULT_DEFAULT_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].defaultDepthMm").value(hasItem(DEFAULT_DEFAULT_DEPTH_MM)))
            .andExpect(jsonPath("$.[*].minWidthMm").value(hasItem(DEFAULT_MIN_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].maxWidthMm").value(hasItem(DEFAULT_MAX_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].supportsDoors").value(hasItem(DEFAULT_SUPPORTS_DOORS)))
            .andExpect(jsonPath("$.[*].supportsDrawers").value(hasItem(DEFAULT_SUPPORTS_DRAWERS)))
            .andExpect(jsonPath("$.[*].supportsShelves").value(hasItem(DEFAULT_SUPPORTS_SHELVES)))
            .andExpect(jsonPath("$.[*].fusionTemplateName").value(hasItem(DEFAULT_FUSION_TEMPLATE_NAME)))
            .andExpect(jsonPath("$.[*].csvProfileJson").value(hasItem(DEFAULT_CSV_PROFILE_JSON)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));

        // Check, that the count call also returns 1
        restCabinetTemplateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCabinetTemplateShouldNotBeFound(String filter) throws Exception {
        restCabinetTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCabinetTemplateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCabinetTemplate() throws Exception {
        // Get the cabinetTemplate
        restCabinetTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCabinetTemplate() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinetTemplate
        CabinetTemplate updatedCabinetTemplate = cabinetTemplateRepository.findById(cabinetTemplate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCabinetTemplate are not directly saved in db
        em.detach(updatedCabinetTemplate);
        updatedCabinetTemplate
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultWidthMm(UPDATED_DEFAULT_WIDTH_MM)
            .defaultHeightMm(UPDATED_DEFAULT_HEIGHT_MM)
            .defaultDepthMm(UPDATED_DEFAULT_DEPTH_MM)
            .minWidthMm(UPDATED_MIN_WIDTH_MM)
            .maxWidthMm(UPDATED_MAX_WIDTH_MM)
            .supportsDoors(UPDATED_SUPPORTS_DOORS)
            .supportsDrawers(UPDATED_SUPPORTS_DRAWERS)
            .supportsShelves(UPDATED_SUPPORTS_SHELVES)
            .fusionTemplateName(UPDATED_FUSION_TEMPLATE_NAME)
            .csvProfileJson(UPDATED_CSV_PROFILE_JSON)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(updatedCabinetTemplate);

        restCabinetTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cabinetTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCabinetTemplateToMatchAllProperties(updatedCabinetTemplate);
    }

    @Test
    @Transactional
    void putNonExistingCabinetTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetTemplate.setId(longCount.incrementAndGet());

        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCabinetTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cabinetTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCabinetTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetTemplate.setId(longCount.incrementAndGet());

        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cabinetTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCabinetTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetTemplate.setId(longCount.incrementAndGet());

        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetTemplateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCabinetTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinetTemplate using partial update
        CabinetTemplate partialUpdatedCabinetTemplate = new CabinetTemplate();
        partialUpdatedCabinetTemplate.setId(cabinetTemplate.getId());

        partialUpdatedCabinetTemplate
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultHeightMm(UPDATED_DEFAULT_HEIGHT_MM)
            .defaultDepthMm(UPDATED_DEFAULT_DEPTH_MM)
            .minWidthMm(UPDATED_MIN_WIDTH_MM)
            .supportsDoors(UPDATED_SUPPORTS_DOORS)
            .supportsDrawers(UPDATED_SUPPORTS_DRAWERS)
            .supportsShelves(UPDATED_SUPPORTS_SHELVES)
            .fusionTemplateName(UPDATED_FUSION_TEMPLATE_NAME)
            .csvProfileJson(UPDATED_CSV_PROFILE_JSON)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);

        restCabinetTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCabinetTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCabinetTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CabinetTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCabinetTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCabinetTemplate, cabinetTemplate),
            getPersistedCabinetTemplate(cabinetTemplate)
        );
    }

    @Test
    @Transactional
    void fullUpdateCabinetTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cabinetTemplate using partial update
        CabinetTemplate partialUpdatedCabinetTemplate = new CabinetTemplate();
        partialUpdatedCabinetTemplate.setId(cabinetTemplate.getId());

        partialUpdatedCabinetTemplate
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultWidthMm(UPDATED_DEFAULT_WIDTH_MM)
            .defaultHeightMm(UPDATED_DEFAULT_HEIGHT_MM)
            .defaultDepthMm(UPDATED_DEFAULT_DEPTH_MM)
            .minWidthMm(UPDATED_MIN_WIDTH_MM)
            .maxWidthMm(UPDATED_MAX_WIDTH_MM)
            .supportsDoors(UPDATED_SUPPORTS_DOORS)
            .supportsDrawers(UPDATED_SUPPORTS_DRAWERS)
            .supportsShelves(UPDATED_SUPPORTS_SHELVES)
            .fusionTemplateName(UPDATED_FUSION_TEMPLATE_NAME)
            .csvProfileJson(UPDATED_CSV_PROFILE_JSON)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);

        restCabinetTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCabinetTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCabinetTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CabinetTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCabinetTemplateUpdatableFieldsEquals(
            partialUpdatedCabinetTemplate,
            getPersistedCabinetTemplate(partialUpdatedCabinetTemplate)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCabinetTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetTemplate.setId(longCount.incrementAndGet());

        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCabinetTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cabinetTemplateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cabinetTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCabinetTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetTemplate.setId(longCount.incrementAndGet());

        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cabinetTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCabinetTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cabinetTemplate.setId(longCount.incrementAndGet());

        // Create the CabinetTemplate
        CabinetTemplateDTO cabinetTemplateDTO = cabinetTemplateMapper.toDto(cabinetTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCabinetTemplateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cabinetTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CabinetTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCabinetTemplate() throws Exception {
        // Initialize the database
        insertedCabinetTemplate = cabinetTemplateRepository.saveAndFlush(cabinetTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cabinetTemplate
        restCabinetTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, cabinetTemplate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cabinetTemplateRepository.count();
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

    protected CabinetTemplate getPersistedCabinetTemplate(CabinetTemplate cabinetTemplate) {
        return cabinetTemplateRepository.findById(cabinetTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedCabinetTemplateToMatchAllProperties(CabinetTemplate expectedCabinetTemplate) {
        assertCabinetTemplateAllPropertiesEquals(expectedCabinetTemplate, getPersistedCabinetTemplate(expectedCabinetTemplate));
    }

    protected void assertPersistedCabinetTemplateToMatchUpdatableProperties(CabinetTemplate expectedCabinetTemplate) {
        assertCabinetTemplateAllUpdatablePropertiesEquals(expectedCabinetTemplate, getPersistedCabinetTemplate(expectedCabinetTemplate));
    }
}
