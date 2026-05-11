package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.service.dto.DesignArtifactDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DesignArtifact} and its DTO {@link DesignArtifactDTO}.
 */
@Mapper(componentModel = "spring")
public interface DesignArtifactMapper extends EntityMapper<DesignArtifactDTO, DesignArtifact> {
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    DesignArtifactDTO toDto(DesignArtifact s);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);
}
