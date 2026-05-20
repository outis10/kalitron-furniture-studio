package com.kalitron.studio.repository;

import com.kalitron.studio.domain.DesignArtifact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DesignArtifact entity.
 */
@Repository
public interface DesignArtifactRepository extends JpaRepository<DesignArtifact, Long> {
    Optional<DesignArtifact> findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(Long sessionId, String fileName);

    default Optional<DesignArtifact> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DesignArtifact> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DesignArtifact> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select designArtifact from DesignArtifact designArtifact left join fetch designArtifact.session",
        countQuery = "select count(designArtifact) from DesignArtifact designArtifact"
    )
    Page<DesignArtifact> findAllWithToOneRelationships(Pageable pageable);

    @Query("select designArtifact from DesignArtifact designArtifact left join fetch designArtifact.session")
    List<DesignArtifact> findAllWithToOneRelationships();

    @Query("select designArtifact from DesignArtifact designArtifact left join fetch designArtifact.session where designArtifact.id =:id")
    Optional<DesignArtifact> findOneWithToOneRelationships(@Param("id") Long id);
}
