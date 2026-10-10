package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.domain.User;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import com.kalitron.studio.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SiteMeasurement} and its DTO {@link SiteMeasurementDTO}.
 */
@Mapper(componentModel = "spring")
public interface SiteMeasurementMapper extends EntityMapper<SiteMeasurementDTO, SiteMeasurement> {
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    @Mapping(target = "measuredBy", source = "measuredBy", qualifiedByName = "userLogin")
    SiteMeasurementDTO toDto(SiteMeasurement s);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
