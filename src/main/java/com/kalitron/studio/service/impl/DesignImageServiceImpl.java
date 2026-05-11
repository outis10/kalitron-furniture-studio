package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.repository.DesignImageRepository;
import com.kalitron.studio.service.DesignImageService;
import com.kalitron.studio.service.dto.DesignImageDTO;
import com.kalitron.studio.service.mapper.DesignImageMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.kalitron.studio.domain.DesignImage}.
 */
@Service
@Transactional
public class DesignImageServiceImpl implements DesignImageService {

    private static final Logger LOG = LoggerFactory.getLogger(DesignImageServiceImpl.class);

    private final DesignImageRepository designImageRepository;

    private final DesignImageMapper designImageMapper;

    public DesignImageServiceImpl(DesignImageRepository designImageRepository, DesignImageMapper designImageMapper) {
        this.designImageRepository = designImageRepository;
        this.designImageMapper = designImageMapper;
    }

    @Override
    public DesignImageDTO save(DesignImageDTO designImageDTO) {
        LOG.debug("Request to save DesignImage : {}", designImageDTO);
        DesignImage designImage = designImageMapper.toEntity(designImageDTO);
        designImage = designImageRepository.save(designImage);
        return designImageMapper.toDto(designImage);
    }

    @Override
    public DesignImageDTO update(DesignImageDTO designImageDTO) {
        LOG.debug("Request to update DesignImage : {}", designImageDTO);
        DesignImage designImage = designImageMapper.toEntity(designImageDTO);
        designImage = designImageRepository.save(designImage);
        return designImageMapper.toDto(designImage);
    }

    @Override
    public Optional<DesignImageDTO> partialUpdate(DesignImageDTO designImageDTO) {
        LOG.debug("Request to partially update DesignImage : {}", designImageDTO);

        return designImageRepository
            .findById(designImageDTO.getId())
            .map(existingDesignImage -> {
                designImageMapper.partialUpdate(existingDesignImage, designImageDTO);

                return existingDesignImage;
            })
            .map(designImageRepository::save)
            .map(designImageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignImageDTO> findAll() {
        LOG.debug("Request to get all DesignImages");
        return designImageRepository.findAll().stream().map(designImageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<DesignImageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return designImageRepository.findAllWithEagerRelationships(pageable).map(designImageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DesignImageDTO> findOne(Long id) {
        LOG.debug("Request to get DesignImage : {}", id);
        return designImageRepository.findOneWithEagerRelationships(id).map(designImageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DesignImage : {}", id);
        designImageRepository.deleteById(id);
    }
}
