package com.kalitron.studio.service.dto.measurement;

import com.kalitron.studio.domain.enumeration.ProjectType;
import java.io.Serializable;
import java.util.List;

/**
 * Site measurement snapshot (schema version 1) as captured by KFS-APP and sent
 * to the sync API (E12 #112). Device and revision metadata are handled by the
 * sync API; this is the part the validation engine reads.
 */
public record SiteMeasurementPayloadDTO(
    Integer schemaVersion,
    ProjectType projectType,
    String catalogVersion,
    List<MeasurementCornerDTO> corners,
    List<MeasurementWallDTO> walls,
    MeasurementSiteDTO site
) implements Serializable {}
