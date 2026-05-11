package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.Quote;
import com.kalitron.studio.repository.QuoteRepository;
import com.kalitron.studio.service.criteria.QuoteCriteria;
import com.kalitron.studio.service.dto.QuoteDTO;
import com.kalitron.studio.service.mapper.QuoteMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Quote} entities in the database.
 * The main input is a {@link QuoteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link QuoteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuoteQueryService extends QueryService<Quote> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteQueryService.class);

    private final QuoteRepository quoteRepository;

    private final QuoteMapper quoteMapper;

    public QuoteQueryService(QuoteRepository quoteRepository, QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.quoteMapper = quoteMapper;
    }

    /**
     * Return a {@link Page} of {@link QuoteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuoteDTO> findByCriteria(QuoteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Quote> specification = createSpecification(criteria);
        return quoteRepository.findAll(specification, page).map(quoteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuoteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Quote> specification = createSpecification(criteria);
        return quoteRepository.count(specification);
    }

    /**
     * Function to convert {@link QuoteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Quote> createSpecification(QuoteCriteria criteria) {
        Specification<Quote> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Quote_.id),
                buildStringSpecification(criteria.getQuoteNumber(), Quote_.quoteNumber),
                buildSpecification(criteria.getStatus(), Quote_.status),
                buildRangeSpecification(criteria.getSubtotalMxn(), Quote_.subtotalMxn),
                buildRangeSpecification(criteria.getTaxMxn(), Quote_.taxMxn),
                buildRangeSpecification(criteria.getTotalMxn(), Quote_.totalMxn),
                buildRangeSpecification(criteria.getLaborMxn(), Quote_.laborMxn),
                buildRangeSpecification(criteria.getValidUntil(), Quote_.validUntil),
                buildStringSpecification(criteria.getPublicToken(), Quote_.publicToken),
                buildStringSpecification(criteria.getNotes(), Quote_.notes),
                buildRangeSpecification(criteria.getCreatedAt(), Quote_.createdAt),
                buildRangeSpecification(criteria.getSentAt(), Quote_.sentAt),
                buildSpecification(criteria.getItemsId(), root -> root.join(Quote_.itemses, JoinType.LEFT).get(QuoteItem_.id)),
                buildSpecification(criteria.getRenderImageId(), root -> root.join(Quote_.renderImage, JoinType.LEFT).get(DesignImage_.id)),
                buildSpecification(criteria.getPdfArtifactId(), root ->
                    root.join(Quote_.pdfArtifact, JoinType.LEFT).get(DesignArtifact_.id)
                ),
                buildSpecification(criteria.getSessionId(), root -> root.join(Quote_.session, JoinType.LEFT).get(DesignSession_.id))
            );
        }
        return specification;
    }
}
