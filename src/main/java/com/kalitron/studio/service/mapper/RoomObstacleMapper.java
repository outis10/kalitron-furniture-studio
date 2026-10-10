package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomObstacle;
import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.RoomObstacleDTO;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoomObstacle} and its DTO {@link RoomObstacleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomObstacleMapper extends EntityMapper<RoomObstacleDTO, RoomObstacle> {
    @Mapping(target = "siteMeasurement", source = "siteMeasurement", qualifiedByName = "siteMeasurementMeasurementUuid")
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    RoomObstacleDTO toDto(RoomObstacle s);

    @Named("siteMeasurementMeasurementUuid")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "measurementUuid", source = "measurementUuid")
    SiteMeasurementDTO toDtoSiteMeasurementMeasurementUuid(SiteMeasurement siteMeasurement);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
