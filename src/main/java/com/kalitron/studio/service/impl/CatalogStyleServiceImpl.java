package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.CatalogStyle;
import com.kalitron.studio.repository.CatalogStyleRepository;
import com.kalitron.studio.service.CatalogStyleService;
import com.kalitron.studio.service.dto.CatalogStyleDTO;
import com.kalitron.studio.service.mapper.CatalogStyleMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.CatalogStyle}.
 */
@Service
@Transactional
public class CatalogStyleServiceImpl implements CatalogStyleService {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogStyleServiceImpl.class);

    private final CatalogStyleRepository catalogStyleRepository;

    private final CatalogStyleMapper catalogStyleMapper;

    public CatalogStyleServiceImpl(CatalogStyleRepository catalogStyleRepository, CatalogStyleMapper catalogStyleMapper) {
        this.catalogStyleRepository = catalogStyleRepository;
        this.catalogStyleMapper = catalogStyleMapper;
    }

    @Override
    public CatalogStyleDTO save(CatalogStyleDTO catalogStyleDTO) {
        LOG.debug("Request to save CatalogStyle : {}", catalogStyleDTO);
        CatalogStyle catalogStyle = catalogStyleMapper.toEntity(catalogStyleDTO);
        catalogStyle = catalogStyleRepository.save(catalogStyle);
        return catalogStyleMapper.toDto(catalogStyle);
    }

    @Override
    public CatalogStyleDTO update(CatalogStyleDTO catalogStyleDTO) {
        LOG.debug("Request to update CatalogStyle : {}", catalogStyleDTO);
        CatalogStyle catalogStyle = catalogStyleMapper.toEntity(catalogStyleDTO);
        catalogStyle = catalogStyleRepository.save(catalogStyle);
        return catalogStyleMapper.toDto(catalogStyle);
    }

    @Override
    public Optional<CatalogStyleDTO> partialUpdate(CatalogStyleDTO catalogStyleDTO) {
        LOG.debug("Request to partially update CatalogStyle : {}", catalogStyleDTO);

        return catalogStyleRepository
            .findById(catalogStyleDTO.getId())
            .map(existingCatalogStyle -> {
                catalogStyleMapper.partialUpdate(existingCatalogStyle, catalogStyleDTO);

                return existingCatalogStyle;
            })
            .map(catalogStyleRepository::save)
            .map(catalogStyleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogStyleDTO> findAll() {
        LOG.debug("Request to get all CatalogStyles");
        return catalogStyleRepository.findAll().stream().map(catalogStyleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CatalogStyleDTO> findOne(Long id) {
        LOG.debug("Request to get CatalogStyle : {}", id);
        return catalogStyleRepository.findById(id).map(catalogStyleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CatalogStyle : {}", id);
        catalogStyleRepository.deleteById(id);
    }
}
