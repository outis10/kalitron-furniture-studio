package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.GenerationJob;
import com.kalitron.studio.service.dto.DesignArtifactDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.GenerationJobDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GenerationJob} and its DTO {@link GenerationJobDTO}.
 */
@Mapper(componentModel = "spring")
public interface GenerationJobMapper extends EntityMapper<GenerationJobDTO, GenerationJob> {
    @Mapping(target = "artifact", source = "artifact", qualifiedByName = "designArtifactFileName")
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    GenerationJobDTO toDto(GenerationJob s);

    @Named("designArtifactFileName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fileName", source = "fileName")
    DesignArtifactDTO toDtoDesignArtifactFileName(DesignArtifact designArtifact);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);
}
