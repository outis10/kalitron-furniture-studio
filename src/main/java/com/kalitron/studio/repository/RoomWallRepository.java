package com.kalitron.studio.repository;

import com.kalitron.studio.domain.RoomWall;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoomWall entity.
 */
@Repository
public interface RoomWallRepository extends JpaRepository<RoomWall, Long> {
    default Optional<RoomWall> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RoomWall> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RoomWall> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select roomWall from RoomWall roomWall left join fetch roomWall.session",
        countQuery = "select count(roomWall) from RoomWall roomWall"
    )
    Page<RoomWall> findAllWithToOneRelationships(Pageable pageable);

    @Query("select roomWall from RoomWall roomWall left join fetch roomWall.session")
    List<RoomWall> findAllWithToOneRelationships();

    @Query("select roomWall from RoomWall roomWall left join fetch roomWall.session where roomWall.id =:id")
    Optional<RoomWall> findOneWithToOneRelationships(@Param("id") Long id);
}
