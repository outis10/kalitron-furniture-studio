package com.kalitron.studio.repository;

import com.kalitron.studio.domain.Cabinet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cabinet entity.
 */
@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long>, JpaSpecificationExecutor<Cabinet> {
    default Optional<Cabinet> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Cabinet> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Cabinet> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select cabinet from Cabinet cabinet left join fetch cabinet.template left join fetch cabinet.material left join fetch cabinet.spec",
        countQuery = "select count(cabinet) from Cabinet cabinet"
    )
    Page<Cabinet> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select cabinet from Cabinet cabinet left join fetch cabinet.template left join fetch cabinet.material left join fetch cabinet.spec"
    )
    List<Cabinet> findAllWithToOneRelationships();

    @Query(
        "select cabinet from Cabinet cabinet left join fetch cabinet.template left join fetch cabinet.material left join fetch cabinet.spec where cabinet.id =:id"
    )
    Optional<Cabinet> findOneWithToOneRelationships(@Param("id") Long id);
}
