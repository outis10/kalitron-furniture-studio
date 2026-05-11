package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.RoomObstacle;
import com.kalitron.studio.repository.RoomObstacleRepository;
import com.kalitron.studio.service.RoomObstacleService;
import com.kalitron.studio.service.dto.RoomObstacleDTO;
import com.kalitron.studio.service.mapper.RoomObstacleMapper;
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
 * Service Implementation for managing {@link com.kalitron.studio.domain.RoomObstacle}.
 */
@Service
@Transactional
public class RoomObstacleServiceImpl implements RoomObstacleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomObstacleServiceImpl.class);

    private final RoomObstacleRepository roomObstacleRepository;

    private final RoomObstacleMapper roomObstacleMapper;

    public RoomObstacleServiceImpl(RoomObstacleRepository roomObstacleRepository, RoomObstacleMapper roomObstacleMapper) {
        this.roomObstacleRepository = roomObstacleRepository;
        this.roomObstacleMapper = roomObstacleMapper;
    }

    @Override
    public RoomObstacleDTO save(RoomObstacleDTO roomObstacleDTO) {
        LOG.debug("Request to save RoomObstacle : {}", roomObstacleDTO);
        RoomObstacle roomObstacle = roomObstacleMapper.toEntity(roomObstacleDTO);
        roomObstacle = roomObstacleRepository.save(roomObstacle);
        return roomObstacleMapper.toDto(roomObstacle);
    }

    @Override
    public RoomObstacleDTO update(RoomObstacleDTO roomObstacleDTO) {
        LOG.debug("Request to update RoomObstacle : {}", roomObstacleDTO);
        RoomObstacle roomObstacle = roomObstacleMapper.toEntity(roomObstacleDTO);
        roomObstacle = roomObstacleRepository.save(roomObstacle);
        return roomObstacleMapper.toDto(roomObstacle);
    }

    @Override
    public Optional<RoomObstacleDTO> partialUpdate(RoomObstacleDTO roomObstacleDTO) {
        LOG.debug("Request to partially update RoomObstacle : {}", roomObstacleDTO);

        return roomObstacleRepository
            .findById(roomObstacleDTO.getId())
            .map(existingRoomObstacle -> {
                roomObstacleMapper.partialUpdate(existingRoomObstacle, roomObstacleDTO);

                return existingRoomObstacle;
            })
            .map(roomObstacleRepository::save)
            .map(roomObstacleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomObstacleDTO> findAll() {
        LOG.debug("Request to get all RoomObstacles");
        return roomObstacleRepository.findAll().stream().map(roomObstacleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<RoomObstacleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return roomObstacleRepository.findAllWithEagerRelationships(pageable).map(roomObstacleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomObstacleDTO> findOne(Long id) {
        LOG.debug("Request to get RoomObstacle : {}", id);
        return roomObstacleRepository.findOneWithEagerRelationships(id).map(roomObstacleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete RoomObstacle : {}", id);
        roomObstacleRepository.deleteById(id);
    }
}
