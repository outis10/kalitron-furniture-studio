package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.CatalogStyle;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.service.dto.CatalogStyleDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DesignSession} and its DTO {@link DesignSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface DesignSessionMapper extends EntityMapper<DesignSessionDTO, DesignSession> {
    @Mapping(target = "spec", source = "spec", qualifiedByName = "kitchenSpecStyle")
    @Mapping(target = "catalogStyle", source = "catalogStyle", qualifiedByName = "catalogStyleName")
    DesignSessionDTO toDto(DesignSession s);

    @Named("kitchenSpecStyle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "style", source = "style")
    KitchenSpecDTO toDtoKitchenSpecStyle(KitchenSpec kitchenSpec);

    @Named("catalogStyleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CatalogStyleDTO toDtoCatalogStyleName(CatalogStyle catalogStyle);
}
