package com.kalitron.studio.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.service.CroquisCatalogService;
import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import com.kalitron.studio.service.dto.croquis.CroquisCodeDTO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Loads {@code croquis/catalog.json} once at startup with a strict mapper and
 * validates it; any problem fails application startup (fail fast).
 */
@Service
public class CroquisCatalogServiceImpl implements CroquisCatalogService {

    private static final Logger LOG = LoggerFactory.getLogger(CroquisCatalogServiceImpl.class);

    private final CroquisCatalogDTO catalog;
    private final Map<String, CroquisCodeDTO> entriesByCode;

    public CroquisCatalogServiceImpl(@Value("${app.croquis.catalog-location:classpath:croquis/catalog.json}") Resource catalogResource) {
        this.catalog = load(catalogResource);
        this.entriesByCode = catalog.entries().stream().collect(Collectors.toUnmodifiableMap(CroquisCodeDTO::code, Function.identity()));
        LOG.info(
            "Loaded croquis catalog {} ({} codes, {} rules)",
            catalog.catalogVersion(),
            catalog.entries().size(),
            catalog.validationRules().size()
        );
    }

    @Override
    public CroquisCatalogDTO getCatalog() {
        return catalog;
    }

    @Override
    public String getCatalogVersion() {
        return catalog.catalogVersion();
    }

    @Override
    public Optional<CroquisCodeDTO> findByCode(String code) {
        return Optional.ofNullable(code).map(entriesByCode::get);
    }

    static CroquisCatalogDTO load(Resource resource) {
        ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        CroquisCatalogDTO parsed;
        try (InputStream in = resource.getInputStream()) {
            parsed = mapper.readValue(in, CroquisCatalogDTO.class);
        } catch (IOException e) {
            throw new IllegalStateException("Invalid croquis catalog " + resource.getDescription() + ": " + e.getMessage(), e);
        }
        CroquisCatalogValidator.validate(parsed);
        return parsed;
    }
}
