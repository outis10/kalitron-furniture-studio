package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
import com.kalitron.studio.service.dto.MaterialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KitchenSpec} and its DTO {@link KitchenSpecDTO}.
 */
@Mapper(componentModel = "spring")
public interface KitchenSpecMapper extends EntityMapper<KitchenSpecDTO, KitchenSpec> {
    @Mapping(target = "primaryMaterial", source = "primaryMaterial", qualifiedByName = "materialName")
    KitchenSpecDTO toDto(KitchenSpec s);

    @Named("materialName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MaterialDTO toDtoMaterialName(Material material);
}
