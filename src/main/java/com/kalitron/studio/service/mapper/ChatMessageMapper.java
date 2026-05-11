package com.kalitron.studio.service.mapper;

import com.kalitron.studio.domain.ChatMessage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.service.dto.ChatMessageDTO;
import com.kalitron.studio.service.dto.DesignSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChatMessage} and its DTO {@link ChatMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatMessageMapper extends EntityMapper<ChatMessageDTO, ChatMessage> {
    @Mapping(target = "session", source = "session", qualifiedByName = "designSessionSessionCode")
    ChatMessageDTO toDto(ChatMessage s);

    @Named("designSessionSessionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sessionCode", source = "sessionCode")
    DesignSessionDTO toDtoDesignSessionSessionCode(DesignSession designSession);
}
