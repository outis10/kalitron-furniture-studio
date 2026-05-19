package com.kalitron.studio.repository;

import com.kalitron.studio.domain.DesignSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DesignSession entity.
 */
@Repository
public interface DesignSessionRepository extends JpaRepository<DesignSession, Long>, JpaSpecificationExecutor<DesignSession> {
    long countBySessionCodeStartingWith(String sessionCodePrefix);

    boolean existsBySessionCode(String sessionCode);

    Optional<DesignSession> findBySessionCode(String sessionCode);

    List<DesignSession> findAllByOrderByUpdatedAtDesc();

    default Optional<DesignSession> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DesignSession> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DesignSession> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select designSession from DesignSession designSession left join fetch designSession.spec left join fetch designSession.catalogStyle",
        countQuery = "select count(designSession) from DesignSession designSession"
    )
    Page<DesignSession> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select designSession from DesignSession designSession left join fetch designSession.spec left join fetch designSession.catalogStyle"
    )
    List<DesignSession> findAllWithToOneRelationships();

    @Query(
        "select designSession from DesignSession designSession left join fetch designSession.spec left join fetch designSession.catalogStyle where designSession.id =:id"
    )
    Optional<DesignSession> findOneWithToOneRelationships(@Param("id") Long id);
}
