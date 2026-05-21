package com.kalitron.studio.repository;

import com.kalitron.studio.domain.Material;
import com.kalitron.studio.domain.enumeration.MaterialKind;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Material entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, JpaSpecificationExecutor<Material> {
    Optional<Material> findFirstByMaterialKindAndIsActiveTrueOrderByIdAsc(MaterialKind materialKind);

    Optional<Material> findFirstByIsActiveTrueOrderByIdAsc();
}
