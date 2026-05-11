package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.service.dto.CabinetDTO;
import com.kalitron.studio.service.dto.CabinetTemplateDTO;
import com.kalitron.studio.service.dto.KitchenSpecDTO;
import com.kalitron.studio.service.dto.MaterialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cabinet} and its DTO {@link CabinetDTO}.
 */
@Mapper(componentModel = "spring")
public interface CabinetMapper extends EntityMapper<CabinetDTO, Cabinet> {
    @Mapping(target = "template", source = "template", qualifiedByName = "cabinetTemplateCode")
    @Mapping(target = "material", source = "material", qualifiedByName = "materialName")
    @Mapping(target = "spec", source = "spec", qualifiedByName = "kitchenSpecStyle")
    CabinetDTO toDto(Cabinet s);

    @Named("cabinetTemplateCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CabinetTemplateDTO toDtoCabinetTemplateCode(CabinetTemplate cabinetTemplate);

    @Named("materialName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MaterialDTO toDtoMaterialName(Material material);

    @Named("kitchenSpecStyle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "style", source = "style")
    KitchenSpecDTO toDtoKitchenSpecStyle(KitchenSpec kitchenSpec);
}
