package com.kalitron.studio.repository;

import com.kalitron.studio.domain.CatalogStyle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CatalogStyle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogStyleRepository extends JpaRepository<CatalogStyle, Long> {}
