package com.kalitron.studio.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.RoomObstacle;
import com.kalitron.studio.domain.RoomWall;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.RoomObstacleRepository;
import com.kalitron.studio.repository.RoomWallRepository;
import com.kalitron.studio.service.MeasuredLayoutService;
import com.kalitron.studio.service.dto.LayoutObstacleDTO;
import com.kalitron.studio.service.dto.LayoutZoneDTO;
import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import com.kalitron.studio.service.dto.MeasuredWallSegmentDTO;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeasuredLayoutServiceImpl implements MeasuredLayoutService {

    private static final String MEASURED_LAYOUT_FILE_NAME = "measured-layout.json";

    private final DesignSessionRepository designSessionRepository;

    private final RoomWallRepository roomWallRepository;

    private final RoomObstacleRepository roomObstacleRepository;

    private final DesignArtifactRepository designArtifactRepository;

    private final ObjectMapper objectMapper;

    public MeasuredLayoutServiceImpl(
        DesignSessionRepository designSessionRepository,
        RoomWallRepository roomWallRepository,
        RoomObstacleRepository roomObstacleRepository,
        DesignArtifactRepository designArtifactRepository,
        ObjectMapper objectMapper
    ) {
        this.designSessionRepository = designSessionRepository;
        this.roomWallRepository = roomWallRepository;
        this.roomObstacleRepository = roomObstacleRepository;
        this.designArtifactRepository = designArtifactRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public MeasuredLayoutRequestDTO saveMeasuredLayout(Long sessionId, MeasuredLayoutRequestDTO request) {
        DesignSession session = designSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Design session not found"));
        if (!sessionId.equals(request.getSessionId())) {
            throw new IllegalArgumentException("Path session id does not match request session id");
        }

        validateMeasuredLayout(request);

        roomWallRepository.deleteBySessionId(sessionId);
        roomObstacleRepository.deleteBySessionId(sessionId);
        for (MeasuredWallSegmentDTO wall : request.getWalls()) {
            roomWallRepository.save(
                new RoomWall()
                    .session(session)
                    .name(wall.getWallCode().trim())
                    .lengthMm(wall.getLengthMm())
                    .heightMm(wall.getHeightMm() != null ? wall.getHeightMm() : request.getRoomHeightMm())
                    .angleDeg(wall.getAngleDeg())
                    .positionX(wall.getStartXMm())
                    .positionY(wall.getStartYMm())
                    .sortOrder(wall.getSortOrder())
            );
        }
        persistZonesAndObstacles(session, request);

        persistMeasuredLayoutSnapshot(session, request);
        session.setUpdatedAt(Instant.now());
        designSessionRepository.save(session);
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MeasuredLayoutRequestDTO> findMeasuredLayout(Long sessionId) {
        return designArtifactRepository
            .findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(sessionId, MEASURED_LAYOUT_FILE_NAME)
            .map(DesignArtifact::getMetadataJson)
            .flatMap(this::readMeasuredLayoutSnapshot);
    }

    private void validateMeasuredLayout(MeasuredLayoutRequestDTO request) {
        if (request.getLayout() == null) {
            throw new IllegalArgumentException("Layout type is required");
        }
        if (request.getRoomHeightMm() == null || request.getRoomHeightMm() <= 0) {
            throw new IllegalArgumentException("Room height must be greater than 0");
        }
        if (request.getWalls() == null || request.getWalls().isEmpty()) {
            throw new IllegalArgumentException("At least one wall segment is required");
        }
        validateWallCount(request.getLayout(), request.getWalls().size());

        Set<String> wallCodes = new HashSet<>();
        for (MeasuredWallSegmentDTO wall : request.getWalls()) {
            String wallCode = wall.getWallCode() == null ? "" : wall.getWallCode().trim();
            if (wallCode.isEmpty()) {
                throw new IllegalArgumentException("Wall code is required");
            }
            if (!wallCodes.add(wallCode)) {
                throw new IllegalArgumentException("Wall codes must be unique");
            }
            if (wall.getLengthMm() == null || wall.getLengthMm() <= 0) {
                throw new IllegalArgumentException("Wall length must be greater than 0");
            }
        }
        validateZones(request, wallCodes);
        validateObstacles(request, wallCodes);
    }

    private void validateWallCount(KitchenLayout layout, int wallCount) {
        if (layout == KitchenLayout.L_SHAPE && wallCount < 2) {
            throw new IllegalArgumentException("L-shaped layouts require at least 2 wall segments");
        }
        if (layout == KitchenLayout.U_SHAPE && wallCount < 3) {
            throw new IllegalArgumentException("U-shaped layouts require at least 3 wall segments");
        }
    }

    private void validateZones(MeasuredLayoutRequestDTO request, Set<String> wallCodes) {
        if (request.getZones() == null) {
            return;
        }
        Set<String> zoneCodes = new HashSet<>();
        for (LayoutZoneDTO zone : request.getZones()) {
            String zoneCode = zone.getZoneCode() == null ? "" : zone.getZoneCode().trim();
            String wallCode = zone.getWallCode() == null ? "" : zone.getWallCode().trim();
            if (zoneCode.isEmpty()) {
                throw new IllegalArgumentException("Zone code is required");
            }
            if (!zoneCodes.add(zoneCode)) {
                throw new IllegalArgumentException("Zone codes must be unique");
            }
            validateWallReference(wallCodes, wallCode, "Zone wall code must reference an existing wall");
            validateSpanWithinWall(request, wallCode, zone.getxMm(), zone.getWidthMm(), "Zone must fit inside its wall segment");
        }
    }

    private void validateObstacles(MeasuredLayoutRequestDTO request, Set<String> wallCodes) {
        if (request.getObstacles() == null) {
            return;
        }
        for (LayoutObstacleDTO obstacle : request.getObstacles()) {
            String wallCode = obstacle.getWallCode() == null ? "" : obstacle.getWallCode().trim();
            validateWallReference(wallCodes, wallCode, "Obstacle wall code must reference an existing wall");
            validateSpanWithinWall(
                request,
                wallCode,
                obstacle.getxMm(),
                obstacle.getWidthMm(),
                "Obstacle must fit inside its wall segment"
            );
        }
    }

    private void validateWallReference(Set<String> wallCodes, String wallCode, String message) {
        if (wallCode.isEmpty() || !wallCodes.contains(wallCode)) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateSpanWithinWall(MeasuredLayoutRequestDTO request, String wallCode, Integer xMm, Integer widthMm, String message) {
        if (xMm == null || widthMm == null) {
            return;
        }
        request
            .getWalls()
            .stream()
            .filter(wall -> wallCode.equals(wall.getWallCode()))
            .findFirst()
            .ifPresent(wall -> {
                if (xMm + widthMm > wall.getLengthMm()) {
                    throw new IllegalArgumentException(message);
                }
            });
    }

    private void persistZonesAndObstacles(DesignSession session, MeasuredLayoutRequestDTO request) {
        if (request.getZones() != null) {
            for (LayoutZoneDTO zone : request.getZones()) {
                roomObstacleRepository.save(
                    new RoomObstacle()
                        .session(session)
                        .obstacleType(toObstacleType(zone.getZoneType()))
                        .label(zone.getZoneType())
                        .xMm(zone.getxMm())
                        .yMm(zone.getyMm())
                        .zMm(zone.getzMm())
                        .widthMm(zone.getWidthMm())
                        .heightMm(zone.getHeightMm())
                        .depthMm(zone.getDepthMm())
                        .notes(
                            limitNotes(
                                "wallCode=" +
                                    zone.getWallCode() +
                                    ";zoneCode=" +
                                    zone.getZoneCode() +
                                    ";notes=" +
                                    nullToEmpty(zone.getNotes())
                            )
                        )
                );
            }
        }
        if (request.getObstacles() != null) {
            for (LayoutObstacleDTO obstacle : request.getObstacles()) {
                roomObstacleRepository.save(
                    new RoomObstacle()
                        .session(session)
                        .obstacleType(obstacle.getObstacleType())
                        .label(obstacle.getLabel())
                        .xMm(obstacle.getxMm())
                        .yMm(obstacle.getyMm())
                        .zMm(obstacle.getzMm())
                        .widthMm(obstacle.getWidthMm())
                        .heightMm(obstacle.getHeightMm())
                        .depthMm(obstacle.getDepthMm())
                        .notes(limitNotes("wallCode=" + obstacle.getWallCode() + ";notes=" + nullToEmpty(obstacle.getNotes())))
                );
            }
        }
    }

    private RoomObstacleType toObstacleType(String zoneType) {
        if (zoneType == null) {
            return RoomObstacleType.OTHER;
        }
        return switch (zoneType.trim().toUpperCase()) {
            case "SINK" -> RoomObstacleType.WATER;
            case "RANGE", "STOVE", "COOKTOP" -> RoomObstacleType.GAS;
            case "FRIDGE", "REFRIGERATOR", "DISHWASHER", "OVEN" -> RoomObstacleType.APPLIANCE;
            default -> RoomObstacleType.OTHER;
        };
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String limitNotes(String value) {
        if (value == null || value.length() <= 300) {
            return value;
        }
        return value.substring(0, 300);
    }

    private void persistMeasuredLayoutSnapshot(DesignSession session, MeasuredLayoutRequestDTO request) {
        DesignArtifact artifact = designArtifactRepository
            .findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(session.getId(), MEASURED_LAYOUT_FILE_NAME)
            .orElseGet(DesignArtifact::new);

        artifact
            .session(session)
            .artifactType(ArtifactType.OTHER)
            .fileName(MEASURED_LAYOUT_FILE_NAME)
            .filePath("inline://design-sessions/" + session.getSessionCode() + "/measured-layout")
            .mimeType("application/json")
            .createdAt(artifact.getCreatedAt() != null ? artifact.getCreatedAt() : Instant.now())
            .metadataJson(writeMeasuredLayoutSnapshot(request));
        designArtifactRepository.save(artifact);
    }

    private String writeMeasuredLayoutSnapshot(MeasuredLayoutRequestDTO request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Measured layout could not be serialized", e);
        }
    }

    private Optional<MeasuredLayoutRequestDTO> readMeasuredLayoutSnapshot(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(metadataJson, MeasuredLayoutRequestDTO.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
