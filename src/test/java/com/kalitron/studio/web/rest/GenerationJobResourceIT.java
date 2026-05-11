package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.GenerationJobAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.GenerationJob;
import com.kalitron.studio.domain.enumeration.GenerationJobStatus;
import com.kalitron.studio.domain.enumeration.GenerationJobType;
import com.kalitron.studio.repository.GenerationJobRepository;
import com.kalitron.studio.service.GenerationJobService;
import com.kalitron.studio.service.dto.GenerationJobDTO;
import com.kalitron.studio.service.mapper.GenerationJobMapper;
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
 * Integration tests for the {@link GenerationJobResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GenerationJobResourceIT {

    private static final GenerationJobType DEFAULT_JOB_TYPE = GenerationJobType.AI_CHAT;
    private static final GenerationJobType UPDATED_JOB_TYPE = GenerationJobType.AI_SPEC_EXTRACTION;

    private static final GenerationJobStatus DEFAULT_STATUS = GenerationJobStatus.PENDING;
    private static final GenerationJobStatus UPDATED_STATUS = GenerationJobStatus.RUNNING;

    private static final String DEFAULT_INPUT_JSON = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_JSON = "BBBBBBBBBB";

    private static final String DEFAULT_OUTPUT_JSON = "AAAAAAAAAA";
    private static final String UPDATED_OUTPUT_JSON = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FINISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/generation-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GenerationJobRepository generationJobRepository;

    @Mock
    private GenerationJobRepository generationJobRepositoryMock;

    @Autowired
    private GenerationJobMapper generationJobMapper;

    @Mock
    private GenerationJobService generationJobServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGenerationJobMockMvc;

    private GenerationJob generationJob;

    private GenerationJob insertedGenerationJob;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenerationJob createEntity(EntityManager em) {
        GenerationJob generationJob = new GenerationJob()
            .jobType(DEFAULT_JOB_TYPE)
            .status(DEFAULT_STATUS)
            .inputJson(DEFAULT_INPUT_JSON)
            .outputJson(DEFAULT_OUTPUT_JSON)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .createdAt(DEFAULT_CREATED_AT)
            .startedAt(DEFAULT_STARTED_AT)
            .finishedAt(DEFAULT_FINISHED_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        generationJob.setSession(designSession);
        return generationJob;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenerationJob createUpdatedEntity(EntityManager em) {
        GenerationJob updatedGenerationJob = new GenerationJob()
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .inputJson(UPDATED_INPUT_JSON)
            .outputJson(UPDATED_OUTPUT_JSON)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedGenerationJob.setSession(designSession);
        return updatedGenerationJob;
    }

    @BeforeEach
    void initTest() {
        generationJob = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedGenerationJob != null) {
            generationJobRepository.delete(insertedGenerationJob);
            insertedGenerationJob = null;
        }
    }

    @Test
    @Transactional
    void createGenerationJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);
        var returnedGenerationJobDTO = om.readValue(
            restGenerationJobMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(generationJobDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GenerationJobDTO.class
        );

        // Validate the GenerationJob in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGenerationJob = generationJobMapper.toEntity(returnedGenerationJobDTO);
        assertGenerationJobUpdatableFieldsEquals(returnedGenerationJob, getPersistedGenerationJob(returnedGenerationJob));

        insertedGenerationJob = returnedGenerationJob;
    }

    @Test
    @Transactional
    void createGenerationJobWithExistingId() throws Exception {
        // Create the GenerationJob with an existing ID
        generationJob.setId(1L);
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenerationJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(generationJobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkJobTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        generationJob.setJobType(null);

        // Create the GenerationJob, which fails.
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        restGenerationJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(generationJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        generationJob.setStatus(null);

        // Create the GenerationJob, which fails.
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        restGenerationJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(generationJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        generationJob.setCreatedAt(null);

        // Create the GenerationJob, which fails.
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        restGenerationJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(generationJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGenerationJobs() throws Exception {
        // Initialize the database
        insertedGenerationJob = generationJobRepository.saveAndFlush(generationJob);

        // Get all the generationJobList
        restGenerationJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(generationJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobType").value(hasItem(DEFAULT_JOB_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].inputJson").value(hasItem(DEFAULT_INPUT_JSON)))
            .andExpect(jsonPath("$.[*].outputJson").value(hasItem(DEFAULT_OUTPUT_JSON)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].finishedAt").value(hasItem(DEFAULT_FINISHED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGenerationJobsWithEagerRelationshipsIsEnabled() throws Exception {
        when(generationJobServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGenerationJobMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(generationJobServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGenerationJobsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(generationJobServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGenerationJobMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(generationJobRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getGenerationJob() throws Exception {
        // Initialize the database
        insertedGenerationJob = generationJobRepository.saveAndFlush(generationJob);

        // Get the generationJob
        restGenerationJobMockMvc
            .perform(get(ENTITY_API_URL_ID, generationJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(generationJob.getId().intValue()))
            .andExpect(jsonPath("$.jobType").value(DEFAULT_JOB_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.inputJson").value(DEFAULT_INPUT_JSON))
            .andExpect(jsonPath("$.outputJson").value(DEFAULT_OUTPUT_JSON))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.finishedAt").value(DEFAULT_FINISHED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGenerationJob() throws Exception {
        // Get the generationJob
        restGenerationJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGenerationJob() throws Exception {
        // Initialize the database
        insertedGenerationJob = generationJobRepository.saveAndFlush(generationJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the generationJob
        GenerationJob updatedGenerationJob = generationJobRepository.findById(generationJob.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGenerationJob are not directly saved in db
        em.detach(updatedGenerationJob);
        updatedGenerationJob
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .inputJson(UPDATED_INPUT_JSON)
            .outputJson(UPDATED_OUTPUT_JSON)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(updatedGenerationJob);

        restGenerationJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, generationJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(generationJobDTO))
            )
            .andExpect(status().isOk());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGenerationJobToMatchAllProperties(updatedGenerationJob);
    }

    @Test
    @Transactional
    void putNonExistingGenerationJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        generationJob.setId(longCount.incrementAndGet());

        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenerationJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, generationJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(generationJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGenerationJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        generationJob.setId(longCount.incrementAndGet());

        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerationJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(generationJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGenerationJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        generationJob.setId(longCount.incrementAndGet());

        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerationJobMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(generationJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGenerationJobWithPatch() throws Exception {
        // Initialize the database
        insertedGenerationJob = generationJobRepository.saveAndFlush(generationJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the generationJob using partial update
        GenerationJob partialUpdatedGenerationJob = new GenerationJob();
        partialUpdatedGenerationJob.setId(generationJob.getId());

        partialUpdatedGenerationJob
            .status(UPDATED_STATUS)
            .outputJson(UPDATED_OUTPUT_JSON)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);

        restGenerationJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenerationJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenerationJob))
            )
            .andExpect(status().isOk());

        // Validate the GenerationJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenerationJobUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGenerationJob, generationJob),
            getPersistedGenerationJob(generationJob)
        );
    }

    @Test
    @Transactional
    void fullUpdateGenerationJobWithPatch() throws Exception {
        // Initialize the database
        insertedGenerationJob = generationJobRepository.saveAndFlush(generationJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the generationJob using partial update
        GenerationJob partialUpdatedGenerationJob = new GenerationJob();
        partialUpdatedGenerationJob.setId(generationJob.getId());

        partialUpdatedGenerationJob
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .inputJson(UPDATED_INPUT_JSON)
            .outputJson(UPDATED_OUTPUT_JSON)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT);

        restGenerationJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenerationJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenerationJob))
            )
            .andExpect(status().isOk());

        // Validate the GenerationJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenerationJobUpdatableFieldsEquals(partialUpdatedGenerationJob, getPersistedGenerationJob(partialUpdatedGenerationJob));
    }

    @Test
    @Transactional
    void patchNonExistingGenerationJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        generationJob.setId(longCount.incrementAndGet());

        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenerationJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, generationJobDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(generationJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGenerationJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        generationJob.setId(longCount.incrementAndGet());

        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerationJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(generationJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGenerationJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        generationJob.setId(longCount.incrementAndGet());

        // Create the GenerationJob
        GenerationJobDTO generationJobDTO = generationJobMapper.toDto(generationJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerationJobMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(generationJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenerationJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGenerationJob() throws Exception {
        // Initialize the database
        insertedGenerationJob = generationJobRepository.saveAndFlush(generationJob);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the generationJob
        restGenerationJobMockMvc
            .perform(delete(ENTITY_API_URL_ID, generationJob.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return generationJobRepository.count();
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

    protected GenerationJob getPersistedGenerationJob(GenerationJob generationJob) {
        return generationJobRepository.findById(generationJob.getId()).orElseThrow();
    }

    protected void assertPersistedGenerationJobToMatchAllProperties(GenerationJob expectedGenerationJob) {
        assertGenerationJobAllPropertiesEquals(expectedGenerationJob, getPersistedGenerationJob(expectedGenerationJob));
    }

    protected void assertPersistedGenerationJobToMatchUpdatableProperties(GenerationJob expectedGenerationJob) {
        assertGenerationJobAllUpdatablePropertiesEquals(expectedGenerationJob, getPersistedGenerationJob(expectedGenerationJob));
    }
}
