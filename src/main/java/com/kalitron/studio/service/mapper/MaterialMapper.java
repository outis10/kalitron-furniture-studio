package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.Material;
import com.kalitron.studio.service.dto.MaterialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Material} and its DTO {@link MaterialDTO}.
 */
@Mapper(componentModel = "spring")
public interface MaterialMapper extends EntityMapper<MaterialDTO, Material> {}
