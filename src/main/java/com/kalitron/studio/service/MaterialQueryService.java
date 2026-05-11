package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.repository.MaterialRepository;
import com.kalitron.studio.service.criteria.MaterialCriteria;
import com.kalitron.studio.service.dto.MaterialDTO;
import com.kalitron.studio.service.mapper.MaterialMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Material} entities in the database.
 * The main input is a {@link MaterialCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MaterialDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MaterialQueryService extends QueryService<Material> {

    private static final Logger LOG = LoggerFactory.getLogger(MaterialQueryService.class);

    private final MaterialRepository materialRepository;

    private final MaterialMapper materialMapper;

    public MaterialQueryService(MaterialRepository materialRepository, MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.materialMapper = materialMapper;
    }

    /**
     * Return a {@link List} of {@link MaterialDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MaterialDTO> findByCriteria(MaterialCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Material> specification = createSpecification(criteria);
        return materialMapper.toDto(materialRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MaterialCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Material> specification = createSpecification(criteria);
        return materialRepository.count(specification);
    }

    /**
     * Function to convert {@link MaterialCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Material> createSpecification(MaterialCriteria criteria) {
        Specification<Material> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Material_.id),
                buildStringSpecification(criteria.getCode(), Material_.code),
                buildStringSpecification(criteria.getName(), Material_.name),
                buildSpecification(criteria.getMaterialKind(), Material_.materialKind),
                buildRangeSpecification(criteria.getThicknessMm(), Material_.thicknessMm),
                buildRangeSpecification(criteria.getSheetWidthMm(), Material_.sheetWidthMm),
                buildRangeSpecification(criteria.getSheetHeightMm(), Material_.sheetHeightMm),
                buildRangeSpecification(criteria.getCostPerSheetMxn(), Material_.costPerSheetMxn),
                buildRangeSpecification(criteria.getCostPerSquareMeterMxn(), Material_.costPerSquareMeterMxn),
                buildStringSpecification(criteria.getSupplierName(), Material_.supplierName),
                buildSpecification(criteria.getIsActive(), Material_.isActive),
                buildStringSpecification(criteria.getNotes(), Material_.notes)
            );
        }
        return specification;
    }
}
