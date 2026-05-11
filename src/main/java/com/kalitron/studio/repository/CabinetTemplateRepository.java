package com.kalitron.studio.repository;

import com.kalitron.studio.domain.CabinetTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CabinetTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CabinetTemplateRepository extends JpaRepository<CabinetTemplate, Long>, JpaSpecificationExecutor<CabinetTemplate> {}
