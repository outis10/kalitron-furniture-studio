package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomWall;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.RoomWallDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoomWall} and its DTO {@link RoomWallDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomWallMapper extends EntityMapper<RoomWallDTO, RoomWall> {
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    RoomWallDTO toDto(RoomWall s);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);
}
