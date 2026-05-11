package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.repository.CabinetTemplateRepository;
import com.kalitron.studio.service.criteria.CabinetTemplateCriteria;
import com.kalitron.studio.service.dto.CabinetTemplateDTO;
import com.kalitron.studio.service.mapper.CabinetTemplateMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CabinetTemplate} entities in the database.
 * The main input is a {@link CabinetTemplateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CabinetTemplateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CabinetTemplateQueryService extends QueryService<CabinetTemplate> {

    private static final Logger LOG = LoggerFactory.getLogger(CabinetTemplateQueryService.class);

    private final CabinetTemplateRepository cabinetTemplateRepository;

    private final CabinetTemplateMapper cabinetTemplateMapper;

    public CabinetTemplateQueryService(CabinetTemplateRepository cabinetTemplateRepository, CabinetTemplateMapper cabinetTemplateMapper) {
        this.cabinetTemplateRepository = cabinetTemplateRepository;
        this.cabinetTemplateMapper = cabinetTemplateMapper;
    }

    /**
     * Return a {@link List} of {@link CabinetTemplateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CabinetTemplateDTO> findByCriteria(CabinetTemplateCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<CabinetTemplate> specification = createSpecification(criteria);
        return cabinetTemplateMapper.toDto(cabinetTemplateRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CabinetTemplateCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CabinetTemplate> specification = createSpecification(criteria);
        return cabinetTemplateRepository.count(specification);
    }

    /**
     * Function to convert {@link CabinetTemplateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CabinetTemplate> createSpecification(CabinetTemplateCriteria criteria) {
        Specification<CabinetTemplate> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), CabinetTemplate_.id),
                buildStringSpecification(criteria.getCode(), CabinetTemplate_.code),
                buildStringSpecification(criteria.getName(), CabinetTemplate_.name),
                buildSpecification(criteria.getCategory(), CabinetTemplate_.category),
                buildRangeSpecification(criteria.getDefaultWidthMm(), CabinetTemplate_.defaultWidthMm),
                buildRangeSpecification(criteria.getDefaultHeightMm(), CabinetTemplate_.defaultHeightMm),
                buildRangeSpecification(criteria.getDefaultDepthMm(), CabinetTemplate_.defaultDepthMm),
                buildRangeSpecification(criteria.getMinWidthMm(), CabinetTemplate_.minWidthMm),
                buildRangeSpecification(criteria.getMaxWidthMm(), CabinetTemplate_.maxWidthMm),
                buildSpecification(criteria.getSupportsDoors(), CabinetTemplate_.supportsDoors),
                buildSpecification(criteria.getSupportsDrawers(), CabinetTemplate_.supportsDrawers),
                buildSpecification(criteria.getSupportsShelves(), CabinetTemplate_.supportsShelves),
                buildStringSpecification(criteria.getFusionTemplateName(), CabinetTemplate_.fusionTemplateName),
                buildSpecification(criteria.getIsActive(), CabinetTemplate_.isActive),
                buildRangeSpecification(criteria.getSortOrder(), CabinetTemplate_.sortOrder)
            );
        }
        return specification;
    }
}
