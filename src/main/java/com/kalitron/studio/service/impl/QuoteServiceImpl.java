package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.Quote;
import com.kalitron.studio.repository.QuoteRepository;
import com.kalitron.studio.service.QuoteService;
import com.kalitron.studio.service.dto.QuoteDTO;
import com.kalitron.studio.service.mapper.QuoteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.Quote}.
 */
@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final QuoteRepository quoteRepository;

    private final QuoteMapper quoteMapper;

    public QuoteServiceImpl(QuoteRepository quoteRepository, QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.quoteMapper = quoteMapper;
    }

    @Override
    public QuoteDTO save(QuoteDTO quoteDTO) {
        LOG.debug("Request to save Quote : {}", quoteDTO);
        Quote quote = quoteMapper.toEntity(quoteDTO);
        quote = quoteRepository.save(quote);
        return quoteMapper.toDto(quote);
    }

    @Override
    public QuoteDTO update(QuoteDTO quoteDTO) {
        LOG.debug("Request to update Quote : {}", quoteDTO);
        Quote quote = quoteMapper.toEntity(quoteDTO);
        quote = quoteRepository.save(quote);
        return quoteMapper.toDto(quote);
    }

    @Override
    public Optional<QuoteDTO> partialUpdate(QuoteDTO quoteDTO) {
        LOG.debug("Request to partially update Quote : {}", quoteDTO);

        return quoteRepository
            .findById(quoteDTO.getId())
            .map(existingQuote -> {
                quoteMapper.partialUpdate(existingQuote, quoteDTO);

                return existingQuote;
            })
            .map(quoteRepository::save)
            .map(quoteMapper::toDto);
    }

    public Page<QuoteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return quoteRepository.findAllWithEagerRelationships(pageable).map(quoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuoteDTO> findOne(Long id) {
        LOG.debug("Request to get Quote : {}", id);
        return quoteRepository.findOneWithEagerRelationships(id).map(quoteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Quote : {}", id);
        quoteRepository.deleteById(id);
    }
}
