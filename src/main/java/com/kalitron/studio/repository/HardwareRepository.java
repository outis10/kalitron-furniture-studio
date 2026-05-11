package com.kalitron.studio.repository;

import com.kalitron.studio.domain.Hardware;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hardware entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HardwareRepository extends JpaRepository<Hardware, Long>, JpaSpecificationExecutor<Hardware> {}
