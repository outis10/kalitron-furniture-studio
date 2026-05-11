package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.QuoteItemAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static com.kalitron.studio.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.Quote;
import com.kalitron.studio.domain.QuoteItem;
import com.kalitron.studio.repository.QuoteItemRepository;
import com.kalitron.studio.service.QuoteItemService;
import com.kalitron.studio.service.dto.QuoteItemDTO;
import com.kalitron.studio.service.mapper.QuoteItemMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link QuoteItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuoteItemResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE_MXN = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_MXN = new BigDecimal(2);

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/quote-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuoteItemRepository quoteItemRepository;

    @Mock
    private QuoteItemRepository quoteItemRepositoryMock;

    @Autowired
    private QuoteItemMapper quoteItemMapper;

    @Mock
    private QuoteItemService quoteItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuoteItemMockMvc;

    private QuoteItem quoteItem;

    private QuoteItem insertedQuoteItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuoteItem createEntity(EntityManager em) {
        QuoteItem quoteItem = new QuoteItem()
            .description(DEFAULT_DESCRIPTION)
            .quantity(DEFAULT_QUANTITY)
            .unitPriceMxn(DEFAULT_UNIT_PRICE_MXN)
            .totalMxn(DEFAULT_TOTAL_MXN)
            .category(DEFAULT_CATEGORY)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Quote quote;
        if (TestUtil.findAll(em, Quote.class).isEmpty()) {
            quote = QuoteResourceIT.createEntity(em);
            em.persist(quote);
            em.flush();
        } else {
            quote = TestUtil.findAll(em, Quote.class).get(0);
        }
        quoteItem.setQuote(quote);
        return quoteItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuoteItem createUpdatedEntity(EntityManager em) {
        QuoteItem updatedQuoteItem = new QuoteItem()
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitPriceMxn(UPDATED_UNIT_PRICE_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .category(UPDATED_CATEGORY)
            .notes(UPDATED_NOTES);
        // Add required entity
        Quote quote;
        if (TestUtil.findAll(em, Quote.class).isEmpty()) {
            quote = QuoteResourceIT.createUpdatedEntity(em);
            em.persist(quote);
            em.flush();
        } else {
            quote = TestUtil.findAll(em, Quote.class).get(0);
        }
        updatedQuoteItem.setQuote(quote);
        return updatedQuoteItem;
    }

    @BeforeEach
    void initTest() {
        quoteItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedQuoteItem != null) {
            quoteItemRepository.delete(insertedQuoteItem);
            insertedQuoteItem = null;
        }
    }

    @Test
    @Transactional
    void createQuoteItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);
        var returnedQuoteItemDTO = om.readValue(
            restQuoteItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuoteItemDTO.class
        );

        // Validate the QuoteItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuoteItem = quoteItemMapper.toEntity(returnedQuoteItemDTO);
        assertQuoteItemUpdatableFieldsEquals(returnedQuoteItem, getPersistedQuoteItem(returnedQuoteItem));

        insertedQuoteItem = returnedQuoteItem;
    }

    @Test
    @Transactional
    void createQuoteItemWithExistingId() throws Exception {
        // Create the QuoteItem with an existing ID
        quoteItem.setId(1L);
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuoteItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quoteItem.setDescription(null);

        // Create the QuoteItem, which fails.
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        restQuoteItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quoteItem.setQuantity(null);

        // Create the QuoteItem, which fails.
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        restQuoteItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceMxnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quoteItem.setUnitPriceMxn(null);

        // Create the QuoteItem, which fails.
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        restQuoteItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalMxnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quoteItem.setTotalMxn(null);

        // Create the QuoteItem, which fails.
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        restQuoteItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuoteItems() throws Exception {
        // Initialize the database
        insertedQuoteItem = quoteItemRepository.saveAndFlush(quoteItem);

        // Get all the quoteItemList
        restQuoteItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quoteItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPriceMxn").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE_MXN))))
            .andExpect(jsonPath("$.[*].totalMxn").value(hasItem(sameNumber(DEFAULT_TOTAL_MXN))))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuoteItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(quoteItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuoteItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quoteItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuoteItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quoteItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuoteItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quoteItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuoteItem() throws Exception {
        // Initialize the database
        insertedQuoteItem = quoteItemRepository.saveAndFlush(quoteItem);

        // Get the quoteItem
        restQuoteItemMockMvc
            .perform(get(ENTITY_API_URL_ID, quoteItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quoteItem.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPriceMxn").value(sameNumber(DEFAULT_UNIT_PRICE_MXN)))
            .andExpect(jsonPath("$.totalMxn").value(sameNumber(DEFAULT_TOTAL_MXN)))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingQuoteItem() throws Exception {
        // Get the quoteItem
        restQuoteItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuoteItem() throws Exception {
        // Initialize the database
        insertedQuoteItem = quoteItemRepository.saveAndFlush(quoteItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quoteItem
        QuoteItem updatedQuoteItem = quoteItemRepository.findById(quoteItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuoteItem are not directly saved in db
        em.detach(updatedQuoteItem);
        updatedQuoteItem
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitPriceMxn(UPDATED_UNIT_PRICE_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .category(UPDATED_CATEGORY)
            .notes(UPDATED_NOTES);
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(updatedQuoteItem);

        restQuoteItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quoteItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quoteItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuoteItemToMatchAllProperties(updatedQuoteItem);
    }

    @Test
    @Transactional
    void putNonExistingQuoteItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quoteItem.setId(longCount.incrementAndGet());

        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuoteItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quoteItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quoteItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuoteItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quoteItem.setId(longCount.incrementAndGet());

        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quoteItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuoteItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quoteItem.setId(longCount.incrementAndGet());

        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuoteItemWithPatch() throws Exception {
        // Initialize the database
        insertedQuoteItem = quoteItemRepository.saveAndFlush(quoteItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quoteItem using partial update
        QuoteItem partialUpdatedQuoteItem = new QuoteItem();
        partialUpdatedQuoteItem.setId(quoteItem.getId());

        partialUpdatedQuoteItem.category(UPDATED_CATEGORY).notes(UPDATED_NOTES);

        restQuoteItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuoteItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuoteItem))
            )
            .andExpect(status().isOk());

        // Validate the QuoteItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuoteItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuoteItem, quoteItem),
            getPersistedQuoteItem(quoteItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuoteItemWithPatch() throws Exception {
        // Initialize the database
        insertedQuoteItem = quoteItemRepository.saveAndFlush(quoteItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quoteItem using partial update
        QuoteItem partialUpdatedQuoteItem = new QuoteItem();
        partialUpdatedQuoteItem.setId(quoteItem.getId());

        partialUpdatedQuoteItem
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitPriceMxn(UPDATED_UNIT_PRICE_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .category(UPDATED_CATEGORY)
            .notes(UPDATED_NOTES);

        restQuoteItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuoteItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuoteItem))
            )
            .andExpect(status().isOk());

        // Validate the QuoteItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuoteItemUpdatableFieldsEquals(partialUpdatedQuoteItem, getPersistedQuoteItem(partialUpdatedQuoteItem));
    }

    @Test
    @Transactional
    void patchNonExistingQuoteItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quoteItem.setId(longCount.incrementAndGet());

        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuoteItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quoteItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quoteItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuoteItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quoteItem.setId(longCount.incrementAndGet());

        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quoteItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuoteItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quoteItem.setId(longCount.incrementAndGet());

        // Create the QuoteItem
        QuoteItemDTO quoteItemDTO = quoteItemMapper.toDto(quoteItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quoteItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuoteItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuoteItem() throws Exception {
        // Initialize the database
        insertedQuoteItem = quoteItemRepository.saveAndFlush(quoteItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quoteItem
        restQuoteItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, quoteItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quoteItemRepository.count();
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

    protected QuoteItem getPersistedQuoteItem(QuoteItem quoteItem) {
        return quoteItemRepository.findById(quoteItem.getId()).orElseThrow();
    }

    protected void assertPersistedQuoteItemToMatchAllProperties(QuoteItem expectedQuoteItem) {
        assertQuoteItemAllPropertiesEquals(expectedQuoteItem, getPersistedQuoteItem(expectedQuoteItem));
    }

    protected void assertPersistedQuoteItemToMatchUpdatableProperties(QuoteItem expectedQuoteItem) {
        assertQuoteItemAllUpdatablePropertiesEquals(expectedQuoteItem, getPersistedQuoteItem(expectedQuoteItem));
    }
}
