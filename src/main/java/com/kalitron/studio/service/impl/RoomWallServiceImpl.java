package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.RoomWall;
import com.kalitron.studio.repository.RoomWallRepository;
import com.kalitron.studio.service.RoomWallService;
import com.kalitron.studio.service.dto.RoomWallDTO;
import com.kalitron.studio.service.mapper.RoomWallMapper;
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
 * Service Implementation for managing {@link com.kalitron.studio.domain.RoomWall}.
 */
@Service
@Transactional
public class RoomWallServiceImpl implements RoomWallService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomWallServiceImpl.class);

    private final RoomWallRepository roomWallRepository;

    private final RoomWallMapper roomWallMapper;

    public RoomWallServiceImpl(RoomWallRepository roomWallRepository, RoomWallMapper roomWallMapper) {
        this.roomWallRepository = roomWallRepository;
        this.roomWallMapper = roomWallMapper;
    }

    @Override
    public RoomWallDTO save(RoomWallDTO roomWallDTO) {
        LOG.debug("Request to save RoomWall : {}", roomWallDTO);
        RoomWall roomWall = roomWallMapper.toEntity(roomWallDTO);
        roomWall = roomWallRepository.save(roomWall);
        return roomWallMapper.toDto(roomWall);
    }

    @Override
    public RoomWallDTO update(RoomWallDTO roomWallDTO) {
        LOG.debug("Request to update RoomWall : {}", roomWallDTO);
        RoomWall roomWall = roomWallMapper.toEntity(roomWallDTO);
        roomWall = roomWallRepository.save(roomWall);
        return roomWallMapper.toDto(roomWall);
    }

    @Override
    public Optional<RoomWallDTO> partialUpdate(RoomWallDTO roomWallDTO) {
        LOG.debug("Request to partially update RoomWall : {}", roomWallDTO);

        return roomWallRepository
            .findById(roomWallDTO.getId())
            .map(existingRoomWall -> {
                roomWallMapper.partialUpdate(existingRoomWall, roomWallDTO);

                return existingRoomWall;
            })
            .map(roomWallRepository::save)
            .map(roomWallMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomWallDTO> findAll() {
        LOG.debug("Request to get all RoomWalls");
        return roomWallRepository.findAll().stream().map(roomWallMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<RoomWallDTO> findAllWithEagerRelationships(Pageable pageable) {
        return roomWallRepository.findAllWithEagerRelationships(pageable).map(roomWallMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomWallDTO> findOne(Long id) {
        LOG.debug("Request to get RoomWall : {}", id);
        return roomWallRepository.findOneWithEagerRelationships(id).map(roomWallMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete RoomWall : {}", id);
        roomWallRepository.deleteById(id);
    }
}
