package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.CabinetPart;
import com.kalitron.studio.repository.CabinetPartRepository;
import com.kalitron.studio.service.criteria.CabinetPartCriteria;
import com.kalitron.studio.service.dto.CabinetPartDTO;
import com.kalitron.studio.service.mapper.CabinetPartMapper;
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
 * Service for executing complex queries for {@link CabinetPart} entities in the database.
 * The main input is a {@link CabinetPartCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CabinetPartDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CabinetPartQueryService extends QueryService<CabinetPart> {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetPartQueryService.class);

    private final CabinetPartRepository cabinetPartRepository;

    private final CabinetPartMapper cabinetPartMapper;

    public CabinetPartQueryService(CabinetPartRepository cabinetPartRepository, CabinetPartMapper cabinetPartMapper) {
        this.cabinetPartRepository = cabinetPartRepository;
        this.cabinetPartMapper = cabinetPartMapper;
    }

    /**
     * Return a {@link Page} of {@link CabinetPartDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CabinetPartDTO> findByCriteria(CabinetPartCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CabinetPart> specification = createSpecification(criteria);
        return cabinetPartRepository.findAll(specification, page).map(cabinetPartMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CabinetPartCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CabinetPart> specification = createSpecification(criteria);
        return cabinetPartRepository.count(specification);
    }

    /**
     * Function to convert {@link CabinetPartCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CabinetPart> createSpecification(CabinetPartCriteria criteria) {
        Specification<CabinetPart> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), CabinetPart_.id),
                buildStringSpecification(criteria.getPartCode(), CabinetPart_.partCode),
                buildStringSpecification(criteria.getName(), CabinetPart_.name),
                buildRangeSpecification(criteria.getWidthMm(), CabinetPart_.widthMm),
                buildRangeSpecification(criteria.getHeightMm(), CabinetPart_.heightMm),
                buildRangeSpecification(criteria.getThicknessMm(), CabinetPart_.thicknessMm),
                buildRangeSpecification(criteria.getQuantity(), CabinetPart_.quantity),
                buildStringSpecification(criteria.getEdgeBanding(), CabinetPart_.edgeBanding),
                buildStringSpecification(criteria.getGrainDirection(), CabinetPart_.grainDirection),
                buildStringSpecification(criteria.getCncOperation(), CabinetPart_.cncOperation),
                buildStringSpecification(criteria.getNotes(), CabinetPart_.notes),
                buildSpecification(criteria.getMaterialId(), root -> root.join(CabinetPart_.material, JoinType.LEFT).get(Material_.id)),
                buildSpecification(criteria.getCabinetId(), root -> root.join(CabinetPart_.cabinet, JoinType.LEFT).get(Cabinet_.id))
            );
        }
        return specification;
    }
}
