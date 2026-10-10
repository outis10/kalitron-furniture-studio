package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.service.dto.DesignImageDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.SiteMeasurementDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DesignImage} and its DTO {@link DesignImageDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DesignImageMapper extends EntityMapper<DesignImageDTO, DesignImage> {
    @Mapping(target = "siteMeasurement", source = "siteMeasurement", qualifiedByName = "siteMeasurementMeasurementUuid")
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    DesignImageDTO toDto(DesignImage s);

    @Mapping(target = "imageDataBase64", ignore = true)
    DesignImage toEntity(DesignImageDTO designImageDTO);

    @Mapping(target = "imageDataBase64", ignore = true)
    void partialUpdate(@MappingTarget DesignImage designImage, DesignImageDTO designImageDTO);

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
