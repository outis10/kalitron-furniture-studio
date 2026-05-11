package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.Hardware;
import com.kalitron.studio.service.dto.HardwareDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Hardware} and its DTO {@link HardwareDTO}.
 */
@Mapper(componentModel = "spring")
public interface HardwareMapper extends EntityMapper<HardwareDTO, Hardware> {}
