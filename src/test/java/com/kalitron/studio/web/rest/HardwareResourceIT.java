package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.HardwareAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static com.kalitron.studio.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.Hardware;
import com.kalitron.studio.domain.enumeration.HardwareType;
import com.kalitron.studio.repository.HardwareRepository;
import com.kalitron.studio.service.dto.HardwareDTO;
import com.kalitron.studio.service.mapper.HardwareMapper;
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
 * Integration tests for the {@link HardwareResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HardwareResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final HardwareType DEFAULT_HARDWARE_TYPE = HardwareType.HINGE;
    private static final HardwareType UPDATED_HARDWARE_TYPE = HardwareType.SLIDE;

    private static final BigDecimal DEFAULT_UNIT_COST_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_COST_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_COST_MXN = new BigDecimal(1 - 1);

    private static final String DEFAULT_SUPPLIER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/hardware";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Autowired
    private HardwareMapper hardwareMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHardwareMockMvc;

    private Hardware hardware;

    private Hardware insertedHardware;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hardware createEntity() {
        return new Hardware()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .hardwareType(DEFAULT_HARDWARE_TYPE)
            .unitCostMxn(DEFAULT_UNIT_COST_MXN)
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
    public static Hardware createUpdatedEntity() {
        return new Hardware()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .hardwareType(UPDATED_HARDWARE_TYPE)
            .unitCostMxn(UPDATED_UNIT_COST_MXN)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        hardware = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHardware != null) {
            hardwareRepository.delete(insertedHardware);
            insertedHardware = null;
        }
    }

    @Test
    @Transactional
    void createHardware() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);
        var returnedHardwareDTO = om.readValue(
            restHardwareMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HardwareDTO.class
        );

        // Validate the Hardware in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHardware = hardwareMapper.toEntity(returnedHardwareDTO);
        assertHardwareUpdatableFieldsEquals(returnedHardware, getPersistedHardware(returnedHardware));

        insertedHardware = returnedHardware;
    }

    @Test
    @Transactional
    void createHardwareWithExistingId() throws Exception {
        // Create the Hardware with an existing ID
        hardware.setId(1L);
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHardwareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        hardware.setCode(null);

        // Create the Hardware, which fails.
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        restHardwareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        hardware.setName(null);

        // Create the Hardware, which fails.
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        restHardwareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHardwareTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        hardware.setHardwareType(null);

        // Create the Hardware, which fails.
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        restHardwareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitCostMxnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        hardware.setUnitCostMxn(null);

        // Create the Hardware, which fails.
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        restHardwareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        hardware.setIsActive(null);

        // Create the Hardware, which fails.
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        restHardwareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHardwares() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList
        restHardwareMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hardware.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hardwareType").value(hasItem(DEFAULT_HARDWARE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].unitCostMxn").value(hasItem(sameNumber(DEFAULT_UNIT_COST_MXN))))
            .andExpect(jsonPath("$.[*].supplierName").value(hasItem(DEFAULT_SUPPLIER_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getHardware() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get the hardware
        restHardwareMockMvc
            .perform(get(ENTITY_API_URL_ID, hardware.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hardware.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hardwareType").value(DEFAULT_HARDWARE_TYPE.toString()))
            .andExpect(jsonPath("$.unitCostMxn").value(sameNumber(DEFAULT_UNIT_COST_MXN)))
            .andExpect(jsonPath("$.supplierName").value(DEFAULT_SUPPLIER_NAME))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getHardwaresByIdFiltering() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        Long id = hardware.getId();

        defaultHardwareFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHardwareFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHardwareFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHardwaresByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where code equals to
        defaultHardwareFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllHardwaresByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where code in
        defaultHardwareFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllHardwaresByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where code is not null
        defaultHardwareFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where code contains
        defaultHardwareFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllHardwaresByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where code does not contain
        defaultHardwareFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllHardwaresByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where name equals to
        defaultHardwareFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHardwaresByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where name in
        defaultHardwareFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHardwaresByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where name is not null
        defaultHardwareFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where name contains
        defaultHardwareFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHardwaresByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where name does not contain
        defaultHardwareFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllHardwaresByHardwareTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where hardwareType equals to
        defaultHardwareFiltering("hardwareType.equals=" + DEFAULT_HARDWARE_TYPE, "hardwareType.equals=" + UPDATED_HARDWARE_TYPE);
    }

    @Test
    @Transactional
    void getAllHardwaresByHardwareTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where hardwareType in
        defaultHardwareFiltering(
            "hardwareType.in=" + DEFAULT_HARDWARE_TYPE + "," + UPDATED_HARDWARE_TYPE,
            "hardwareType.in=" + UPDATED_HARDWARE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllHardwaresByHardwareTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where hardwareType is not null
        defaultHardwareFiltering("hardwareType.specified=true", "hardwareType.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn equals to
        defaultHardwareFiltering("unitCostMxn.equals=" + DEFAULT_UNIT_COST_MXN, "unitCostMxn.equals=" + UPDATED_UNIT_COST_MXN);
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn in
        defaultHardwareFiltering(
            "unitCostMxn.in=" + DEFAULT_UNIT_COST_MXN + "," + UPDATED_UNIT_COST_MXN,
            "unitCostMxn.in=" + UPDATED_UNIT_COST_MXN
        );
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn is not null
        defaultHardwareFiltering("unitCostMxn.specified=true", "unitCostMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn is greater than or equal to
        defaultHardwareFiltering(
            "unitCostMxn.greaterThanOrEqual=" + DEFAULT_UNIT_COST_MXN,
            "unitCostMxn.greaterThanOrEqual=" + UPDATED_UNIT_COST_MXN
        );
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn is less than or equal to
        defaultHardwareFiltering(
            "unitCostMxn.lessThanOrEqual=" + DEFAULT_UNIT_COST_MXN,
            "unitCostMxn.lessThanOrEqual=" + SMALLER_UNIT_COST_MXN
        );
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn is less than
        defaultHardwareFiltering("unitCostMxn.lessThan=" + UPDATED_UNIT_COST_MXN, "unitCostMxn.lessThan=" + DEFAULT_UNIT_COST_MXN);
    }

    @Test
    @Transactional
    void getAllHardwaresByUnitCostMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where unitCostMxn is greater than
        defaultHardwareFiltering("unitCostMxn.greaterThan=" + SMALLER_UNIT_COST_MXN, "unitCostMxn.greaterThan=" + DEFAULT_UNIT_COST_MXN);
    }

    @Test
    @Transactional
    void getAllHardwaresBySupplierNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where supplierName equals to
        defaultHardwareFiltering("supplierName.equals=" + DEFAULT_SUPPLIER_NAME, "supplierName.equals=" + UPDATED_SUPPLIER_NAME);
    }

    @Test
    @Transactional
    void getAllHardwaresBySupplierNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where supplierName in
        defaultHardwareFiltering(
            "supplierName.in=" + DEFAULT_SUPPLIER_NAME + "," + UPDATED_SUPPLIER_NAME,
            "supplierName.in=" + UPDATED_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllHardwaresBySupplierNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where supplierName is not null
        defaultHardwareFiltering("supplierName.specified=true", "supplierName.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresBySupplierNameContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where supplierName contains
        defaultHardwareFiltering("supplierName.contains=" + DEFAULT_SUPPLIER_NAME, "supplierName.contains=" + UPDATED_SUPPLIER_NAME);
    }

    @Test
    @Transactional
    void getAllHardwaresBySupplierNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where supplierName does not contain
        defaultHardwareFiltering(
            "supplierName.doesNotContain=" + UPDATED_SUPPLIER_NAME,
            "supplierName.doesNotContain=" + DEFAULT_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllHardwaresByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where isActive equals to
        defaultHardwareFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllHardwaresByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where isActive in
        defaultHardwareFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllHardwaresByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where isActive is not null
        defaultHardwareFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where notes equals to
        defaultHardwareFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllHardwaresByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where notes in
        defaultHardwareFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllHardwaresByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where notes is not null
        defaultHardwareFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllHardwaresByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where notes contains
        defaultHardwareFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllHardwaresByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        // Get all the hardwareList where notes does not contain
        defaultHardwareFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    private void defaultHardwareFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHardwareShouldBeFound(shouldBeFound);
        defaultHardwareShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHardwareShouldBeFound(String filter) throws Exception {
        restHardwareMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hardware.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hardwareType").value(hasItem(DEFAULT_HARDWARE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].unitCostMxn").value(hasItem(sameNumber(DEFAULT_UNIT_COST_MXN))))
            .andExpect(jsonPath("$.[*].supplierName").value(hasItem(DEFAULT_SUPPLIER_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restHardwareMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHardwareShouldNotBeFound(String filter) throws Exception {
        restHardwareMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHardwareMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHardware() throws Exception {
        // Get the hardware
        restHardwareMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHardware() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hardware
        Hardware updatedHardware = hardwareRepository.findById(hardware.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHardware are not directly saved in db
        em.detach(updatedHardware);
        updatedHardware
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .hardwareType(UPDATED_HARDWARE_TYPE)
            .unitCostMxn(UPDATED_UNIT_COST_MXN)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);
        HardwareDTO hardwareDTO = hardwareMapper.toDto(updatedHardware);

        restHardwareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hardwareDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hardwareDTO))
            )
            .andExpect(status().isOk());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHardwareToMatchAllProperties(updatedHardware);
    }

    @Test
    @Transactional
    void putNonExistingHardware() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hardware.setId(longCount.incrementAndGet());

        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHardwareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hardwareDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hardwareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHardware() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hardware.setId(longCount.incrementAndGet());

        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHardwareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hardwareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHardware() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hardware.setId(longCount.incrementAndGet());

        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHardwareMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHardwareWithPatch() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hardware using partial update
        Hardware partialUpdatedHardware = new Hardware();
        partialUpdatedHardware.setId(hardware.getId());

        partialUpdatedHardware.hardwareType(UPDATED_HARDWARE_TYPE).supplierName(UPDATED_SUPPLIER_NAME).isActive(UPDATED_IS_ACTIVE);

        restHardwareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHardware.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHardware))
            )
            .andExpect(status().isOk());

        // Validate the Hardware in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHardwareUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHardware, hardware), getPersistedHardware(hardware));
    }

    @Test
    @Transactional
    void fullUpdateHardwareWithPatch() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hardware using partial update
        Hardware partialUpdatedHardware = new Hardware();
        partialUpdatedHardware.setId(hardware.getId());

        partialUpdatedHardware
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .hardwareType(UPDATED_HARDWARE_TYPE)
            .unitCostMxn(UPDATED_UNIT_COST_MXN)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .notes(UPDATED_NOTES);

        restHardwareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHardware.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHardware))
            )
            .andExpect(status().isOk());

        // Validate the Hardware in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHardwareUpdatableFieldsEquals(partialUpdatedHardware, getPersistedHardware(partialUpdatedHardware));
    }

    @Test
    @Transactional
    void patchNonExistingHardware() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hardware.setId(longCount.incrementAndGet());

        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHardwareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hardwareDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hardwareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHardware() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hardware.setId(longCount.incrementAndGet());

        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHardwareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hardwareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHardware() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hardware.setId(longCount.incrementAndGet());

        // Create the Hardware
        HardwareDTO hardwareDTO = hardwareMapper.toDto(hardware);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHardwareMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(hardwareDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hardware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHardware() throws Exception {
        // Initialize the database
        insertedHardware = hardwareRepository.saveAndFlush(hardware);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the hardware
        restHardwareMockMvc
            .perform(delete(ENTITY_API_URL_ID, hardware.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return hardwareRepository.count();
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

    protected Hardware getPersistedHardware(Hardware hardware) {
        return hardwareRepository.findById(hardware.getId()).orElseThrow();
    }

    protected void assertPersistedHardwareToMatchAllProperties(Hardware expectedHardware) {
        assertHardwareAllPropertiesEquals(expectedHardware, getPersistedHardware(expectedHardware));
    }

    protected void assertPersistedHardwareToMatchUpdatableProperties(Hardware expectedHardware) {
        assertHardwareAllUpdatablePropertiesEquals(expectedHardware, getPersistedHardware(expectedHardware));
    }
}
