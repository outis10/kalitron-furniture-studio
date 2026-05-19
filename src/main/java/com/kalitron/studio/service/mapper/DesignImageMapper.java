package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.service.dto.DesignImageDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DesignImage} and its DTO {@link DesignImageDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DesignImageMapper extends EntityMapper<DesignImageDTO, DesignImage> {
    @Mapping(target = "imageDataBase64", ignore = true)
    DesignImage toEntity(DesignImageDTO designImageDTO);

    @Mapping(target = "imageDataBase64", ignore = true)
    void partialUpdate(@MappingTarget DesignImage designImage, DesignImageDTO designImageDTO);

    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    DesignImageDTO toDto(DesignImage s);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);
}
