package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;

public record MeasurementSiteDTO(Integer floorOutOfLevelMm, String floorOutOfLevelNote) implements Serializable {}
