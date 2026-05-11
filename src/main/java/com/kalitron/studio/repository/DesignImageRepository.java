package com.kalitron.studio.repository;

import com.kalitron.studio.domain.DesignImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DesignImage entity.
 */
@Repository
public interface DesignImageRepository extends JpaRepository<DesignImage, Long> {
    default Optional<DesignImage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DesignImage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DesignImage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select designImage from DesignImage designImage left join fetch designImage.session",
        countQuery = "select count(designImage) from DesignImage designImage"
    )
    Page<DesignImage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select designImage from DesignImage designImage left join fetch designImage.session")
    List<DesignImage> findAllWithToOneRelationships();

    @Query("select designImage from DesignImage designImage left join fetch designImage.session where designImage.id =:id")
    Optional<DesignImage> findOneWithToOneRelationships(@Param("id") Long id);
}
