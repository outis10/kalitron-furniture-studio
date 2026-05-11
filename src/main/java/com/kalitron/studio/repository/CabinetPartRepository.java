package com.kalitron.studio.repository;

import com.kalitron.studio.domain.CabinetPart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CabinetPart entity.
 */
@Repository
public interface CabinetPartRepository extends JpaRepository<CabinetPart, Long>, JpaSpecificationExecutor<CabinetPart> {
    default Optional<CabinetPart> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CabinetPart> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CabinetPart> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select cabinetPart from CabinetPart cabinetPart left join fetch cabinetPart.material left join fetch cabinetPart.cabinet",
        countQuery = "select count(cabinetPart) from CabinetPart cabinetPart"
    )
    Page<CabinetPart> findAllWithToOneRelationships(Pageable pageable);

    @Query("select cabinetPart from CabinetPart cabinetPart left join fetch cabinetPart.material left join fetch cabinetPart.cabinet")
    List<CabinetPart> findAllWithToOneRelationships();

    @Query(
        "select cabinetPart from CabinetPart cabinetPart left join fetch cabinetPart.material left join fetch cabinetPart.cabinet where cabinetPart.id =:id"
    )
    Optional<CabinetPart> findOneWithToOneRelationships(@Param("id") Long id);
}
