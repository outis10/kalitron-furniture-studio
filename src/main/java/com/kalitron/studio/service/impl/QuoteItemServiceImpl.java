package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.QuoteItem;
import com.kalitron.studio.repository.QuoteItemRepository;
import com.kalitron.studio.service.QuoteItemService;
import com.kalitron.studio.service.dto.QuoteItemDTO;
import com.kalitron.studio.service.mapper.QuoteItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.QuoteItem}.
 */
@Service
@Transactional
public class QuoteItemServiceImpl implements QuoteItemService {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteItemServiceImpl.class);

    private final QuoteItemRepository quoteItemRepository;

    private final QuoteItemMapper quoteItemMapper;

    public QuoteItemServiceImpl(QuoteItemRepository quoteItemRepository, QuoteItemMapper quoteItemMapper) {
        this.quoteItemRepository = quoteItemRepository;
        this.quoteItemMapper = quoteItemMapper;
    }

    @Override
    public QuoteItemDTO save(QuoteItemDTO quoteItemDTO) {
        LOG.debug("Request to save QuoteItem : {}", quoteItemDTO);
        QuoteItem quoteItem = quoteItemMapper.toEntity(quoteItemDTO);
        quoteItem = quoteItemRepository.save(quoteItem);
        return quoteItemMapper.toDto(quoteItem);
    }

    @Override
    public QuoteItemDTO update(QuoteItemDTO quoteItemDTO) {
        LOG.debug("Request to update QuoteItem : {}", quoteItemDTO);
        QuoteItem quoteItem = quoteItemMapper.toEntity(quoteItemDTO);
        quoteItem = quoteItemRepository.save(quoteItem);
        return quoteItemMapper.toDto(quoteItem);
    }

    @Override
    public Optional<QuoteItemDTO> partialUpdate(QuoteItemDTO quoteItemDTO) {
        LOG.debug("Request to partially update QuoteItem : {}", quoteItemDTO);

        return quoteItemRepository
            .findById(quoteItemDTO.getId())
            .map(existingQuoteItem -> {
                quoteItemMapper.partialUpdate(existingQuoteItem, quoteItemDTO);

                return existingQuoteItem;
            })
            .map(quoteItemRepository::save)
            .map(quoteItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all QuoteItems");
        return quoteItemRepository.findAll(pageable).map(quoteItemMapper::toDto);
    }

    public Page<QuoteItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return quoteItemRepository.findAllWithEagerRelationships(pageable).map(quoteItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuoteItemDTO> findOne(Long id) {
        LOG.debug("Request to get QuoteItem : {}", id);
        return quoteItemRepository.findOneWithEagerRelationships(id).map(quoteItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete QuoteItem : {}", id);
        quoteItemRepository.deleteById(id);
    }
}
