package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.DesignArtifactAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.service.DesignArtifactService;
import com.kalitron.studio.service.dto.DesignArtifactDTO;
import com.kalitron.studio.service.mapper.DesignArtifactMapper;
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
 * Integration tests for the {@link DesignArtifactResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DesignArtifactResourceIT {

    private static final ArtifactType DEFAULT_ARTIFACT_TYPE = ArtifactType.CSV;
    private static final ArtifactType UPDATED_ARTIFACT_TYPE = ArtifactType.FUSION_SCRIPT;

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FILE_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE_KB = 1L;
    private static final Long UPDATED_FILE_SIZE_KB = 2L;

    private static final String DEFAULT_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_CHECKSUM = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA_JSON = "AAAAAAAAAA";
    private static final String UPDATED_METADATA_JSON = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/design-artifacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DesignArtifactRepository designArtifactRepository;

    @Mock
    private DesignArtifactRepository designArtifactRepositoryMock;

    @Autowired
    private DesignArtifactMapper designArtifactMapper;

    @Mock
    private DesignArtifactService designArtifactServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDesignArtifactMockMvc;

    private DesignArtifact designArtifact;

    private DesignArtifact insertedDesignArtifact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DesignArtifact createEntity(EntityManager em) {
        DesignArtifact designArtifact = new DesignArtifact()
            .artifactType(DEFAULT_ARTIFACT_TYPE)
            .fileName(DEFAULT_FILE_NAME)
            .filePath(DEFAULT_FILE_PATH)
            .mimeType(DEFAULT_MIME_TYPE)
            .fileSizeKb(DEFAULT_FILE_SIZE_KB)
            .checksum(DEFAULT_CHECKSUM)
            .metadataJson(DEFAULT_METADATA_JSON)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        designArtifact.setSession(designSession);
        return designArtifact;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DesignArtifact createUpdatedEntity(EntityManager em) {
        DesignArtifact updatedDesignArtifact = new DesignArtifact()
            .artifactType(UPDATED_ARTIFACT_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .checksum(UPDATED_CHECKSUM)
            .metadataJson(UPDATED_METADATA_JSON)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedDesignArtifact.setSession(designSession);
        return updatedDesignArtifact;
    }

    @BeforeEach
    void initTest() {
        designArtifact = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDesignArtifact != null) {
            designArtifactRepository.delete(insertedDesignArtifact);
            insertedDesignArtifact = null;
        }
    }

    @Test
    @Transactional
    void createDesignArtifact() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);
        var returnedDesignArtifactDTO = om.readValue(
            restDesignArtifactMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DesignArtifactDTO.class
        );

        // Validate the DesignArtifact in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDesignArtifact = designArtifactMapper.toEntity(returnedDesignArtifactDTO);
        assertDesignArtifactUpdatableFieldsEquals(returnedDesignArtifact, getPersistedDesignArtifact(returnedDesignArtifact));

        insertedDesignArtifact = returnedDesignArtifact;
    }

    @Test
    @Transactional
    void createDesignArtifactWithExistingId() throws Exception {
        // Create the DesignArtifact with an existing ID
        designArtifact.setId(1L);
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDesignArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkArtifactTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designArtifact.setArtifactType(null);

        // Create the DesignArtifact, which fails.
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        restDesignArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designArtifact.setFileName(null);

        // Create the DesignArtifact, which fails.
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        restDesignArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFilePathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designArtifact.setFilePath(null);

        // Create the DesignArtifact, which fails.
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        restDesignArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designArtifact.setCreatedAt(null);

        // Create the DesignArtifact, which fails.
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        restDesignArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDesignArtifacts() throws Exception {
        // Initialize the database
        insertedDesignArtifact = designArtifactRepository.saveAndFlush(designArtifact);

        // Get all the designArtifactList
        restDesignArtifactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(designArtifact.getId().intValue())))
            .andExpect(jsonPath("$.[*].artifactType").value(hasItem(DEFAULT_ARTIFACT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].fileSizeKb").value(hasItem(DEFAULT_FILE_SIZE_KB.intValue())))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].metadataJson").value(hasItem(DEFAULT_METADATA_JSON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDesignArtifactsWithEagerRelationshipsIsEnabled() throws Exception {
        when(designArtifactServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDesignArtifactMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(designArtifactServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDesignArtifactsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(designArtifactServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDesignArtifactMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(designArtifactRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDesignArtifact() throws Exception {
        // Initialize the database
        insertedDesignArtifact = designArtifactRepository.saveAndFlush(designArtifact);

        // Get the designArtifact
        restDesignArtifactMockMvc
            .perform(get(ENTITY_API_URL_ID, designArtifact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(designArtifact.getId().intValue()))
            .andExpect(jsonPath("$.artifactType").value(DEFAULT_ARTIFACT_TYPE.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.filePath").value(DEFAULT_FILE_PATH))
            .andExpect(jsonPath("$.mimeType").value(DEFAULT_MIME_TYPE))
            .andExpect(jsonPath("$.fileSizeKb").value(DEFAULT_FILE_SIZE_KB.intValue()))
            .andExpect(jsonPath("$.checksum").value(DEFAULT_CHECKSUM))
            .andExpect(jsonPath("$.metadataJson").value(DEFAULT_METADATA_JSON))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDesignArtifact() throws Exception {
        // Get the designArtifact
        restDesignArtifactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDesignArtifact() throws Exception {
        // Initialize the database
        insertedDesignArtifact = designArtifactRepository.saveAndFlush(designArtifact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designArtifact
        DesignArtifact updatedDesignArtifact = designArtifactRepository.findById(designArtifact.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDesignArtifact are not directly saved in db
        em.detach(updatedDesignArtifact);
        updatedDesignArtifact
            .artifactType(UPDATED_ARTIFACT_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .checksum(UPDATED_CHECKSUM)
            .metadataJson(UPDATED_METADATA_JSON)
            .createdAt(UPDATED_CREATED_AT);
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(updatedDesignArtifact);

        restDesignArtifactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, designArtifactDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designArtifactDTO))
            )
            .andExpect(status().isOk());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDesignArtifactToMatchAllProperties(updatedDesignArtifact);
    }

    @Test
    @Transactional
    void putNonExistingDesignArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designArtifact.setId(longCount.incrementAndGet());

        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDesignArtifactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, designArtifactDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designArtifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDesignArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designArtifact.setId(longCount.incrementAndGet());

        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignArtifactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designArtifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDesignArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designArtifact.setId(longCount.incrementAndGet());

        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignArtifactMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDesignArtifactWithPatch() throws Exception {
        // Initialize the database
        insertedDesignArtifact = designArtifactRepository.saveAndFlush(designArtifact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designArtifact using partial update
        DesignArtifact partialUpdatedDesignArtifact = new DesignArtifact();
        partialUpdatedDesignArtifact.setId(designArtifact.getId());

        partialUpdatedDesignArtifact
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .metadataJson(UPDATED_METADATA_JSON);

        restDesignArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDesignArtifact.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDesignArtifact))
            )
            .andExpect(status().isOk());

        // Validate the DesignArtifact in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDesignArtifactUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDesignArtifact, designArtifact),
            getPersistedDesignArtifact(designArtifact)
        );
    }

    @Test
    @Transactional
    void fullUpdateDesignArtifactWithPatch() throws Exception {
        // Initialize the database
        insertedDesignArtifact = designArtifactRepository.saveAndFlush(designArtifact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designArtifact using partial update
        DesignArtifact partialUpdatedDesignArtifact = new DesignArtifact();
        partialUpdatedDesignArtifact.setId(designArtifact.getId());

        partialUpdatedDesignArtifact
            .artifactType(UPDATED_ARTIFACT_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .checksum(UPDATED_CHECKSUM)
            .metadataJson(UPDATED_METADATA_JSON)
            .createdAt(UPDATED_CREATED_AT);

        restDesignArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDesignArtifact.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDesignArtifact))
            )
            .andExpect(status().isOk());

        // Validate the DesignArtifact in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDesignArtifactUpdatableFieldsEquals(partialUpdatedDesignArtifact, getPersistedDesignArtifact(partialUpdatedDesignArtifact));
    }

    @Test
    @Transactional
    void patchNonExistingDesignArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designArtifact.setId(longCount.incrementAndGet());

        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDesignArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, designArtifactDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(designArtifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDesignArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designArtifact.setId(longCount.incrementAndGet());

        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(designArtifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDesignArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designArtifact.setId(longCount.incrementAndGet());

        // Create the DesignArtifact
        DesignArtifactDTO designArtifactDTO = designArtifactMapper.toDto(designArtifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignArtifactMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(designArtifactDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DesignArtifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDesignArtifact() throws Exception {
        // Initialize the database
        insertedDesignArtifact = designArtifactRepository.saveAndFlush(designArtifact);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the designArtifact
        restDesignArtifactMockMvc
            .perform(delete(ENTITY_API_URL_ID, designArtifact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return designArtifactRepository.count();
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

    protected DesignArtifact getPersistedDesignArtifact(DesignArtifact designArtifact) {
        return designArtifactRepository.findById(designArtifact.getId()).orElseThrow();
    }

    protected void assertPersistedDesignArtifactToMatchAllProperties(DesignArtifact expectedDesignArtifact) {
        assertDesignArtifactAllPropertiesEquals(expectedDesignArtifact, getPersistedDesignArtifact(expectedDesignArtifact));
    }

    protected void assertPersistedDesignArtifactToMatchUpdatableProperties(DesignArtifact expectedDesignArtifact) {
        assertDesignArtifactAllUpdatablePropertiesEquals(expectedDesignArtifact, getPersistedDesignArtifact(expectedDesignArtifact));
    }
}
