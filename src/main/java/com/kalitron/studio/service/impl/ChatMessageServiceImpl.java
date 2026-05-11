package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.ChatMessage;
import com.kalitron.studio.repository.ChatMessageRepository;
import com.kalitron.studio.service.ChatMessageService;
import com.kalitron.studio.service.dto.ChatMessageDTO;
import com.kalitron.studio.service.mapper.ChatMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.ChatMessage}.
 */
@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public ChatMessageDTO save(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to save ChatMessage : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    public ChatMessageDTO update(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to update ChatMessage : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    public Optional<ChatMessageDTO> partialUpdate(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to partially update ChatMessage : {}", chatMessageDTO);

        return chatMessageRepository
            .findById(chatMessageDTO.getId())
            .map(existingChatMessage -> {
                chatMessageMapper.partialUpdate(existingChatMessage, chatMessageDTO);

                return existingChatMessage;
            })
            .map(chatMessageRepository::save)
            .map(chatMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChatMessages");
        return chatMessageRepository.findAll(pageable).map(chatMessageMapper::toDto);
    }

    public Page<ChatMessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return chatMessageRepository.findAllWithEagerRelationships(pageable).map(chatMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ChatMessage : {}", id);
        return chatMessageRepository.findOneWithEagerRelationships(id).map(chatMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ChatMessage : {}", id);
        chatMessageRepository.deleteById(id);
    }
}
