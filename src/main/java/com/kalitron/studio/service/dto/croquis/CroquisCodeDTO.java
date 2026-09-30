package com.kalitron.studio.service.dto.croquis;

import com.kalitron.studio.domain.enumeration.ApplianceType;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import java.io.Serializable;
import java.util.List;

/**
 * One croquis code (e.g. {@code V} = Ventana). {@code labelEsMx} must equal the
 * Spanish value of the mapped enum ({@link ApplianceType} when present,
 * otherwise {@link RoomObstacleType}).
 */
public record CroquisCodeDTO(
    String code,
    CroquisGroup group,
    String labelEsMx,
    String icon,
    List<RequiredField> requiredFields,
    RoomObstacleType obstacleType,
    ApplianceType applianceType,
    List<ProjectType> projectTypes,
    int sortOrder
) implements Serializable {}
