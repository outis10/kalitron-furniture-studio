package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.DesignSessionAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.CatalogStyle;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.service.DesignSessionService;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.mapper.DesignSessionMapper;
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
 * Integration tests for the {@link DesignSessionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DesignSessionResourceIT {

    private static final String DEFAULT_SESSION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_CODE = "BBBBBBBBBB";

    private static final ProjectType DEFAULT_PROJECT_TYPE = ProjectType.KITCHEN;
    private static final ProjectType UPDATED_PROJECT_TYPE = ProjectType.CLOSET;

    private static final SessionStatus DEFAULT_STATUS = SessionStatus.DRAFT;
    private static final SessionStatus UPDATED_STATUS = SessionStatus.CHATTING;

    private static final String DEFAULT_CLIENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CLIENT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CLIENT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_SELECTED_STYLE = "AAAAAAAAAA";
    private static final String UPDATED_SELECTED_STYLE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/design-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Mock
    private DesignSessionRepository designSessionRepositoryMock;

    @Autowired
    private DesignSessionMapper designSessionMapper;

    @Mock
    private DesignSessionService designSessionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDesignSessionMockMvc;

    private DesignSession designSession;

    private DesignSession insertedDesignSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DesignSession createEntity() {
        return new DesignSession()
            .sessionCode(DEFAULT_SESSION_CODE)
            .projectType(DEFAULT_PROJECT_TYPE)
            .status(DEFAULT_STATUS)
            .clientName(DEFAULT_CLIENT_NAME)
            .clientEmail(DEFAULT_CLIENT_EMAIL)
            .clientPhone(DEFAULT_CLIENT_PHONE)
            .selectedStyle(DEFAULT_SELECTED_STYLE)
            .notes(DEFAULT_NOTES)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DesignSession createUpdatedEntity() {
        return new DesignSession()
            .sessionCode(UPDATED_SESSION_CODE)
            .projectType(UPDATED_PROJECT_TYPE)
            .status(UPDATED_STATUS)
            .clientName(UPDATED_CLIENT_NAME)
            .clientEmail(UPDATED_CLIENT_EMAIL)
            .clientPhone(UPDATED_CLIENT_PHONE)
            .selectedStyle(UPDATED_SELECTED_STYLE)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        designSession = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDesignSession != null) {
            designSessionRepository.delete(insertedDesignSession);
            insertedDesignSession = null;
        }
    }

    @Test
    @Transactional
    void createDesignSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);
        var returnedDesignSessionDTO = om.readValue(
            restDesignSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DesignSessionDTO.class
        );

        // Validate the DesignSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDesignSession = designSessionMapper.toEntity(returnedDesignSessionDTO);
        assertDesignSessionUpdatableFieldsEquals(returnedDesignSession, getPersistedDesignSession(returnedDesignSession));

        insertedDesignSession = returnedDesignSession;
    }

    @Test
    @Transactional
    void createDesignSessionWithExistingId() throws Exception {
        // Create the DesignSession with an existing ID
        designSession.setId(1L);
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSessionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designSession.setSessionCode(null);

        // Create the DesignSession, which fails.
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProjectTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designSession.setProjectType(null);

        // Create the DesignSession, which fails.
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designSession.setStatus(null);

        // Create the DesignSession, which fails.
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkClientNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designSession.setClientName(null);

        // Create the DesignSession, which fails.
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designSession.setCreatedAt(null);

        // Create the DesignSession, which fails.
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designSession.setUpdatedAt(null);

        // Create the DesignSession, which fails.
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        restDesignSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDesignSessions() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList
        restDesignSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(designSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionCode").value(hasItem(DEFAULT_SESSION_CODE)))
            .andExpect(jsonPath("$.[*].projectType").value(hasItem(DEFAULT_PROJECT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].clientName").value(hasItem(DEFAULT_CLIENT_NAME)))
            .andExpect(jsonPath("$.[*].clientEmail").value(hasItem(DEFAULT_CLIENT_EMAIL)))
            .andExpect(jsonPath("$.[*].clientPhone").value(hasItem(DEFAULT_CLIENT_PHONE)))
            .andExpect(jsonPath("$.[*].selectedStyle").value(hasItem(DEFAULT_SELECTED_STYLE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDesignSessionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(designSessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDesignSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(designSessionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDesignSessionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(designSessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDesignSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(designSessionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDesignSession() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get the designSession
        restDesignSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, designSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(designSession.getId().intValue()))
            .andExpect(jsonPath("$.sessionCode").value(DEFAULT_SESSION_CODE))
            .andExpect(jsonPath("$.projectType").value(DEFAULT_PROJECT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.clientName").value(DEFAULT_CLIENT_NAME))
            .andExpect(jsonPath("$.clientEmail").value(DEFAULT_CLIENT_EMAIL))
            .andExpect(jsonPath("$.clientPhone").value(DEFAULT_CLIENT_PHONE))
            .andExpect(jsonPath("$.selectedStyle").value(DEFAULT_SELECTED_STYLE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getDesignSessionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        Long id = designSession.getId();

        defaultDesignSessionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDesignSessionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDesignSessionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySessionCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where sessionCode equals to
        defaultDesignSessionFiltering("sessionCode.equals=" + DEFAULT_SESSION_CODE, "sessionCode.equals=" + UPDATED_SESSION_CODE);
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySessionCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where sessionCode in
        defaultDesignSessionFiltering(
            "sessionCode.in=" + DEFAULT_SESSION_CODE + "," + UPDATED_SESSION_CODE,
            "sessionCode.in=" + UPDATED_SESSION_CODE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySessionCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where sessionCode is not null
        defaultDesignSessionFiltering("sessionCode.specified=true", "sessionCode.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySessionCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where sessionCode contains
        defaultDesignSessionFiltering("sessionCode.contains=" + DEFAULT_SESSION_CODE, "sessionCode.contains=" + UPDATED_SESSION_CODE);
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySessionCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where sessionCode does not contain
        defaultDesignSessionFiltering(
            "sessionCode.doesNotContain=" + UPDATED_SESSION_CODE,
            "sessionCode.doesNotContain=" + DEFAULT_SESSION_CODE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByProjectTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where projectType equals to
        defaultDesignSessionFiltering("projectType.equals=" + DEFAULT_PROJECT_TYPE, "projectType.equals=" + UPDATED_PROJECT_TYPE);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByProjectTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where projectType in
        defaultDesignSessionFiltering(
            "projectType.in=" + DEFAULT_PROJECT_TYPE + "," + UPDATED_PROJECT_TYPE,
            "projectType.in=" + UPDATED_PROJECT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByProjectTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where projectType is not null
        defaultDesignSessionFiltering("projectType.specified=true", "projectType.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where status equals to
        defaultDesignSessionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where status in
        defaultDesignSessionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where status is not null
        defaultDesignSessionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientName equals to
        defaultDesignSessionFiltering("clientName.equals=" + DEFAULT_CLIENT_NAME, "clientName.equals=" + UPDATED_CLIENT_NAME);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientName in
        defaultDesignSessionFiltering(
            "clientName.in=" + DEFAULT_CLIENT_NAME + "," + UPDATED_CLIENT_NAME,
            "clientName.in=" + UPDATED_CLIENT_NAME
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientName is not null
        defaultDesignSessionFiltering("clientName.specified=true", "clientName.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientName contains
        defaultDesignSessionFiltering("clientName.contains=" + DEFAULT_CLIENT_NAME, "clientName.contains=" + UPDATED_CLIENT_NAME);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientName does not contain
        defaultDesignSessionFiltering(
            "clientName.doesNotContain=" + UPDATED_CLIENT_NAME,
            "clientName.doesNotContain=" + DEFAULT_CLIENT_NAME
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientEmail equals to
        defaultDesignSessionFiltering("clientEmail.equals=" + DEFAULT_CLIENT_EMAIL, "clientEmail.equals=" + UPDATED_CLIENT_EMAIL);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientEmail in
        defaultDesignSessionFiltering(
            "clientEmail.in=" + DEFAULT_CLIENT_EMAIL + "," + UPDATED_CLIENT_EMAIL,
            "clientEmail.in=" + UPDATED_CLIENT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientEmail is not null
        defaultDesignSessionFiltering("clientEmail.specified=true", "clientEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientEmail contains
        defaultDesignSessionFiltering("clientEmail.contains=" + DEFAULT_CLIENT_EMAIL, "clientEmail.contains=" + UPDATED_CLIENT_EMAIL);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientEmail does not contain
        defaultDesignSessionFiltering(
            "clientEmail.doesNotContain=" + UPDATED_CLIENT_EMAIL,
            "clientEmail.doesNotContain=" + DEFAULT_CLIENT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientPhone equals to
        defaultDesignSessionFiltering("clientPhone.equals=" + DEFAULT_CLIENT_PHONE, "clientPhone.equals=" + UPDATED_CLIENT_PHONE);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientPhone in
        defaultDesignSessionFiltering(
            "clientPhone.in=" + DEFAULT_CLIENT_PHONE + "," + UPDATED_CLIENT_PHONE,
            "clientPhone.in=" + UPDATED_CLIENT_PHONE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientPhone is not null
        defaultDesignSessionFiltering("clientPhone.specified=true", "clientPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientPhone contains
        defaultDesignSessionFiltering("clientPhone.contains=" + DEFAULT_CLIENT_PHONE, "clientPhone.contains=" + UPDATED_CLIENT_PHONE);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByClientPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where clientPhone does not contain
        defaultDesignSessionFiltering(
            "clientPhone.doesNotContain=" + UPDATED_CLIENT_PHONE,
            "clientPhone.doesNotContain=" + DEFAULT_CLIENT_PHONE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySelectedStyleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where selectedStyle equals to
        defaultDesignSessionFiltering("selectedStyle.equals=" + DEFAULT_SELECTED_STYLE, "selectedStyle.equals=" + UPDATED_SELECTED_STYLE);
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySelectedStyleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where selectedStyle in
        defaultDesignSessionFiltering(
            "selectedStyle.in=" + DEFAULT_SELECTED_STYLE + "," + UPDATED_SELECTED_STYLE,
            "selectedStyle.in=" + UPDATED_SELECTED_STYLE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySelectedStyleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where selectedStyle is not null
        defaultDesignSessionFiltering("selectedStyle.specified=true", "selectedStyle.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySelectedStyleContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where selectedStyle contains
        defaultDesignSessionFiltering(
            "selectedStyle.contains=" + DEFAULT_SELECTED_STYLE,
            "selectedStyle.contains=" + UPDATED_SELECTED_STYLE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySelectedStyleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where selectedStyle does not contain
        defaultDesignSessionFiltering(
            "selectedStyle.doesNotContain=" + UPDATED_SELECTED_STYLE,
            "selectedStyle.doesNotContain=" + DEFAULT_SELECTED_STYLE
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where notes equals to
        defaultDesignSessionFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where notes in
        defaultDesignSessionFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where notes is not null
        defaultDesignSessionFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where notes contains
        defaultDesignSessionFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where notes does not contain
        defaultDesignSessionFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where createdAt equals to
        defaultDesignSessionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where createdAt in
        defaultDesignSessionFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where createdAt is not null
        defaultDesignSessionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where updatedAt equals to
        defaultDesignSessionFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllDesignSessionsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where updatedAt in
        defaultDesignSessionFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllDesignSessionsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        // Get all the designSessionList where updatedAt is not null
        defaultDesignSessionFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDesignSessionsBySpecIsEqualToSomething() throws Exception {
        KitchenSpec spec;
        if (TestUtil.findAll(em, KitchenSpec.class).isEmpty()) {
            designSessionRepository.saveAndFlush(designSession);
            spec = KitchenSpecResourceIT.createEntity(em);
        } else {
            spec = TestUtil.findAll(em, KitchenSpec.class).get(0);
        }
        em.persist(spec);
        em.flush();
        designSession.setSpec(spec);
        designSessionRepository.saveAndFlush(designSession);
        Long specId = spec.getId();
        // Get all the designSessionList where spec equals to specId
        defaultDesignSessionShouldBeFound("specId.equals=" + specId);

        // Get all the designSessionList where spec equals to (specId + 1)
        defaultDesignSessionShouldNotBeFound("specId.equals=" + (specId + 1));
    }

    @Test
    @Transactional
    void getAllDesignSessionsByCatalogStyleIsEqualToSomething() throws Exception {
        CatalogStyle catalogStyle;
        if (TestUtil.findAll(em, CatalogStyle.class).isEmpty()) {
            designSessionRepository.saveAndFlush(designSession);
            catalogStyle = CatalogStyleResourceIT.createEntity();
        } else {
            catalogStyle = TestUtil.findAll(em, CatalogStyle.class).get(0);
        }
        em.persist(catalogStyle);
        em.flush();
        designSession.setCatalogStyle(catalogStyle);
        designSessionRepository.saveAndFlush(designSession);
        Long catalogStyleId = catalogStyle.getId();
        // Get all the designSessionList where catalogStyle equals to catalogStyleId
        defaultDesignSessionShouldBeFound("catalogStyleId.equals=" + catalogStyleId);

        // Get all the designSessionList where catalogStyle equals to (catalogStyleId + 1)
        defaultDesignSessionShouldNotBeFound("catalogStyleId.equals=" + (catalogStyleId + 1));
    }

    private void defaultDesignSessionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDesignSessionShouldBeFound(shouldBeFound);
        defaultDesignSessionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDesignSessionShouldBeFound(String filter) throws Exception {
        restDesignSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(designSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionCode").value(hasItem(DEFAULT_SESSION_CODE)))
            .andExpect(jsonPath("$.[*].projectType").value(hasItem(DEFAULT_PROJECT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].clientName").value(hasItem(DEFAULT_CLIENT_NAME)))
            .andExpect(jsonPath("$.[*].clientEmail").value(hasItem(DEFAULT_CLIENT_EMAIL)))
            .andExpect(jsonPath("$.[*].clientPhone").value(hasItem(DEFAULT_CLIENT_PHONE)))
            .andExpect(jsonPath("$.[*].selectedStyle").value(hasItem(DEFAULT_SELECTED_STYLE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restDesignSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDesignSessionShouldNotBeFound(String filter) throws Exception {
        restDesignSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDesignSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDesignSession() throws Exception {
        // Get the designSession
        restDesignSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDesignSession() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designSession
        DesignSession updatedDesignSession = designSessionRepository.findById(designSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDesignSession are not directly saved in db
        em.detach(updatedDesignSession);
        updatedDesignSession
            .sessionCode(UPDATED_SESSION_CODE)
            .projectType(UPDATED_PROJECT_TYPE)
            .status(UPDATED_STATUS)
            .clientName(UPDATED_CLIENT_NAME)
            .clientEmail(UPDATED_CLIENT_EMAIL)
            .clientPhone(UPDATED_CLIENT_PHONE)
            .selectedStyle(UPDATED_SELECTED_STYLE)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(updatedDesignSession);

        restDesignSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, designSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDesignSessionToMatchAllProperties(updatedDesignSession);
    }

    @Test
    @Transactional
    void putNonExistingDesignSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designSession.setId(longCount.incrementAndGet());

        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDesignSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, designSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDesignSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designSession.setId(longCount.incrementAndGet());

        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDesignSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designSession.setId(longCount.incrementAndGet());

        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDesignSessionWithPatch() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designSession using partial update
        DesignSession partialUpdatedDesignSession = new DesignSession();
        partialUpdatedDesignSession.setId(designSession.getId());

        partialUpdatedDesignSession
            .sessionCode(UPDATED_SESSION_CODE)
            .clientName(UPDATED_CLIENT_NAME)
            .clientPhone(UPDATED_CLIENT_PHONE)
            .selectedStyle(UPDATED_SELECTED_STYLE)
            .notes(UPDATED_NOTES);

        restDesignSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDesignSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDesignSession))
            )
            .andExpect(status().isOk());

        // Validate the DesignSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDesignSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDesignSession, designSession),
            getPersistedDesignSession(designSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateDesignSessionWithPatch() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designSession using partial update
        DesignSession partialUpdatedDesignSession = new DesignSession();
        partialUpdatedDesignSession.setId(designSession.getId());

        partialUpdatedDesignSession
            .sessionCode(UPDATED_SESSION_CODE)
            .projectType(UPDATED_PROJECT_TYPE)
            .status(UPDATED_STATUS)
            .clientName(UPDATED_CLIENT_NAME)
            .clientEmail(UPDATED_CLIENT_EMAIL)
            .clientPhone(UPDATED_CLIENT_PHONE)
            .selectedStyle(UPDATED_SELECTED_STYLE)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restDesignSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDesignSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDesignSession))
            )
            .andExpect(status().isOk());

        // Validate the DesignSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDesignSessionUpdatableFieldsEquals(partialUpdatedDesignSession, getPersistedDesignSession(partialUpdatedDesignSession));
    }

    @Test
    @Transactional
    void patchNonExistingDesignSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designSession.setId(longCount.incrementAndGet());

        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDesignSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, designSessionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(designSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDesignSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designSession.setId(longCount.incrementAndGet());

        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(designSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDesignSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designSession.setId(longCount.incrementAndGet());

        // Create the DesignSession
        DesignSessionDTO designSessionDTO = designSessionMapper.toDto(designSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(designSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DesignSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDesignSession() throws Exception {
        // Initialize the database
        insertedDesignSession = designSessionRepository.saveAndFlush(designSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the designSession
        restDesignSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, designSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return designSessionRepository.count();
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

    protected DesignSession getPersistedDesignSession(DesignSession designSession) {
        return designSessionRepository.findById(designSession.getId()).orElseThrow();
    }

    protected void assertPersistedDesignSessionToMatchAllProperties(DesignSession expectedDesignSession) {
        assertDesignSessionAllPropertiesEquals(expectedDesignSession, getPersistedDesignSession(expectedDesignSession));
    }

    protected void assertPersistedDesignSessionToMatchUpdatableProperties(DesignSession expectedDesignSession) {
        assertDesignSessionAllUpdatablePropertiesEquals(expectedDesignSession, getPersistedDesignSession(expectedDesignSession));
    }
}
