package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomWall;
import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.RoomWallDTO;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoomWall} and its DTO {@link RoomWallDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomWallMapper extends EntityMapper<RoomWallDTO, RoomWall> {
    @Mapping(target = "siteMeasurement", source = "siteMeasurement", qualifiedByName = "siteMeasurementMeasurementUuid")
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    RoomWallDTO toDto(RoomWall s);

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
