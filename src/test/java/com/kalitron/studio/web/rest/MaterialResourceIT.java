package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.MaterialAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static com.kalitron.studio.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.domain.enumeration.MaterialKind;
import com.kalitron.studio.repository.MaterialRepository;
import com.kalitron.studio.service.dto.MaterialDTO;
import com.kalitron.studio.service.mapper.MaterialMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MaterialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MaterialResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final MaterialKind DEFAULT_MATERIAL_KIND = MaterialKind.MDF;
    private static final MaterialKind UPDATED_MATERIAL_KIND = MaterialKind.MELAMINE;

    private static final Integer DEFAULT_THICKNESS_MM = 1;
    private static final Integer UPDATED_THICKNESS_MM = 2;
    private static final Integer SMALLER_THICKNESS_MM = 1 - 1;

    private static final Integer DEFAULT_SHEET_WIDTH_MM = 1;
    private static final Integer UPDATED_SHEET_WIDTH_MM = 2;
    private static final Integer SMALLER_SHEET_WIDTH_MM = 1 - 1;

    private static final Integer DEFAULT_SHEET_HEIGHT_MM = 1;
    private static final Integer UPDATED_SHEET_HEIGHT_MM = 2;
    private static final Integer SMALLER_SHEET_HEIGHT_MM = 1 - 1;

    private static final BigDecimal DEFAULT_COST_PER_SHEET_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_COST_PER_SHEET_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_COST_PER_SHEET_MXN = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_COST_PER_SQUARE_METER_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_COST_PER_SQUARE_METER_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_COST_PER_SQUARE_METER_MXN = new BigDecimal(1 - 1);

    private static final String DEFAULT_SUPPLIER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMaterialMockMvc;

    private Material material;

    private Material insertedMaterial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Material createEntity() {
        return new Material()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .materialKind(DEFAULT_MATERIAL_KIND)
            .thicknessMm(DEFAULT_THICKNESS_MM)
            .sheetWidthMm(DEFAULT_SHEET_WIDTH_MM)
            .sheetHeightMm(DEFAULT_SHEET_HEIGHT_MM)
            .costPerSheetMxn(DEFAULT_COST_PER_SHEET_MXN)
            .costPerSquareMeterMxn(DEFAULT_COST_PER_SQUARE_METER_MXN)
            .supplierName(DEFAULT_SUPPLIER_NAME)
            .isActive(DEFAULT_IS_ACTIVE)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Material createUpdatedEntity() {
        return new Material()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .materialKind(UPDATED_MATERIAL_KIND)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .sheetWidthMm(UPDATED_SHEET_WIDTH_MM)
            .sheetHeightMm(UPDATED_SHEET_HEIGHT_MM)
            .costPerSheetMxn(UPDATED_COST_PER_SHEET_MXN)
            .costPerSquareMeterMxn(UPDATED_COST_PER_SQUARE_METER_MXN)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        material = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMaterial != null) {
            materialRepository.delete(insertedMaterial);
            insertedMaterial = null;
        }
    }

    @Test
    @Transactional
    void createMaterial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);
        var returnedMaterialDTO = om.readValue(
            restMaterialMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MaterialDTO.class
        );

        // Validate the Material in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMaterial = materialMapper.toEntity(returnedMaterialDTO);
        assertMaterialUpdatableFieldsEquals(returnedMaterial, getPersistedMaterial(returnedMaterial));

        insertedMaterial = returnedMaterial;
    }

    @Test
    @Transactional
    void createMaterialWithExistingId() throws Exception {
        // Create the Material with an existing ID
        material.setId(1L);
        MaterialDTO materialDTO = materialMapper.toDto(material);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setCode(null);

        // Create the Material, which fails.
        MaterialDTO materialDTO = materialMapper.toDto(material);

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setName(null);

        // Create the Material, which fails.
        MaterialDTO materialDTO = materialMapper.toDto(material);

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaterialKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setMaterialKind(null);

        // Create the Material, which fails.
        MaterialDTO materialDTO = materialMapper.toDto(material);

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setIsActive(null);

        // Create the Material, which fails.
        MaterialDTO materialDTO = materialMapper.toDto(material);

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMaterials() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(material.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].materialKind").value(hasItem(DEFAULT_MATERIAL_KIND.toString())))
            .andExpect(jsonPath("$.[*].thicknessMm").value(hasItem(DEFAULT_THICKNESS_MM)))
            .andExpect(jsonPath("$.[*].sheetWidthMm").value(hasItem(DEFAULT_SHEET_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].sheetHeightMm").value(hasItem(DEFAULT_SHEET_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].costPerSheetMxn").value(hasItem(sameNumber(DEFAULT_COST_PER_SHEET_MXN))))
            .andExpect(jsonPath("$.[*].costPerSquareMeterMxn").value(hasItem(sameNumber(DEFAULT_COST_PER_SQUARE_METER_MXN))))
            .andExpect(jsonPath("$.[*].supplierName").value(hasItem(DEFAULT_SUPPLIER_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getMaterial() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get the material
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, material.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(material.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.materialKind").value(DEFAULT_MATERIAL_KIND.toString()))
            .andExpect(jsonPath("$.thicknessMm").value(DEFAULT_THICKNESS_MM))
            .andExpect(jsonPath("$.sheetWidthMm").value(DEFAULT_SHEET_WIDTH_MM))
            .andExpect(jsonPath("$.sheetHeightMm").value(DEFAULT_SHEET_HEIGHT_MM))
            .andExpect(jsonPath("$.costPerSheetMxn").value(sameNumber(DEFAULT_COST_PER_SHEET_MXN)))
            .andExpect(jsonPath("$.costPerSquareMeterMxn").value(sameNumber(DEFAULT_COST_PER_SQUARE_METER_MXN)))
            .andExpect(jsonPath("$.supplierName").value(DEFAULT_SUPPLIER_NAME))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getMaterialsByIdFiltering() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        Long id = material.getId();

        defaultMaterialFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMaterialFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMaterialFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMaterialsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where code equals to
        defaultMaterialFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllMaterialsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where code in
        defaultMaterialFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllMaterialsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where code is not null
        defaultMaterialFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where code contains
        defaultMaterialFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllMaterialsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where code does not contain
        defaultMaterialFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where name equals to
        defaultMaterialFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where name in
        defaultMaterialFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where name is not null
        defaultMaterialFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where name contains
        defaultMaterialFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where name does not contain
        defaultMaterialFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByMaterialKindIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where materialKind equals to
        defaultMaterialFiltering("materialKind.equals=" + DEFAULT_MATERIAL_KIND, "materialKind.equals=" + UPDATED_MATERIAL_KIND);
    }

    @Test
    @Transactional
    void getAllMaterialsByMaterialKindIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where materialKind in
        defaultMaterialFiltering(
            "materialKind.in=" + DEFAULT_MATERIAL_KIND + "," + UPDATED_MATERIAL_KIND,
            "materialKind.in=" + UPDATED_MATERIAL_KIND
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByMaterialKindIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where materialKind is not null
        defaultMaterialFiltering("materialKind.specified=true", "materialKind.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm equals to
        defaultMaterialFiltering("thicknessMm.equals=" + DEFAULT_THICKNESS_MM, "thicknessMm.equals=" + UPDATED_THICKNESS_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm in
        defaultMaterialFiltering(
            "thicknessMm.in=" + DEFAULT_THICKNESS_MM + "," + UPDATED_THICKNESS_MM,
            "thicknessMm.in=" + UPDATED_THICKNESS_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm is not null
        defaultMaterialFiltering("thicknessMm.specified=true", "thicknessMm.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm is greater than or equal to
        defaultMaterialFiltering(
            "thicknessMm.greaterThanOrEqual=" + DEFAULT_THICKNESS_MM,
            "thicknessMm.greaterThanOrEqual=" + UPDATED_THICKNESS_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm is less than or equal to
        defaultMaterialFiltering(
            "thicknessMm.lessThanOrEqual=" + DEFAULT_THICKNESS_MM,
            "thicknessMm.lessThanOrEqual=" + SMALLER_THICKNESS_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm is less than
        defaultMaterialFiltering("thicknessMm.lessThan=" + UPDATED_THICKNESS_MM, "thicknessMm.lessThan=" + DEFAULT_THICKNESS_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsByThicknessMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where thicknessMm is greater than
        defaultMaterialFiltering("thicknessMm.greaterThan=" + SMALLER_THICKNESS_MM, "thicknessMm.greaterThan=" + DEFAULT_THICKNESS_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm equals to
        defaultMaterialFiltering("sheetWidthMm.equals=" + DEFAULT_SHEET_WIDTH_MM, "sheetWidthMm.equals=" + UPDATED_SHEET_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm in
        defaultMaterialFiltering(
            "sheetWidthMm.in=" + DEFAULT_SHEET_WIDTH_MM + "," + UPDATED_SHEET_WIDTH_MM,
            "sheetWidthMm.in=" + UPDATED_SHEET_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm is not null
        defaultMaterialFiltering("sheetWidthMm.specified=true", "sheetWidthMm.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm is greater than or equal to
        defaultMaterialFiltering(
            "sheetWidthMm.greaterThanOrEqual=" + DEFAULT_SHEET_WIDTH_MM,
            "sheetWidthMm.greaterThanOrEqual=" + UPDATED_SHEET_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm is less than or equal to
        defaultMaterialFiltering(
            "sheetWidthMm.lessThanOrEqual=" + DEFAULT_SHEET_WIDTH_MM,
            "sheetWidthMm.lessThanOrEqual=" + SMALLER_SHEET_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm is less than
        defaultMaterialFiltering("sheetWidthMm.lessThan=" + UPDATED_SHEET_WIDTH_MM, "sheetWidthMm.lessThan=" + DEFAULT_SHEET_WIDTH_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetWidthMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetWidthMm is greater than
        defaultMaterialFiltering(
            "sheetWidthMm.greaterThan=" + SMALLER_SHEET_WIDTH_MM,
            "sheetWidthMm.greaterThan=" + DEFAULT_SHEET_WIDTH_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm equals to
        defaultMaterialFiltering("sheetHeightMm.equals=" + DEFAULT_SHEET_HEIGHT_MM, "sheetHeightMm.equals=" + UPDATED_SHEET_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm in
        defaultMaterialFiltering(
            "sheetHeightMm.in=" + DEFAULT_SHEET_HEIGHT_MM + "," + UPDATED_SHEET_HEIGHT_MM,
            "sheetHeightMm.in=" + UPDATED_SHEET_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm is not null
        defaultMaterialFiltering("sheetHeightMm.specified=true", "sheetHeightMm.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm is greater than or equal to
        defaultMaterialFiltering(
            "sheetHeightMm.greaterThanOrEqual=" + DEFAULT_SHEET_HEIGHT_MM,
            "sheetHeightMm.greaterThanOrEqual=" + UPDATED_SHEET_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm is less than or equal to
        defaultMaterialFiltering(
            "sheetHeightMm.lessThanOrEqual=" + DEFAULT_SHEET_HEIGHT_MM,
            "sheetHeightMm.lessThanOrEqual=" + SMALLER_SHEET_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm is less than
        defaultMaterialFiltering("sheetHeightMm.lessThan=" + UPDATED_SHEET_HEIGHT_MM, "sheetHeightMm.lessThan=" + DEFAULT_SHEET_HEIGHT_MM);
    }

    @Test
    @Transactional
    void getAllMaterialsBySheetHeightMmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where sheetHeightMm is greater than
        defaultMaterialFiltering(
            "sheetHeightMm.greaterThan=" + SMALLER_SHEET_HEIGHT_MM,
            "sheetHeightMm.greaterThan=" + DEFAULT_SHEET_HEIGHT_MM
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn equals to
        defaultMaterialFiltering(
            "costPerSheetMxn.equals=" + DEFAULT_COST_PER_SHEET_MXN,
            "costPerSheetMxn.equals=" + UPDATED_COST_PER_SHEET_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn in
        defaultMaterialFiltering(
            "costPerSheetMxn.in=" + DEFAULT_COST_PER_SHEET_MXN + "," + UPDATED_COST_PER_SHEET_MXN,
            "costPerSheetMxn.in=" + UPDATED_COST_PER_SHEET_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn is not null
        defaultMaterialFiltering("costPerSheetMxn.specified=true", "costPerSheetMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn is greater than or equal to
        defaultMaterialFiltering(
            "costPerSheetMxn.greaterThanOrEqual=" + DEFAULT_COST_PER_SHEET_MXN,
            "costPerSheetMxn.greaterThanOrEqual=" + UPDATED_COST_PER_SHEET_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn is less than or equal to
        defaultMaterialFiltering(
            "costPerSheetMxn.lessThanOrEqual=" + DEFAULT_COST_PER_SHEET_MXN,
            "costPerSheetMxn.lessThanOrEqual=" + SMALLER_COST_PER_SHEET_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn is less than
        defaultMaterialFiltering(
            "costPerSheetMxn.lessThan=" + UPDATED_COST_PER_SHEET_MXN,
            "costPerSheetMxn.lessThan=" + DEFAULT_COST_PER_SHEET_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSheetMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSheetMxn is greater than
        defaultMaterialFiltering(
            "costPerSheetMxn.greaterThan=" + SMALLER_COST_PER_SHEET_MXN,
            "costPerSheetMxn.greaterThan=" + DEFAULT_COST_PER_SHEET_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn equals to
        defaultMaterialFiltering(
            "costPerSquareMeterMxn.equals=" + DEFAULT_COST_PER_SQUARE_METER_MXN,
            "costPerSquareMeterMxn.equals=" + UPDATED_COST_PER_SQUARE_METER_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn in
        defaultMaterialFiltering(
            "costPerSquareMeterMxn.in=" + DEFAULT_COST_PER_SQUARE_METER_MXN + "," + UPDATED_COST_PER_SQUARE_METER_MXN,
            "costPerSquareMeterMxn.in=" + UPDATED_COST_PER_SQUARE_METER_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn is not null
        defaultMaterialFiltering("costPerSquareMeterMxn.specified=true", "costPerSquareMeterMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn is greater than or equal to
        defaultMaterialFiltering(
            "costPerSquareMeterMxn.greaterThanOrEqual=" + DEFAULT_COST_PER_SQUARE_METER_MXN,
            "costPerSquareMeterMxn.greaterThanOrEqual=" + UPDATED_COST_PER_SQUARE_METER_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn is less than or equal to
        defaultMaterialFiltering(
            "costPerSquareMeterMxn.lessThanOrEqual=" + DEFAULT_COST_PER_SQUARE_METER_MXN,
            "costPerSquareMeterMxn.lessThanOrEqual=" + SMALLER_COST_PER_SQUARE_METER_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn is less than
        defaultMaterialFiltering(
            "costPerSquareMeterMxn.lessThan=" + UPDATED_COST_PER_SQUARE_METER_MXN,
            "costPerSquareMeterMxn.lessThan=" + DEFAULT_COST_PER_SQUARE_METER_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByCostPerSquareMeterMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where costPerSquareMeterMxn is greater than
        defaultMaterialFiltering(
            "costPerSquareMeterMxn.greaterThan=" + SMALLER_COST_PER_SQUARE_METER_MXN,
            "costPerSquareMeterMxn.greaterThan=" + DEFAULT_COST_PER_SQUARE_METER_MXN
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySupplierNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where supplierName equals to
        defaultMaterialFiltering("supplierName.equals=" + DEFAULT_SUPPLIER_NAME, "supplierName.equals=" + UPDATED_SUPPLIER_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsBySupplierNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where supplierName in
        defaultMaterialFiltering(
            "supplierName.in=" + DEFAULT_SUPPLIER_NAME + "," + UPDATED_SUPPLIER_NAME,
            "supplierName.in=" + UPDATED_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllMaterialsBySupplierNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where supplierName is not null
        defaultMaterialFiltering("supplierName.specified=true", "supplierName.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsBySupplierNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where supplierName contains
        defaultMaterialFiltering("supplierName.contains=" + DEFAULT_SUPPLIER_NAME, "supplierName.contains=" + UPDATED_SUPPLIER_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsBySupplierNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where supplierName does not contain
        defaultMaterialFiltering(
            "supplierName.doesNotContain=" + UPDATED_SUPPLIER_NAME,
            "supplierName.doesNotContain=" + DEFAULT_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllMaterialsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where isActive equals to
        defaultMaterialFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMaterialsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where isActive in
        defaultMaterialFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMaterialsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where isActive is not null
        defaultMaterialFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where notes equals to
        defaultMaterialFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllMaterialsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where notes in
        defaultMaterialFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllMaterialsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where notes is not null
        defaultMaterialFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where notes contains
        defaultMaterialFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllMaterialsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList where notes does not contain
        defaultMaterialFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    private void defaultMaterialFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMaterialShouldBeFound(shouldBeFound);
        defaultMaterialShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMaterialShouldBeFound(String filter) throws Exception {
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(material.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].materialKind").value(hasItem(DEFAULT_MATERIAL_KIND.toString())))
            .andExpect(jsonPath("$.[*].thicknessMm").value(hasItem(DEFAULT_THICKNESS_MM)))
            .andExpect(jsonPath("$.[*].sheetWidthMm").value(hasItem(DEFAULT_SHEET_WIDTH_MM)))
            .andExpect(jsonPath("$.[*].sheetHeightMm").value(hasItem(DEFAULT_SHEET_HEIGHT_MM)))
            .andExpect(jsonPath("$.[*].costPerSheetMxn").value(hasItem(sameNumber(DEFAULT_COST_PER_SHEET_MXN))))
            .andExpect(jsonPath("$.[*].costPerSquareMeterMxn").value(hasItem(sameNumber(DEFAULT_COST_PER_SQUARE_METER_MXN))))
            .andExpect(jsonPath("$.[*].supplierName").value(hasItem(DEFAULT_SUPPLIER_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMaterialShouldNotBeFound(String filter) throws Exception {
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMaterial() throws Exception {
        // Get the material
        restMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMaterial() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the material
        Material updatedMaterial = materialRepository.findById(material.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMaterial are not directly saved in db
        em.detach(updatedMaterial);
        updatedMaterial
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .materialKind(UPDATED_MATERIAL_KIND)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .sheetWidthMm(UPDATED_SHEET_WIDTH_MM)
            .sheetHeightMm(UPDATED_SHEET_HEIGHT_MM)
            .costPerSheetMxn(UPDATED_COST_PER_SHEET_MXN)
            .costPerSquareMeterMxn(UPDATED_COST_PER_SQUARE_METER_MXN)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);
        MaterialDTO materialDTO = materialMapper.toDto(updatedMaterial);

        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, materialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(materialDTO))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMaterialToMatchAllProperties(updatedMaterial);
    }

    @Test
    @Transactional
    void putNonExistingMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, materialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the material using partial update
        Material partialUpdatedMaterial = new Material();
        partialUpdatedMaterial.setId(material.getId());

        partialUpdatedMaterial
            .code(UPDATED_CODE)
            .materialKind(UPDATED_MATERIAL_KIND)
            .sheetWidthMm(UPDATED_SHEET_WIDTH_MM)
            .sheetHeightMm(UPDATED_SHEET_HEIGHT_MM)
            .costPerSheetMxn(UPDATED_COST_PER_SHEET_MXN)
            .isActive(UPDATED_IS_ACTIVE);

        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaterialUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMaterial, material), getPersistedMaterial(material));
    }

    @Test
    @Transactional
    void fullUpdateMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the material using partial update
        Material partialUpdatedMaterial = new Material();
        partialUpdatedMaterial.setId(material.getId());

        partialUpdatedMaterial
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .materialKind(UPDATED_MATERIAL_KIND)
            .thicknessMm(UPDATED_THICKNESS_MM)
            .sheetWidthMm(UPDATED_SHEET_WIDTH_MM)
            .sheetHeightMm(UPDATED_SHEET_HEIGHT_MM)
            .costPerSheetMxn(UPDATED_COST_PER_SHEET_MXN)
            .costPerSquareMeterMxn(UPDATED_COST_PER_SQUARE_METER_MXN)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);

        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaterialUpdatableFieldsEquals(partialUpdatedMaterial, getPersistedMaterial(partialUpdatedMaterial));
    }

    @Test
    @Transactional
    void patchNonExistingMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, materialDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(materialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMaterial() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the material
        restMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, material.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return materialRepository.count();
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

    protected Material getPersistedMaterial(Material material) {
        return materialRepository.findById(material.getId()).orElseThrow();
    }

    protected void assertPersistedMaterialToMatchAllProperties(Material expectedMaterial) {
        assertMaterialAllPropertiesEquals(expectedMaterial, getPersistedMaterial(expectedMaterial));
    }

    protected void assertPersistedMaterialToMatchUpdatableProperties(Material expectedMaterial) {
        assertMaterialAllUpdatablePropertiesEquals(expectedMaterial, getPersistedMaterial(expectedMaterial));
    }
}
