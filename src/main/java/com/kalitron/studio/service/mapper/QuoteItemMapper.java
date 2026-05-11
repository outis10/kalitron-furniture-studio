package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.domain.Hardware;
import com.kalitron.studio.domain.Quote;
import com.kalitron.studio.domain.QuoteItem;
import com.kalitron.studio.service.dto.CabinetDTO;
import com.kalitron.studio.service.dto.HardwareDTO;
import com.kalitron.studio.service.dto.QuoteDTO;
import com.kalitron.studio.service.dto.QuoteItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuoteItem} and its DTO {@link QuoteItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuoteItemMapper extends EntityMapper<QuoteItemDTO, QuoteItem> {
    @Mapping(target = "cabinet", source = "cabinet", qualifiedByName = "cabinetCabinetCode")
    @Mapping(target = "hardware", source = "hardware", qualifiedByName = "hardwareName")
    @Mapping(target = "quote", source = "quote", qualifiedByName = "quoteQuoteNumber")
    QuoteItemDTO toDto(QuoteItem s);

    @Named("cabinetCabinetCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cabinetCode", source = "cabinetCode")
    CabinetDTO toDtoCabinetCabinetCode(Cabinet cabinet);

    @Named("hardwareName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    HardwareDTO toDtoHardwareName(Hardware hardware);

    @Named("quoteQuoteNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "quoteNumber", source = "quoteNumber")
    QuoteDTO toDtoQuoteQuoteNumber(Quote quote);
}
