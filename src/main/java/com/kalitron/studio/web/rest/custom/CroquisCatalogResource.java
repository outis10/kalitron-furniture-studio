package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.service.CroquisCatalogService;
import com.kalitron.studio.service.dto.croquis.CroquisCatalogDTO;
import java.util.Arrays;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exports the croquis nomenclature catalog (E12 #105). The mobile app caches it
 * offline and revalidates with {@code If-None-Match}.
 */
@RestController
@RequestMapping("/api/croquis")
public class CroquisCatalogResource {

    private final CroquisCatalogService croquisCatalogService;

    public CroquisCatalogResource(CroquisCatalogService croquisCatalogService) {
        this.croquisCatalogService = croquisCatalogService;
    }

    @GetMapping("/catalog")
    public ResponseEntity<CroquisCatalogDTO> getCatalog(
        @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch
    ) {
        String etag = "\"" + croquisCatalogService.getCatalogVersion() + "\"";
        if (matches(ifNoneMatch, etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).cacheControl(CacheControl.noCache()).body(croquisCatalogService.getCatalog());
    }

    private static boolean matches(String ifNoneMatch, String etag) {
        if (ifNoneMatch == null || ifNoneMatch.isBlank()) {
            return false;
        }
        return Arrays.stream(ifNoneMatch.split(","))
            .map(String::trim)
            .map(value -> value.startsWith("W/") ? value.substring(2) : value)
            .anyMatch(value -> value.equals("*") || value.equals(etag));
    }
}
