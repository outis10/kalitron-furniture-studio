package com.kalitron.studio.web.rest;

import static com.kalitron.studio.domain.QuoteAsserts.*;
import static com.kalitron.studio.web.rest.TestUtil.createUpdateProxyForBean;
import static com.kalitron.studio.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.Quote;
import com.kalitron.studio.domain.enumeration.QuoteStatus;
import com.kalitron.studio.repository.QuoteRepository;
import com.kalitron.studio.service.QuoteService;
import com.kalitron.studio.service.dto.QuoteDTO;
import com.kalitron.studio.service.mapper.QuoteMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link QuoteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuoteResourceIT {

    private static final String DEFAULT_QUOTE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_QUOTE_NUMBER = "BBBBBBBBBB";

    private static final QuoteStatus DEFAULT_STATUS = QuoteStatus.DRAFT;
    private static final QuoteStatus UPDATED_STATUS = QuoteStatus.SENT;

    private static final BigDecimal DEFAULT_SUBTOTAL_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_SUBTOTAL_MXN = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TAX_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_TAX_MXN = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_MXN = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LABOR_MXN = new BigDecimal(1);
    private static final BigDecimal UPDATED_LABOR_MXN = new BigDecimal(2);
    private static final BigDecimal SMALLER_LABOR_MXN = new BigDecimal(1 - 1);

    private static final LocalDate DEFAULT_VALID_UNTIL = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALID_UNTIL = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_VALID_UNTIL = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_PUBLIC_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_PUBLIC_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/quotes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteRepository quoteRepositoryMock;

    @Autowired
    private QuoteMapper quoteMapper;

    @Mock
    private QuoteService quoteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuoteMockMvc;

    private Quote quote;

    private Quote insertedQuote;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quote createEntity(EntityManager em) {
        Quote quote = new Quote()
            .quoteNumber(DEFAULT_QUOTE_NUMBER)
            .status(DEFAULT_STATUS)
            .subtotalMxn(DEFAULT_SUBTOTAL_MXN)
            .taxMxn(DEFAULT_TAX_MXN)
            .totalMxn(DEFAULT_TOTAL_MXN)
            .laborMxn(DEFAULT_LABOR_MXN)
            .validUntil(DEFAULT_VALID_UNTIL)
            .publicToken(DEFAULT_PUBLIC_TOKEN)
            .notes(DEFAULT_NOTES)
            .createdAt(DEFAULT_CREATED_AT)
            .sentAt(DEFAULT_SENT_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        quote.setSession(designSession);
        return quote;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quote createUpdatedEntity(EntityManager em) {
        Quote updatedQuote = new Quote()
            .quoteNumber(UPDATED_QUOTE_NUMBER)
            .status(UPDATED_STATUS)
            .subtotalMxn(UPDATED_SUBTOTAL_MXN)
            .taxMxn(UPDATED_TAX_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .laborMxn(UPDATED_LABOR_MXN)
            .validUntil(UPDATED_VALID_UNTIL)
            .publicToken(UPDATED_PUBLIC_TOKEN)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT)
            .sentAt(UPDATED_SENT_AT);
        // Add required entity
        DesignSession designSession;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            designSession = DesignSessionResourceIT.createUpdatedEntity();
            em.persist(designSession);
            em.flush();
        } else {
            designSession = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        updatedQuote.setSession(designSession);
        return updatedQuote;
    }

    @BeforeEach
    void initTest() {
        quote = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedQuote != null) {
            quoteRepository.delete(insertedQuote);
            insertedQuote = null;
        }
    }

    @Test
    @Transactional
    void createQuote() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        var returnedQuoteDTO = om.readValue(
            restQuoteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuoteDTO.class
        );

        // Validate the Quote in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuote = quoteMapper.toEntity(returnedQuoteDTO);
        assertQuoteUpdatableFieldsEquals(returnedQuote, getPersistedQuote(returnedQuote));

        insertedQuote = returnedQuote;
    }

    @Test
    @Transactional
    void createQuoteWithExistingId() throws Exception {
        // Create the Quote with an existing ID
        quote.setId(1L);
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuoteNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quote.setQuoteNumber(null);

        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quote.setStatus(null);

        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubtotalMxnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quote.setSubtotalMxn(null);

        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTaxMxnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quote.setTaxMxn(null);

        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalMxnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quote.setTotalMxn(null);

        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        quote.setCreatedAt(null);

        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        restQuoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuotes() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList
        restQuoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quote.getId().intValue())))
            .andExpect(jsonPath("$.[*].quoteNumber").value(hasItem(DEFAULT_QUOTE_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].subtotalMxn").value(hasItem(sameNumber(DEFAULT_SUBTOTAL_MXN))))
            .andExpect(jsonPath("$.[*].taxMxn").value(hasItem(sameNumber(DEFAULT_TAX_MXN))))
            .andExpect(jsonPath("$.[*].totalMxn").value(hasItem(sameNumber(DEFAULT_TOTAL_MXN))))
            .andExpect(jsonPath("$.[*].laborMxn").value(hasItem(sameNumber(DEFAULT_LABOR_MXN))))
            .andExpect(jsonPath("$.[*].validUntil").value(hasItem(DEFAULT_VALID_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].publicToken").value(hasItem(DEFAULT_PUBLIC_TOKEN)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuotesWithEagerRelationshipsIsEnabled() throws Exception {
        when(quoteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuoteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quoteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuotesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quoteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuoteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quoteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuote() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get the quote
        restQuoteMockMvc
            .perform(get(ENTITY_API_URL_ID, quote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quote.getId().intValue()))
            .andExpect(jsonPath("$.quoteNumber").value(DEFAULT_QUOTE_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.subtotalMxn").value(sameNumber(DEFAULT_SUBTOTAL_MXN)))
            .andExpect(jsonPath("$.taxMxn").value(sameNumber(DEFAULT_TAX_MXN)))
            .andExpect(jsonPath("$.totalMxn").value(sameNumber(DEFAULT_TOTAL_MXN)))
            .andExpect(jsonPath("$.laborMxn").value(sameNumber(DEFAULT_LABOR_MXN)))
            .andExpect(jsonPath("$.validUntil").value(DEFAULT_VALID_UNTIL.toString()))
            .andExpect(jsonPath("$.publicToken").value(DEFAULT_PUBLIC_TOKEN))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()));
    }

    @Test
    @Transactional
    void getQuotesByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        Long id = quote.getId();

        defaultQuoteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuoteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuoteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuotesByQuoteNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where quoteNumber equals to
        defaultQuoteFiltering("quoteNumber.equals=" + DEFAULT_QUOTE_NUMBER, "quoteNumber.equals=" + UPDATED_QUOTE_NUMBER);
    }

    @Test
    @Transactional
    void getAllQuotesByQuoteNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where quoteNumber in
        defaultQuoteFiltering(
            "quoteNumber.in=" + DEFAULT_QUOTE_NUMBER + "," + UPDATED_QUOTE_NUMBER,
            "quoteNumber.in=" + UPDATED_QUOTE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllQuotesByQuoteNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where quoteNumber is not null
        defaultQuoteFiltering("quoteNumber.specified=true", "quoteNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByQuoteNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where quoteNumber contains
        defaultQuoteFiltering("quoteNumber.contains=" + DEFAULT_QUOTE_NUMBER, "quoteNumber.contains=" + UPDATED_QUOTE_NUMBER);
    }

    @Test
    @Transactional
    void getAllQuotesByQuoteNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where quoteNumber does not contain
        defaultQuoteFiltering("quoteNumber.doesNotContain=" + UPDATED_QUOTE_NUMBER, "quoteNumber.doesNotContain=" + DEFAULT_QUOTE_NUMBER);
    }

    @Test
    @Transactional
    void getAllQuotesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where status equals to
        defaultQuoteFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllQuotesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where status in
        defaultQuoteFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllQuotesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where status is not null
        defaultQuoteFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn equals to
        defaultQuoteFiltering("subtotalMxn.equals=" + DEFAULT_SUBTOTAL_MXN, "subtotalMxn.equals=" + UPDATED_SUBTOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn in
        defaultQuoteFiltering(
            "subtotalMxn.in=" + DEFAULT_SUBTOTAL_MXN + "," + UPDATED_SUBTOTAL_MXN,
            "subtotalMxn.in=" + UPDATED_SUBTOTAL_MXN
        );
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn is not null
        defaultQuoteFiltering("subtotalMxn.specified=true", "subtotalMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn is greater than or equal to
        defaultQuoteFiltering(
            "subtotalMxn.greaterThanOrEqual=" + DEFAULT_SUBTOTAL_MXN,
            "subtotalMxn.greaterThanOrEqual=" + UPDATED_SUBTOTAL_MXN
        );
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn is less than or equal to
        defaultQuoteFiltering("subtotalMxn.lessThanOrEqual=" + DEFAULT_SUBTOTAL_MXN, "subtotalMxn.lessThanOrEqual=" + SMALLER_SUBTOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn is less than
        defaultQuoteFiltering("subtotalMxn.lessThan=" + UPDATED_SUBTOTAL_MXN, "subtotalMxn.lessThan=" + DEFAULT_SUBTOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesBySubtotalMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where subtotalMxn is greater than
        defaultQuoteFiltering("subtotalMxn.greaterThan=" + SMALLER_SUBTOTAL_MXN, "subtotalMxn.greaterThan=" + DEFAULT_SUBTOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn equals to
        defaultQuoteFiltering("taxMxn.equals=" + DEFAULT_TAX_MXN, "taxMxn.equals=" + UPDATED_TAX_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn in
        defaultQuoteFiltering("taxMxn.in=" + DEFAULT_TAX_MXN + "," + UPDATED_TAX_MXN, "taxMxn.in=" + UPDATED_TAX_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn is not null
        defaultQuoteFiltering("taxMxn.specified=true", "taxMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn is greater than or equal to
        defaultQuoteFiltering("taxMxn.greaterThanOrEqual=" + DEFAULT_TAX_MXN, "taxMxn.greaterThanOrEqual=" + UPDATED_TAX_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn is less than or equal to
        defaultQuoteFiltering("taxMxn.lessThanOrEqual=" + DEFAULT_TAX_MXN, "taxMxn.lessThanOrEqual=" + SMALLER_TAX_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn is less than
        defaultQuoteFiltering("taxMxn.lessThan=" + UPDATED_TAX_MXN, "taxMxn.lessThan=" + DEFAULT_TAX_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTaxMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where taxMxn is greater than
        defaultQuoteFiltering("taxMxn.greaterThan=" + SMALLER_TAX_MXN, "taxMxn.greaterThan=" + DEFAULT_TAX_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn equals to
        defaultQuoteFiltering("totalMxn.equals=" + DEFAULT_TOTAL_MXN, "totalMxn.equals=" + UPDATED_TOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn in
        defaultQuoteFiltering("totalMxn.in=" + DEFAULT_TOTAL_MXN + "," + UPDATED_TOTAL_MXN, "totalMxn.in=" + UPDATED_TOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn is not null
        defaultQuoteFiltering("totalMxn.specified=true", "totalMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn is greater than or equal to
        defaultQuoteFiltering("totalMxn.greaterThanOrEqual=" + DEFAULT_TOTAL_MXN, "totalMxn.greaterThanOrEqual=" + UPDATED_TOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn is less than or equal to
        defaultQuoteFiltering("totalMxn.lessThanOrEqual=" + DEFAULT_TOTAL_MXN, "totalMxn.lessThanOrEqual=" + SMALLER_TOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn is less than
        defaultQuoteFiltering("totalMxn.lessThan=" + UPDATED_TOTAL_MXN, "totalMxn.lessThan=" + DEFAULT_TOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByTotalMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where totalMxn is greater than
        defaultQuoteFiltering("totalMxn.greaterThan=" + SMALLER_TOTAL_MXN, "totalMxn.greaterThan=" + DEFAULT_TOTAL_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn equals to
        defaultQuoteFiltering("laborMxn.equals=" + DEFAULT_LABOR_MXN, "laborMxn.equals=" + UPDATED_LABOR_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn in
        defaultQuoteFiltering("laborMxn.in=" + DEFAULT_LABOR_MXN + "," + UPDATED_LABOR_MXN, "laborMxn.in=" + UPDATED_LABOR_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn is not null
        defaultQuoteFiltering("laborMxn.specified=true", "laborMxn.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn is greater than or equal to
        defaultQuoteFiltering("laborMxn.greaterThanOrEqual=" + DEFAULT_LABOR_MXN, "laborMxn.greaterThanOrEqual=" + UPDATED_LABOR_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn is less than or equal to
        defaultQuoteFiltering("laborMxn.lessThanOrEqual=" + DEFAULT_LABOR_MXN, "laborMxn.lessThanOrEqual=" + SMALLER_LABOR_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn is less than
        defaultQuoteFiltering("laborMxn.lessThan=" + UPDATED_LABOR_MXN, "laborMxn.lessThan=" + DEFAULT_LABOR_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByLaborMxnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where laborMxn is greater than
        defaultQuoteFiltering("laborMxn.greaterThan=" + SMALLER_LABOR_MXN, "laborMxn.greaterThan=" + DEFAULT_LABOR_MXN);
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil equals to
        defaultQuoteFiltering("validUntil.equals=" + DEFAULT_VALID_UNTIL, "validUntil.equals=" + UPDATED_VALID_UNTIL);
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil in
        defaultQuoteFiltering("validUntil.in=" + DEFAULT_VALID_UNTIL + "," + UPDATED_VALID_UNTIL, "validUntil.in=" + UPDATED_VALID_UNTIL);
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil is not null
        defaultQuoteFiltering("validUntil.specified=true", "validUntil.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil is greater than or equal to
        defaultQuoteFiltering(
            "validUntil.greaterThanOrEqual=" + DEFAULT_VALID_UNTIL,
            "validUntil.greaterThanOrEqual=" + UPDATED_VALID_UNTIL
        );
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil is less than or equal to
        defaultQuoteFiltering("validUntil.lessThanOrEqual=" + DEFAULT_VALID_UNTIL, "validUntil.lessThanOrEqual=" + SMALLER_VALID_UNTIL);
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil is less than
        defaultQuoteFiltering("validUntil.lessThan=" + UPDATED_VALID_UNTIL, "validUntil.lessThan=" + DEFAULT_VALID_UNTIL);
    }

    @Test
    @Transactional
    void getAllQuotesByValidUntilIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where validUntil is greater than
        defaultQuoteFiltering("validUntil.greaterThan=" + SMALLER_VALID_UNTIL, "validUntil.greaterThan=" + DEFAULT_VALID_UNTIL);
    }

    @Test
    @Transactional
    void getAllQuotesByPublicTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where publicToken equals to
        defaultQuoteFiltering("publicToken.equals=" + DEFAULT_PUBLIC_TOKEN, "publicToken.equals=" + UPDATED_PUBLIC_TOKEN);
    }

    @Test
    @Transactional
    void getAllQuotesByPublicTokenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where publicToken in
        defaultQuoteFiltering(
            "publicToken.in=" + DEFAULT_PUBLIC_TOKEN + "," + UPDATED_PUBLIC_TOKEN,
            "publicToken.in=" + UPDATED_PUBLIC_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllQuotesByPublicTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where publicToken is not null
        defaultQuoteFiltering("publicToken.specified=true", "publicToken.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByPublicTokenContainsSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where publicToken contains
        defaultQuoteFiltering("publicToken.contains=" + DEFAULT_PUBLIC_TOKEN, "publicToken.contains=" + UPDATED_PUBLIC_TOKEN);
    }

    @Test
    @Transactional
    void getAllQuotesByPublicTokenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where publicToken does not contain
        defaultQuoteFiltering("publicToken.doesNotContain=" + UPDATED_PUBLIC_TOKEN, "publicToken.doesNotContain=" + DEFAULT_PUBLIC_TOKEN);
    }

    @Test
    @Transactional
    void getAllQuotesByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where notes equals to
        defaultQuoteFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllQuotesByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where notes in
        defaultQuoteFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllQuotesByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where notes is not null
        defaultQuoteFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where notes contains
        defaultQuoteFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllQuotesByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where notes does not contain
        defaultQuoteFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllQuotesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where createdAt equals to
        defaultQuoteFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllQuotesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where createdAt in
        defaultQuoteFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllQuotesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where createdAt is not null
        defaultQuoteFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesBySentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where sentAt equals to
        defaultQuoteFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllQuotesBySentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where sentAt in
        defaultQuoteFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllQuotesBySentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        // Get all the quoteList where sentAt is not null
        defaultQuoteFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllQuotesByRenderImageIsEqualToSomething() throws Exception {
        DesignImage renderImage;
        if (TestUtil.findAll(em, DesignImage.class).isEmpty()) {
            quoteRepository.saveAndFlush(quote);
            renderImage = DesignImageResourceIT.createEntity(em);
        } else {
            renderImage = TestUtil.findAll(em, DesignImage.class).get(0);
        }
        em.persist(renderImage);
        em.flush();
        quote.setRenderImage(renderImage);
        quoteRepository.saveAndFlush(quote);
        Long renderImageId = renderImage.getId();
        // Get all the quoteList where renderImage equals to renderImageId
        defaultQuoteShouldBeFound("renderImageId.equals=" + renderImageId);

        // Get all the quoteList where renderImage equals to (renderImageId + 1)
        defaultQuoteShouldNotBeFound("renderImageId.equals=" + (renderImageId + 1));
    }

    @Test
    @Transactional
    void getAllQuotesByPdfArtifactIsEqualToSomething() throws Exception {
        DesignArtifact pdfArtifact;
        if (TestUtil.findAll(em, DesignArtifact.class).isEmpty()) {
            quoteRepository.saveAndFlush(quote);
            pdfArtifact = DesignArtifactResourceIT.createEntity(em);
        } else {
            pdfArtifact = TestUtil.findAll(em, DesignArtifact.class).get(0);
        }
        em.persist(pdfArtifact);
        em.flush();
        quote.setPdfArtifact(pdfArtifact);
        quoteRepository.saveAndFlush(quote);
        Long pdfArtifactId = pdfArtifact.getId();
        // Get all the quoteList where pdfArtifact equals to pdfArtifactId
        defaultQuoteShouldBeFound("pdfArtifactId.equals=" + pdfArtifactId);

        // Get all the quoteList where pdfArtifact equals to (pdfArtifactId + 1)
        defaultQuoteShouldNotBeFound("pdfArtifactId.equals=" + (pdfArtifactId + 1));
    }

    @Test
    @Transactional
    void getAllQuotesBySessionIsEqualToSomething() throws Exception {
        DesignSession session;
        if (TestUtil.findAll(em, DesignSession.class).isEmpty()) {
            quoteRepository.saveAndFlush(quote);
            session = DesignSessionResourceIT.createEntity();
        } else {
            session = TestUtil.findAll(em, DesignSession.class).get(0);
        }
        em.persist(session);
        em.flush();
        quote.setSession(session);
        quoteRepository.saveAndFlush(quote);
        Long sessionId = session.getId();
        // Get all the quoteList where session equals to sessionId
        defaultQuoteShouldBeFound("sessionId.equals=" + sessionId);

        // Get all the quoteList where session equals to (sessionId + 1)
        defaultQuoteShouldNotBeFound("sessionId.equals=" + (sessionId + 1));
    }

    private void defaultQuoteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuoteShouldBeFound(shouldBeFound);
        defaultQuoteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuoteShouldBeFound(String filter) throws Exception {
        restQuoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quote.getId().intValue())))
            .andExpect(jsonPath("$.[*].quoteNumber").value(hasItem(DEFAULT_QUOTE_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].subtotalMxn").value(hasItem(sameNumber(DEFAULT_SUBTOTAL_MXN))))
            .andExpect(jsonPath("$.[*].taxMxn").value(hasItem(sameNumber(DEFAULT_TAX_MXN))))
            .andExpect(jsonPath("$.[*].totalMxn").value(hasItem(sameNumber(DEFAULT_TOTAL_MXN))))
            .andExpect(jsonPath("$.[*].laborMxn").value(hasItem(sameNumber(DEFAULT_LABOR_MXN))))
            .andExpect(jsonPath("$.[*].validUntil").value(hasItem(DEFAULT_VALID_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].publicToken").value(hasItem(DEFAULT_PUBLIC_TOKEN)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())));

        // Check, that the count call also returns 1
        restQuoteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuoteShouldNotBeFound(String filter) throws Exception {
        restQuoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuoteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuote() throws Exception {
        // Get the quote
        restQuoteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuote() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quote
        Quote updatedQuote = quoteRepository.findById(quote.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuote are not directly saved in db
        em.detach(updatedQuote);
        updatedQuote
            .quoteNumber(UPDATED_QUOTE_NUMBER)
            .status(UPDATED_STATUS)
            .subtotalMxn(UPDATED_SUBTOTAL_MXN)
            .taxMxn(UPDATED_TAX_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .laborMxn(UPDATED_LABOR_MXN)
            .validUntil(UPDATED_VALID_UNTIL)
            .publicToken(UPDATED_PUBLIC_TOKEN)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT)
            .sentAt(UPDATED_SENT_AT);
        QuoteDTO quoteDTO = quoteMapper.toDto(updatedQuote);

        restQuoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quoteDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuoteToMatchAllProperties(updatedQuote);
    }

    @Test
    @Transactional
    void putNonExistingQuote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quote.setId(longCount.incrementAndGet());

        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quoteDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quote.setId(longCount.incrementAndGet());

        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quote.setId(longCount.incrementAndGet());

        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuoteWithPatch() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quote using partial update
        Quote partialUpdatedQuote = new Quote();
        partialUpdatedQuote.setId(quote.getId());

        partialUpdatedQuote
            .quoteNumber(UPDATED_QUOTE_NUMBER)
            .status(UPDATED_STATUS)
            .taxMxn(UPDATED_TAX_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .laborMxn(UPDATED_LABOR_MXN)
            .notes(UPDATED_NOTES)
            .sentAt(UPDATED_SENT_AT);

        restQuoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuote.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuote))
            )
            .andExpect(status().isOk());

        // Validate the Quote in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuoteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedQuote, quote), getPersistedQuote(quote));
    }

    @Test
    @Transactional
    void fullUpdateQuoteWithPatch() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quote using partial update
        Quote partialUpdatedQuote = new Quote();
        partialUpdatedQuote.setId(quote.getId());

        partialUpdatedQuote
            .quoteNumber(UPDATED_QUOTE_NUMBER)
            .status(UPDATED_STATUS)
            .subtotalMxn(UPDATED_SUBTOTAL_MXN)
            .taxMxn(UPDATED_TAX_MXN)
            .totalMxn(UPDATED_TOTAL_MXN)
            .laborMxn(UPDATED_LABOR_MXN)
            .validUntil(UPDATED_VALID_UNTIL)
            .publicToken(UPDATED_PUBLIC_TOKEN)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT)
            .sentAt(UPDATED_SENT_AT);

        restQuoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuote.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuote))
            )
            .andExpect(status().isOk());

        // Validate the Quote in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuoteUpdatableFieldsEquals(partialUpdatedQuote, getPersistedQuote(partialUpdatedQuote));
    }

    @Test
    @Transactional
    void patchNonExistingQuote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quote.setId(longCount.incrementAndGet());

        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quoteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quote.setId(longCount.incrementAndGet());

        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quoteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuote() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quote.setId(longCount.incrementAndGet());

        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuoteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quoteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quote in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuote() throws Exception {
        // Initialize the database
        insertedQuote = quoteRepository.saveAndFlush(quote);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quote
        restQuoteMockMvc
            .perform(delete(ENTITY_API_URL_ID, quote.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quoteRepository.count();
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

    protected Quote getPersistedQuote(Quote quote) {
        return quoteRepository.findById(quote.getId()).orElseThrow();
    }

    protected void assertPersistedQuoteToMatchAllProperties(Quote expectedQuote) {
        assertQuoteAllPropertiesEquals(expectedQuote, getPersistedQuote(expectedQuote));
    }

    protected void assertPersistedQuoteToMatchUpdatableProperties(Quote expectedQuote) {
        assertQuoteAllUpdatablePropertiesEquals(expectedQuote, getPersistedQuote(expectedQuote));
    }
}
