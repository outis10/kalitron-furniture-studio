package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.DesignImageAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.enumeration.ImageType;
import com.kalitron.studio.repository.DesignImageRepository;
import com.kalitron.studio.service.DesignImageService;
import com.kalitron.studio.service.dto.DesignImageDTO;
import com.kalitron.studio.service.mapper.DesignImageMapper;
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
 * Integration tests for the {@link DesignImageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DesignImageResourceIT {

    private static final ImageType DEFAULT_IMAGE_TYPE = ImageType.REFERENCE;
    private static final ImageType UPDATED_IMAGE_TYPE = ImageType.CATALOG;

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FILE_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE_KB = 1L;
    private static final Long UPDATED_FILE_SIZE_KB = 2L;

    private static final Integer DEFAULT_WIDTH_PX = 1;
    private static final Integer UPDATED_WIDTH_PX = 2;

    private static final Integer DEFAULT_HEIGHT_PX = 1;
    private static final Integer UPDATED_HEIGHT_PX = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_UPLOADED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOADED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/design-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DesignImageRepository designImageRepository;

    @Mock
    private DesignImageRepository designImageRepositoryMock;

    @Autowired
    private DesignImageMapper designImageMapper;

    @Mock
    private DesignImageService designImageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDesignImageMockMvc;

    private DesignImage designImage;

    private DesignImage insertedDesignImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DesignImage createEntity(EntityManager em) {
        DesignImage designImage = new DesignImage()
            .imageType(DEFAULT_IMAGE_TYPE)
            .fileName(DEFAULT_FILE_NAME)
            .filePath(DEFAULT_FILE_PATH)
            .mimeType(DEFAULT_MIME_TYPE)
            .fileSizeKb(DEFAULT_FILE_SIZE_KB)
            .widthPx(DEFAULT_WIDTH_PX)
            .heightPx(DEFAULT_HEIGHT_PX)
            .isActive(DEFAULT_IS_ACTIVE)
            .uploadedAt(DEFAULT_UPLOADED_AT)
            .description(DEFAULT_DESCRIPTION);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        designImage.setSession(designSession);
        return designImage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DesignImage createUpdatedEntity(EntityManager em) {
        DesignImage updatedDesignImage = new DesignImage()
            .imageType(UPDATED_IMAGE_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX)
            .isActive(UPDATED_IS_ACTIVE)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .description(UPDATED_DESCRIPTION);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedDesignImage.setSession(designSession);
        return updatedDesignImage;
    }

    @BeforeEach
    void initTest() {
        designImage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDesignImage != null) {
            designImageRepository.delete(insertedDesignImage);
            insertedDesignImage = null;
        }
    }

    @Test
    @Transactional
    void createDesignImage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);
        var returnedDesignImageDTO = om.readValue(
            restDesignImageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DesignImageDTO.class
        );

        // Validate the DesignImage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDesignImage = designImageMapper.toEntity(returnedDesignImageDTO);
        assertDesignImageUpdatableFieldsEquals(returnedDesignImage, getPersistedDesignImage(returnedDesignImage));

        insertedDesignImage = returnedDesignImage;
    }

    @Test
    @Transactional
    void createDesignImageWithExistingId() throws Exception {
        // Create the DesignImage with an existing ID
        designImage.setId(1L);
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDesignImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImageTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designImage.setImageType(null);

        // Create the DesignImage, which fails.
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        restDesignImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designImage.setFileName(null);

        // Create the DesignImage, which fails.
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        restDesignImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFilePathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designImage.setFilePath(null);

        // Create the DesignImage, which fails.
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        restDesignImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designImage.setIsActive(null);

        // Create the DesignImage, which fails.
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        restDesignImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUploadedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        designImage.setUploadedAt(null);

        // Create the DesignImage, which fails.
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        restDesignImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDesignImages() throws Exception {
        // Initialize the database
        insertedDesignImage = designImageRepository.saveAndFlush(designImage);

        // Get all the designImageList
        restDesignImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(designImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].fileSizeKb").value(hasItem(DEFAULT_FILE_SIZE_KB.intValue())))
            .andExpect(jsonPath("$.[*].widthPx").value(hasItem(DEFAULT_WIDTH_PX)))
            .andExpect(jsonPath("$.[*].heightPx").value(hasItem(DEFAULT_HEIGHT_PX)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].uploadedAt").value(hasItem(DEFAULT_UPLOADED_AT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDesignImagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(designImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDesignImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(designImageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDesignImagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(designImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDesignImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(designImageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDesignImage() throws Exception {
        // Initialize the database
        insertedDesignImage = designImageRepository.saveAndFlush(designImage);

        // Get the designImage
        restDesignImageMockMvc
            .perform(get(ENTITY_API_URL_ID, designImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(designImage.getId().intValue()))
            .andExpect(jsonPath("$.imageType").value(DEFAULT_IMAGE_TYPE.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.filePath").value(DEFAULT_FILE_PATH))
            .andExpect(jsonPath("$.mimeType").value(DEFAULT_MIME_TYPE))
            .andExpect(jsonPath("$.fileSizeKb").value(DEFAULT_FILE_SIZE_KB.intValue()))
            .andExpect(jsonPath("$.widthPx").value(DEFAULT_WIDTH_PX))
            .andExpect(jsonPath("$.heightPx").value(DEFAULT_HEIGHT_PX))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.uploadedAt").value(DEFAULT_UPLOADED_AT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingDesignImage() throws Exception {
        // Get the designImage
        restDesignImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDesignImage() throws Exception {
        // Initialize the database
        insertedDesignImage = designImageRepository.saveAndFlush(designImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designImage
        DesignImage updatedDesignImage = designImageRepository.findById(designImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDesignImage are not directly saved in db
        em.detach(updatedDesignImage);
        updatedDesignImage
            .imageType(UPDATED_IMAGE_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX)
            .isActive(UPDATED_IS_ACTIVE)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .description(UPDATED_DESCRIPTION);
        DesignImageDTO designImageDTO = designImageMapper.toDto(updatedDesignImage);

        restDesignImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, designImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDesignImageToMatchAllProperties(updatedDesignImage);
    }

    @Test
    @Transactional
    void putNonExistingDesignImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designImage.setId(longCount.incrementAndGet());

        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDesignImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, designImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDesignImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designImage.setId(longCount.incrementAndGet());

        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(designImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDesignImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designImage.setId(longCount.incrementAndGet());

        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDesignImageWithPatch() throws Exception {
        // Initialize the database
        insertedDesignImage = designImageRepository.saveAndFlush(designImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designImage using partial update
        DesignImage partialUpdatedDesignImage = new DesignImage();
        partialUpdatedDesignImage.setId(designImage.getId());

        partialUpdatedDesignImage
            .imageType(UPDATED_IMAGE_TYPE)
            .filePath(UPDATED_FILE_PATH)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .description(UPDATED_DESCRIPTION);

        restDesignImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDesignImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDesignImage))
            )
            .andExpect(status().isOk());

        // Validate the DesignImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDesignImageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDesignImage, designImage),
            getPersistedDesignImage(designImage)
        );
    }

    @Test
    @Transactional
    void fullUpdateDesignImageWithPatch() throws Exception {
        // Initialize the database
        insertedDesignImage = designImageRepository.saveAndFlush(designImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the designImage using partial update
        DesignImage partialUpdatedDesignImage = new DesignImage();
        partialUpdatedDesignImage.setId(designImage.getId());

        partialUpdatedDesignImage
            .imageType(UPDATED_IMAGE_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .filePath(UPDATED_FILE_PATH)
            .mimeType(UPDATED_MIME_TYPE)
            .fileSizeKb(UPDATED_FILE_SIZE_KB)
            .widthPx(UPDATED_WIDTH_PX)
            .heightPx(UPDATED_HEIGHT_PX)
            .isActive(UPDATED_IS_ACTIVE)
            .uploadedAt(UPDATED_UPLOADED_AT)
            .description(UPDATED_DESCRIPTION);

        restDesignImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDesignImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDesignImage))
            )
            .andExpect(status().isOk());

        // Validate the DesignImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDesignImageUpdatableFieldsEquals(partialUpdatedDesignImage, getPersistedDesignImage(partialUpdatedDesignImage));
    }

    @Test
    @Transactional
    void patchNonExistingDesignImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designImage.setId(longCount.incrementAndGet());

        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDesignImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, designImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(designImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDesignImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designImage.setId(longCount.incrementAndGet());

        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(designImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDesignImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        designImage.setId(longCount.incrementAndGet());

        // Create the DesignImage
        DesignImageDTO designImageDTO = designImageMapper.toDto(designImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDesignImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(designImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DesignImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDesignImage() throws Exception {
        // Initialize the database
        insertedDesignImage = designImageRepository.saveAndFlush(designImage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the designImage
        restDesignImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, designImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return designImageRepository.count();
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

    protected DesignImage getPersistedDesignImage(DesignImage designImage) {
        return designImageRepository.findById(designImage.getId()).orElseThrow();
    }

    protected void assertPersistedDesignImageToMatchAllProperties(DesignImage expectedDesignImage) {
        assertDesignImageAllPropertiesEquals(expectedDesignImage, getPersistedDesignImage(expectedDesignImage));
    }

    protected void assertPersistedDesignImageToMatchUpdatableProperties(DesignImage expectedDesignImage) {
        assertDesignImageAllUpdatablePropertiesEquals(expectedDesignImage, getPersistedDesignImage(expectedDesignImage));
    }
}
