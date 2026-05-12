package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.CatalogStyleAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.CatalogStyle;
import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.repository.CatalogStyleRepository;
import com.kalitron.studio.service.dto.CatalogStyleDTO;
import com.kalitron.studio.service.mapper.CatalogStyleMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CatalogStyleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CatalogStyleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THUMBNAIL_PATH = "AAAAAAAAAA";
    private static final String UPDATED_THUMBNAIL_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_STYLE = "AAAAAAAAAA";
    private static final String UPDATED_STYLE = "BBBBBBBBBB";

    private static final FinishType DEFAULT_PRIMARY_FINISH = FinishType.MATTE_WHITE;
    private static final FinishType UPDATED_PRIMARY_FINISH = FinishType.MATTE_GRAY;

    private static final String DEFAULT_PRICE_RANGE = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_RANGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/catalog-styles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CatalogStyleRepository catalogStyleRepository;

    @Autowired
    private CatalogStyleMapper catalogStyleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCatalogStyleMockMvc;

    private CatalogStyle catalogStyle;

    private CatalogStyle insertedCatalogStyle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogStyle createEntity() {
        return new CatalogStyle()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .thumbnailPath(DEFAULT_THUMBNAIL_PATH)
            .style(DEFAULT_STYLE)
            .primaryFinish(DEFAULT_PRIMARY_FINISH)
            .priceRange(DEFAULT_PRICE_RANGE)
            .isActive(DEFAULT_IS_ACTIVE)
            .sortOrder(DEFAULT_SORT_ORDER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogStyle createUpdatedEntity() {
        return new CatalogStyle()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .thumbnailPath(UPDATED_THUMBNAIL_PATH)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .priceRange(UPDATED_PRICE_RANGE)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);
    }

    @BeforeEach
    void initTest() {
        catalogStyle = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCatalogStyle != null) {
            catalogStyleRepository.delete(insertedCatalogStyle);
            insertedCatalogStyle = null;
        }
    }

    @Test
    @Transactional
    void createCatalogStyle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);
        var returnedCatalogStyleDTO = om.readValue(
            restCatalogStyleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(catalogStyleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CatalogStyleDTO.class
        );

        // Validate the CatalogStyle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCatalogStyle = catalogStyleMapper.toEntity(returnedCatalogStyleDTO);
        assertCatalogStyleUpdatableFieldsEquals(returnedCatalogStyle, getPersistedCatalogStyle(returnedCatalogStyle));

        insertedCatalogStyle = returnedCatalogStyle;
    }

    @Test
    @Transactional
    void createCatalogStyleWithExistingId() throws Exception {
        // Create the CatalogStyle with an existing ID
        catalogStyle.setId(1L);
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCatalogStyleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(catalogStyleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        catalogStyle.setName(null);

        // Create the CatalogStyle, which fails.
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        restCatalogStyleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(catalogStyleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkThumbnailPathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        catalogStyle.setThumbnailPath(null);

        // Create the CatalogStyle, which fails.
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        restCatalogStyleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(catalogStyleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        catalogStyle.setIsActive(null);

        // Create the CatalogStyle, which fails.
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        restCatalogStyleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(catalogStyleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCatalogStyles() throws Exception {
        // Initialize the database
        insertedCatalogStyle = catalogStyleRepository.saveAndFlush(catalogStyle);

        // Get all the catalogStyleList
        restCatalogStyleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(catalogStyle.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailPath").value(hasItem(DEFAULT_THUMBNAIL_PATH)))
            .andExpect(jsonPath("$.[*].style").value(hasItem(DEFAULT_STYLE)))
            .andExpect(jsonPath("$.[*].primaryFinish").value(hasItem(DEFAULT_PRIMARY_FINISH.toString())))
            .andExpect(jsonPath("$.[*].priceRange").value(hasItem(DEFAULT_PRICE_RANGE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)));
    }

    @Test
    @WithAnonymousUser
    void getAllCatalogStylesIsPublicAndReturnsSeedData() throws Exception {
        restCatalogStyleMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(8))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Moderno Blanco")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Moderno Gris")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Minimalista Negro")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Madera Natural")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Rustico Pino")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Clasico Crema")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Industrial")))
            .andExpect(jsonPath("$.[*].name").value(hasItem("Escandinavo")))
            .andExpect(jsonPath("$.[*].isActive").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is(true))));
    }

    @Test
    @Transactional
    void getCatalogStyle() throws Exception {
        // Initialize the database
        insertedCatalogStyle = catalogStyleRepository.saveAndFlush(catalogStyle);

        // Get the catalogStyle
        restCatalogStyleMockMvc
            .perform(get(ENTITY_API_URL_ID, catalogStyle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(catalogStyle.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbnailPath").value(DEFAULT_THUMBNAIL_PATH))
            .andExpect(jsonPath("$.style").value(DEFAULT_STYLE))
            .andExpect(jsonPath("$.primaryFinish").value(DEFAULT_PRIMARY_FINISH.toString()))
            .andExpect(jsonPath("$.priceRange").value(DEFAULT_PRICE_RANGE))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingCatalogStyle() throws Exception {
        // Get the catalogStyle
        restCatalogStyleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCatalogStyle() throws Exception {
        // Initialize the database
        insertedCatalogStyle = catalogStyleRepository.saveAndFlush(catalogStyle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the catalogStyle
        CatalogStyle updatedCatalogStyle = catalogStyleRepository.findById(catalogStyle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCatalogStyle are not directly saved in db
        em.detach(updatedCatalogStyle);
        updatedCatalogStyle
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .thumbnailPath(UPDATED_THUMBNAIL_PATH)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .priceRange(UPDATED_PRICE_RANGE)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(updatedCatalogStyle);

        restCatalogStyleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, catalogStyleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(catalogStyleDTO))
            )
            .andExpect(status().isOk());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCatalogStyleToMatchAllProperties(updatedCatalogStyle);
    }

    @Test
    @Transactional
    void putNonExistingCatalogStyle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogStyle.setId(longCount.incrementAndGet());

        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCatalogStyleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, catalogStyleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(catalogStyleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCatalogStyle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogStyle.setId(longCount.incrementAndGet());

        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogStyleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(catalogStyleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCatalogStyle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogStyle.setId(longCount.incrementAndGet());

        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogStyleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(catalogStyleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCatalogStyleWithPatch() throws Exception {
        // Initialize the database
        insertedCatalogStyle = catalogStyleRepository.saveAndFlush(catalogStyle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the catalogStyle using partial update
        CatalogStyle partialUpdatedCatalogStyle = new CatalogStyle();
        partialUpdatedCatalogStyle.setId(catalogStyle.getId());

        partialUpdatedCatalogStyle
            .thumbnailPath(UPDATED_THUMBNAIL_PATH)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .priceRange(UPDATED_PRICE_RANGE)
            .sortOrder(UPDATED_SORT_ORDER);

        restCatalogStyleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCatalogStyle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCatalogStyle))
            )
            .andExpect(status().isOk());

        // Validate the CatalogStyle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCatalogStyleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCatalogStyle, catalogStyle),
            getPersistedCatalogStyle(catalogStyle)
        );
    }

    @Test
    @Transactional
    void fullUpdateCatalogStyleWithPatch() throws Exception {
        // Initialize the database
        insertedCatalogStyle = catalogStyleRepository.saveAndFlush(catalogStyle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the catalogStyle using partial update
        CatalogStyle partialUpdatedCatalogStyle = new CatalogStyle();
        partialUpdatedCatalogStyle.setId(catalogStyle.getId());

        partialUpdatedCatalogStyle
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .thumbnailPath(UPDATED_THUMBNAIL_PATH)
            .style(UPDATED_STYLE)
            .primaryFinish(UPDATED_PRIMARY_FINISH)
            .priceRange(UPDATED_PRICE_RANGE)
            .isActive(UPDATED_IS_ACTIVE)
            .sortOrder(UPDATED_SORT_ORDER);

        restCatalogStyleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCatalogStyle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCatalogStyle))
            )
            .andExpect(status().isOk());

        // Validate the CatalogStyle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCatalogStyleUpdatableFieldsEquals(partialUpdatedCatalogStyle, getPersistedCatalogStyle(partialUpdatedCatalogStyle));
    }

    @Test
    @Transactional
    void patchNonExistingCatalogStyle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogStyle.setId(longCount.incrementAndGet());

        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCatalogStyleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, catalogStyleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(catalogStyleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCatalogStyle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogStyle.setId(longCount.incrementAndGet());

        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogStyleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(catalogStyleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCatalogStyle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        catalogStyle.setId(longCount.incrementAndGet());

        // Create the CatalogStyle
        CatalogStyleDTO catalogStyleDTO = catalogStyleMapper.toDto(catalogStyle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogStyleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(catalogStyleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CatalogStyle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCatalogStyle() throws Exception {
        // Initialize the database
        insertedCatalogStyle = catalogStyleRepository.saveAndFlush(catalogStyle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the catalogStyle
        restCatalogStyleMockMvc
            .perform(delete(ENTITY_API_URL_ID, catalogStyle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return catalogStyleRepository.count();
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

    protected CatalogStyle getPersistedCatalogStyle(CatalogStyle catalogStyle) {
        return catalogStyleRepository.findById(catalogStyle.getId()).orElseThrow();
    }

    protected void assertPersistedCatalogStyleToMatchAllProperties(CatalogStyle expectedCatalogStyle) {
        assertCatalogStyleAllPropertiesEquals(expectedCatalogStyle, getPersistedCatalogStyle(expectedCatalogStyle));
    }

    protected void assertPersistedCatalogStyleToMatchUpdatableProperties(CatalogStyle expectedCatalogStyle) {
        assertCatalogStyleAllUpdatablePropertiesEquals(expectedCatalogStyle, getPersistedCatalogStyle(expectedCatalogStyle));
    }
}
