package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.CatalogStyle;
import com.kalitron.studio.service.dto.CatalogStyleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CatalogStyle} and its DTO {@link CatalogStyleDTO}.
 */
@Mapper(componentModel = "spring")
public interface CatalogStyleMapper extends EntityMapper<CatalogStyleDTO, CatalogStyle> {}
