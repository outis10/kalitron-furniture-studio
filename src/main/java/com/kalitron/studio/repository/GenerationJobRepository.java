package com.kalitron.studio.repository;

import com.kalitron.studio.domain.GenerationJob;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GenerationJob entity.
 */
@Repository
public interface GenerationJobRepository extends JpaRepository<GenerationJob, Long> {
    default Optional<GenerationJob> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<GenerationJob> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<GenerationJob> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select generationJob from GenerationJob generationJob left join fetch generationJob.artifact left join fetch generationJob.session",
        countQuery = "select count(generationJob) from GenerationJob generationJob"
    )
    Page<GenerationJob> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select generationJob from GenerationJob generationJob left join fetch generationJob.artifact left join fetch generationJob.session"
    )
    List<GenerationJob> findAllWithToOneRelationships();

    @Query(
        "select generationJob from GenerationJob generationJob left join fetch generationJob.artifact left join fetch generationJob.session where generationJob.id =:id"
    )
    Optional<GenerationJob> findOneWithToOneRelationships(@Param("id") Long id);
}
