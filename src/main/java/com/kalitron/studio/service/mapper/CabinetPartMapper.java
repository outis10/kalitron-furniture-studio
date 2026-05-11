package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.domain.CabinetPart;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.service.dto.CabinetDTO;
import com.kalitron.studio.service.dto.CabinetPartDTO;
import com.kalitron.studio.service.dto.MaterialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CabinetPart} and its DTO {@link CabinetPartDTO}.
 */
@Mapper(componentModel = "spring")
public interface CabinetPartMapper extends EntityMapper<CabinetPartDTO, CabinetPart> {
    @Mapping(target = "material", source = "material", qualifiedByName = "materialName")
    @Mapping(target = "cabinet", source = "cabinet", qualifiedByName = "cabinetCabinetCode")
    CabinetPartDTO toDto(CabinetPart s);

    @Named("materialName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MaterialDTO toDtoMaterialName(Material material);

    @Named("cabinetCabinetCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cabinetCode", source = "cabinetCode")
    CabinetDTO toDtoCabinetCabinetCode(Cabinet cabinet);
}
