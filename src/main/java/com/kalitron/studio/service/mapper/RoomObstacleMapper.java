package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomObstacle;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.RoomObstacleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoomObstacle} and its DTO {@link RoomObstacleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomObstacleMapper extends EntityMapper<RoomObstacleDTO, RoomObstacle> {
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    RoomObstacleDTO toDto(RoomObstacle s);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);
}
