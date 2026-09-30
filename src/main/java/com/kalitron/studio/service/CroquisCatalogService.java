package com.kalitron.studio.service;

import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.CroquisCodeDTO;
import java.util.Optional;

/**
 * Single source of truth for croquis nomenclature and declarative validation
 * rules (E12 #105), consumed by the mobile app, the backup sheet and the rule
 * engines.
 */
public interface CroquisCatalogService {
    /** The validated catalog loaded at startup. */
    CroquisCatalogDTO getCatalog();

    /** Version used as HTTP ETag. */
    String getCatalogVersion();

    Optional<CroquisCodeDTO> findByCode(String code);
}
