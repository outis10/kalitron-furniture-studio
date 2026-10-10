package com.kalitron.studio.repository;

import com.kalitron.studio.domain.SiteMeasurement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SiteMeasurement entity.
 */
@Repository
public interface SiteMeasurementRepository extends JpaRepository<SiteMeasurement, Long> {
    @Query("select siteMeasurement from SiteMeasurement siteMeasurement where siteMeasurement.measuredBy.login = ?#{authentication.name}")
    List<SiteMeasurement> findByMeasuredByIsCurrentUser();

    default Optional<SiteMeasurement> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SiteMeasurement> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SiteMeasurement> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select siteMeasurement from SiteMeasurement siteMeasurement left join fetch siteMeasurement.session left join fetch siteMeasurement.measuredBy",
        countQuery = "select count(siteMeasurement) from SiteMeasurement siteMeasurement"
    )
    Page<SiteMeasurement> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select siteMeasurement from SiteMeasurement siteMeasurement left join fetch siteMeasurement.session left join fetch siteMeasurement.measuredBy"
    )
    List<SiteMeasurement> findAllWithToOneRelationships();

    @Query(
        "select siteMeasurement from SiteMeasurement siteMeasurement left join fetch siteMeasurement.session left join fetch siteMeasurement.measuredBy where siteMeasurement.id =:id"
    )
    Optional<SiteMeasurement> findOneWithToOneRelationships(@Param("id") Long id);
}
