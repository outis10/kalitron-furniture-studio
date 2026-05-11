package com.kalitron.studio.service;

import com.kalitron.studio.domain.*; // for static metamodels
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.service.criteria.DesignSessionCriteria;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.mapper.DesignSessionMapper;
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
 * Service for executing complex queries for {@link DesignSession} entities in the database.
 * The main input is a {@link DesignSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DesignSessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DesignSessionQueryService extends QueryService<DesignSession> {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSessionQueryService.class);

    private final DesignSessionRepository designSessionRepository;

    private final DesignSessionMapper designSessionMapper;

    public DesignSessionQueryService(DesignSessionRepository designSessionRepository, DesignSessionMapper designSessionMapper) {
        this.designSessionRepository = designSessionRepository;
        this.designSessionMapper = designSessionMapper;
    }

    /**
     * Return a {@link Page} of {@link DesignSessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DesignSessionDTO> findByCriteria(DesignSessionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DesignSession> specification = createSpecification(criteria);
        return designSessionRepository.findAll(specification, page).map(designSessionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DesignSessionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DesignSession> specification = createSpecification(criteria);
        return designSessionRepository.count(specification);
    }

    /**
     * Function to convert {@link DesignSessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DesignSession> createSpecification(DesignSessionCriteria criteria) {
        Specification<DesignSession> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), DesignSession_.id),
                buildStringSpecification(criteria.getSessionCode(), DesignSession_.sessionCode),
                buildSpecification(criteria.getProjectType(), DesignSession_.projectType),
                buildSpecification(criteria.getStatus(), DesignSession_.status),
                buildStringSpecification(criteria.getClientName(), DesignSession_.clientName),
                buildStringSpecification(criteria.getClientEmail(), DesignSession_.clientEmail),
                buildStringSpecification(criteria.getClientPhone(), DesignSession_.clientPhone),
                buildStringSpecification(criteria.getSelectedStyle(), DesignSession_.selectedStyle),
                buildStringSpecification(criteria.getNotes(), DesignSession_.notes),
                buildRangeSpecification(criteria.getCreatedAt(), DesignSession_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), DesignSession_.updatedAt),
                buildSpecification(criteria.getSpecId(), root -> root.join(DesignSession_.spec, JoinType.LEFT).get(KitchenSpec_.id)),
                buildSpecification(criteria.getMessagesId(), root ->
                    root.join(DesignSession_.messageses, JoinType.LEFT).get(ChatMessage_.id)
                ),
                buildSpecification(criteria.getImagesId(), root -> root.join(DesignSession_.imageses, JoinType.LEFT).get(DesignImage_.id)),
                buildSpecification(criteria.getArtifactsId(), root ->
                    root.join(DesignSession_.artifactses, JoinType.LEFT).get(DesignArtifact_.id)
                ),
                buildSpecification(criteria.getJobsId(), root -> root.join(DesignSession_.jobses, JoinType.LEFT).get(GenerationJob_.id)),
                buildSpecification(criteria.getQuotesId(), root -> root.join(DesignSession_.quoteses, JoinType.LEFT).get(Quote_.id)),
                buildSpecification(criteria.getWallsId(), root -> root.join(DesignSession_.wallses, JoinType.LEFT).get(RoomWall_.id)),
                buildSpecification(criteria.getObstaclesId(), root ->
                    root.join(DesignSession_.obstacleses, JoinType.LEFT).get(RoomObstacle_.id)
                ),
                buildSpecification(criteria.getCatalogStyleId(), root ->
                    root.join(DesignSession_.catalogStyle, JoinType.LEFT).get(CatalogStyle_.id)
                )
            );
        }
        return specification;
    }
}
