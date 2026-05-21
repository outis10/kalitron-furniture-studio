package com.kalitron.studio.repository;

import com.kalitron.studio.domain.KitchenSpec;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KitchenSpec entity.
 */
@Repository
public interface KitchenSpecRepository extends JpaRepository<KitchenSpec, Long>, JpaSpecificationExecutor<KitchenSpec> {
    Optional<KitchenSpec> findBySessionId(Long sessionId);

    default Optional<KitchenSpec> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<KitchenSpec> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<KitchenSpec> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select kitchenSpec from KitchenSpec kitchenSpec left join fetch kitchenSpec.primaryMaterial",
        countQuery = "select count(kitchenSpec) from KitchenSpec kitchenSpec"
    )
    Page<KitchenSpec> findAllWithToOneRelationships(Pageable pageable);

    @Query("select kitchenSpec from KitchenSpec kitchenSpec left join fetch kitchenSpec.primaryMaterial")
    List<KitchenSpec> findAllWithToOneRelationships();

    @Query("select kitchenSpec from KitchenSpec kitchenSpec left join fetch kitchenSpec.primaryMaterial where kitchenSpec.id =:id")
    Optional<KitchenSpec> findOneWithToOneRelationships(@Param("id") Long id);
}
