package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.Quote;
import com.kalitron.studio.service.dto.DesignArtifactDTO;
import com.kalitron.studio.service.dto.DesignImageDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.QuoteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Quote} and its DTO {@link QuoteDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuoteMapper extends EntityMapper<QuoteDTO, Quote> {
    @Mapping(target = "renderImage", source = "renderImage", qualifiedByName = "designImageFileName")
    @Mapping(target = "pdfArtifact", source = "pdfArtifact", qualifiedByName = "designArtifactFileName")
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    QuoteDTO toDto(Quote s);

    @Named("designImageFileName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fileName", source = "fileName")
    DesignImageDTO toDtoDesignImageFileName(DesignImage designImage);

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
