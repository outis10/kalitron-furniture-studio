package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.repository.KitchenSpecRepository;
import com.kalitron.studio.service.criteria.KitchenSpecCriteria;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
import com.kalitron.studio.service.mapper.KitchenSpecMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link KitchenSpec} entities in the database.
 * The main input is a {@link KitchenSpecCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link KitchenSpecDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class KitchenSpecQueryService extends QueryService<KitchenSpec> {

    private static final Logger LOG = LoggerFactory.getLogger(KitchenSpecQueryService.class);

    private final KitchenSpecRepository kitchenSpecRepository;

    private final KitchenSpecMapper kitchenSpecMapper;

    public KitchenSpecQueryService(KitchenSpecRepository kitchenSpecRepository, KitchenSpecMapper kitchenSpecMapper) {
        this.kitchenSpecRepository = kitchenSpecRepository;
        this.kitchenSpecMapper = kitchenSpecMapper;
    }

    /**
     * Return a {@link List} of {@link KitchenSpecDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<KitchenSpecDTO> findByCriteria(KitchenSpecCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<KitchenSpec> specification = createSpecification(criteria);
        return kitchenSpecMapper.toDto(kitchenSpecRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(KitchenSpecCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<KitchenSpec> specification = createSpecification(criteria);
        return kitchenSpecRepository.count(specification);
    }

    /**
     * Function to convert {@link KitchenSpecCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<KitchenSpec> createSpecification(KitchenSpecCriteria criteria) {
        Specification<KitchenSpec> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), KitchenSpec_.id),
                buildSpecification(criteria.getLayout(), KitchenSpec_.layout),
                buildRangeSpecification(criteria.getTotalWidthMm(), KitchenSpec_.totalWidthMm),
                buildRangeSpecification(criteria.getTotalHeightMm(), KitchenSpec_.totalHeightMm),
                buildRangeSpecification(criteria.getTotalDepthMm(), KitchenSpec_.totalDepthMm),
                buildStringSpecification(criteria.getStyle(), KitchenSpec_.style),
                buildSpecification(criteria.getPrimaryFinish(), KitchenSpec_.primaryFinish),
                buildStringSpecification(criteria.getHandleType(), KitchenSpec_.handleType),
                buildStringSpecification(criteria.getCountertopMaterial(), KitchenSpec_.countertopMaterial),
                buildStringSpecification(criteria.getSinkPosition(), KitchenSpec_.sinkPosition),
                buildSpecification(criteria.getConfirmedByClient(), KitchenSpec_.confirmedByClient),
                buildRangeSpecification(criteria.getConfirmedAt(), KitchenSpec_.confirmedAt),
                buildSpecification(criteria.getCabinetsId(), root -> root.join(KitchenSpec_.cabinetses, JoinType.LEFT).get(Cabinet_.id)),
                buildSpecification(criteria.getPrimaryMaterialId(), root ->
                    root.join(KitchenSpec_.primaryMaterial, JoinType.LEFT).get(Material_.id)
                ),
                buildSpecification(criteria.getSessionId(), root -> root.join(KitchenSpec_.session, JoinType.LEFT).get(DesignSession_.id))
            );
        }
        return specification;
    }
}
