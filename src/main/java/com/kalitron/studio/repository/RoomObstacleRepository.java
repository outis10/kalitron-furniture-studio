package com.kalitron.studio.repository;

import com.kalitron.studio.domain.RoomObstacle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoomObstacle entity.
 */
@Repository
public interface RoomObstacleRepository extends JpaRepository<RoomObstacle, Long> {
    List<RoomObstacle> findBySessionIdOrderByIdAsc(Long sessionId);

    void deleteBySessionId(Long sessionId);

    default Optional<RoomObstacle> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RoomObstacle> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RoomObstacle> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select roomObstacle from RoomObstacle roomObstacle left join fetch roomObstacle.session",
        countQuery = "select count(roomObstacle) from RoomObstacle roomObstacle"
    )
    Page<RoomObstacle> findAllWithToOneRelationships(Pageable pageable);

    @Query("select roomObstacle from RoomObstacle roomObstacle left join fetch roomObstacle.session")
    List<RoomObstacle> findAllWithToOneRelationships();

    @Query("select roomObstacle from RoomObstacle roomObstacle left join fetch roomObstacle.session where roomObstacle.id =:id")
    Optional<RoomObstacle> findOneWithToOneRelationships(@Param("id") Long id);
}
