package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.repository.CabinetRepository;
import com.kalitron.studio.service.criteria.CabinetCriteria;
import com.kalitron.studio.service.dto.CabinetDTO;
import com.kalitron.studio.service.mapper.CabinetMapper;
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
 * Service for executing complex queries for {@link Cabinet} entities in the database.
 * The main input is a {@link CabinetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CabinetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CabinetQueryService extends QueryService<Cabinet> {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetQueryService.class);

    private final CabinetRepository cabinetRepository;

    private final CabinetMapper cabinetMapper;

    public CabinetQueryService(CabinetRepository cabinetRepository, CabinetMapper cabinetMapper) {
        this.cabinetRepository = cabinetRepository;
        this.cabinetMapper = cabinetMapper;
    }

    /**
     * Return a {@link Page} of {@link CabinetDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CabinetDTO> findByCriteria(CabinetCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cabinet> specification = createSpecification(criteria);
        return cabinetRepository.findAll(specification, page).map(cabinetMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CabinetCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Cabinet> specification = createSpecification(criteria);
        return cabinetRepository.count(specification);
    }

    /**
     * Function to convert {@link CabinetCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Cabinet> createSpecification(CabinetCriteria criteria) {
        Specification<Cabinet> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Cabinet_.id),
                buildStringSpecification(criteria.getCabinetCode(), Cabinet_.cabinetCode),
                buildSpecification(criteria.getCategory(), Cabinet_.category),
                buildStringSpecification(criteria.getLabel(), Cabinet_.label),
                buildRangeSpecification(criteria.getWidthMm(), Cabinet_.widthMm),
                buildRangeSpecification(criteria.getHeightMm(), Cabinet_.heightMm),
                buildRangeSpecification(criteria.getDepthMm(), Cabinet_.depthMm),
                buildRangeSpecification(criteria.getDoors(), Cabinet_.doors),
                buildRangeSpecification(criteria.getDrawers(), Cabinet_.drawers),
                buildRangeSpecification(criteria.getShelves(), Cabinet_.shelves),
                buildSpecification(criteria.getFinish(), Cabinet_.finish),
                buildRangeSpecification(criteria.getPositionX(), Cabinet_.positionX),
                buildRangeSpecification(criteria.getPositionY(), Cabinet_.positionY),
                buildRangeSpecification(criteria.getPositionZ(), Cabinet_.positionZ),
                buildRangeSpecification(criteria.getRotationDeg(), Cabinet_.rotationDeg),
                buildRangeSpecification(criteria.getPositionSeq(), Cabinet_.positionSeq),
                buildStringSpecification(criteria.getNotes(), Cabinet_.notes),
                buildSpecification(criteria.getPartsId(), root -> root.join(Cabinet_.partses, JoinType.LEFT).get(CabinetPart_.id)),
                buildSpecification(criteria.getTemplateId(), root -> root.join(Cabinet_.template, JoinType.LEFT).get(CabinetTemplate_.id)),
                buildSpecification(criteria.getMaterialId(), root -> root.join(Cabinet_.material, JoinType.LEFT).get(Material_.id)),
                buildSpecification(criteria.getSpecId(), root -> root.join(Cabinet_.spec, JoinType.LEFT).get(KitchenSpec_.id))
            );
        }
        return specification;
    }
}
