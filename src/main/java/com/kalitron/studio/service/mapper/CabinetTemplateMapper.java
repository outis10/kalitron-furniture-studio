package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.service.dto.CabinetTemplateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CabinetTemplate} and its DTO {@link CabinetTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface CabinetTemplateMapper extends EntityMapper<CabinetTemplateDTO, CabinetTemplate> {}
