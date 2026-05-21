package com.kalitron.studio.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.domain.Cabinet;
import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.KitchenSpec;
import com.kalitron.studio.domain.Material;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.FinishType;
import com.kalitron.studio.domain.enumeration.MaterialKind;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import com.kalitron.studio.repository.CabinetRepository;
import com.kalitron.studio.repository.CabinetTemplateRepository;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.KitchenSpecRepository;
import com.kalitron.studio.repository.MaterialRepository;
import com.kalitron.studio.service.CabinetPlanService;
import com.kalitron.studio.service.MeasuredLayoutService;
import com.kalitron.studio.service.dto.CabinetPlanItemDTO;
import com.kalitron.studio.service.dto.CabinetPlanResponseDTO;
import com.kalitron.studio.service.dto.CabinetPlanValidationMessageDTO;
import com.kalitron.studio.service.dto.LayoutObstacleDTO;
import com.kalitron.studio.service.dto.LayoutZoneDTO;
import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import com.kalitron.studio.service.dto.MeasuredWallSegmentDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CabinetPlanServiceImpl implements CabinetPlanService {

    private static final String CABINET_PLAN_FILE_NAME = "cabinet-plan.json";
    private static final int DEFAULT_BASE_HEIGHT_MM = 720;
    private static final int DEFAULT_BASE_DEPTH_MM = 560;
    private static final int DEFAULT_UPPER_HEIGHT_MM = 720;
    private static final int DEFAULT_UPPER_DEPTH_MM = 350;
    private static final int DEFAULT_MODULE_WIDTH_MM = 800;
    private static final int MIN_MODULE_WIDTH_MM = 250;
    private static final int BASE_BLOCK_TOP_MM = 900;

    private final DesignSessionRepository designSessionRepository;
    private final KitchenSpecRepository kitchenSpecRepository;
    private final CabinetRepository cabinetRepository;
    private final CabinetTemplateRepository cabinetTemplateRepository;
    private final MaterialRepository materialRepository;
    private final DesignArtifactRepository designArtifactRepository;
    private final MeasuredLayoutService measuredLayoutService;
    private final CabinetPlanValidator cabinetPlanValidator;
    private final ObjectMapper objectMapper;

    public CabinetPlanServiceImpl(
        DesignSessionRepository designSessionRepository,
        KitchenSpecRepository kitchenSpecRepository,
        CabinetRepository cabinetRepository,
        CabinetTemplateRepository cabinetTemplateRepository,
        MaterialRepository materialRepository,
        DesignArtifactRepository designArtifactRepository,
        MeasuredLayoutService measuredLayoutService,
        CabinetPlanValidator cabinetPlanValidator,
        ObjectMapper objectMapper
    ) {
        this.designSessionRepository = designSessionRepository;
        this.kitchenSpecRepository = kitchenSpecRepository;
        this.cabinetRepository = cabinetRepository;
        this.cabinetTemplateRepository = cabinetTemplateRepository;
        this.materialRepository = materialRepository;
        this.designArtifactRepository = designArtifactRepository;
        this.measuredLayoutService = measuredLayoutService;
        this.cabinetPlanValidator = cabinetPlanValidator;
        this.objectMapper = objectMapper;
    }

    @Override
    public CabinetPlanResponseDTO generateCabinetPlan(Long sessionId) {
        DesignSession session = designSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Design session not found"));
        MeasuredLayoutRequestDTO layout = measuredLayoutService
            .findMeasuredLayout(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Measured layout not found"));

        CabinetPlanResponseDTO response = new CabinetPlanResponseDTO();
        response.setSessionId(session.getId());
        response.setSessionCode(session.getSessionCode());
        response.setLayout(layout.getLayout());

        List<CabinetPlanValidationMessageDTO> messages = response.getValidationMessages();
        List<CabinetPlanItemDTO> cabinets = new ArrayList<>();
        int sequence = 1;
        for (MeasuredWallSegmentDTO wall : sortedWalls(layout)) {
            WallPlan wallPlan = planWall(layout, wall, sequence, messages);
            cabinets.addAll(wallPlan.cabinets());
            sequence += wallPlan.cabinets().size();
        }

        messages.addAll(cabinetPlanValidator.validate(layout, cabinets));

        int totalOccupiedLengthMm = cabinets.stream().map(CabinetPlanItemDTO::getWidthMm).reduce(0, Integer::sum);
        response.setCabinets(cabinets);
        response.setCabinetCount(cabinets.size());
        response.setTotalOccupiedLengthMm(totalOccupiedLengthMm);
        response.setValid(messages.stream().noneMatch(message -> "ERROR".equals(message.getSeverity())));

        KitchenSpec spec = ensureKitchenSpec(session, layout);
        Material material = ensureMaterial();
        cabinetRepository.deleteBySpecId(spec.getId());
        for (CabinetPlanItemDTO cabinet : cabinets) {
            cabinetRepository.save(toCabinet(cabinet, spec, material));
        }
        persistCabinetPlanSnapshot(session, response);
        session.setUpdatedAt(Instant.now());
        designSessionRepository.save(session);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CabinetPlanResponseDTO> findCabinetPlan(Long sessionId) {
        return designArtifactRepository
            .findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(sessionId, CABINET_PLAN_FILE_NAME)
            .map(DesignArtifact::getMetadataJson)
            .flatMap(this::readCabinetPlanSnapshot);
    }

    private WallPlan planWall(
        MeasuredLayoutRequestDTO layout,
        MeasuredWallSegmentDTO wall,
        int startingSequence,
        List<CabinetPlanValidationMessageDTO> messages
    ) {
        List<ReservedRange> reservedRanges = reservedBaseRanges(layout, wall);
        List<CabinetPlanItemDTO> cabinets = new ArrayList<>();
        int sequence = startingSequence;
        for (ReservedRange range : reservedRanges) {
            CabinetPlanItemDTO cabinet = cabinetFromReservedRange(layout, wall, range, sequence++);
            cabinets.add(cabinet);
        }

        int cursor = 0;
        for (ReservedRange range : reservedRanges) {
            sequence = fillGap(layout, wall, cursor, range.startMm(), sequence, cabinets, messages);
            cursor = Math.max(cursor, range.endMm());
        }
        fillGap(layout, wall, cursor, wall.getLengthMm(), sequence, cabinets, messages);

        cabinets.sort(Comparator.comparing(CabinetPlanItemDTO::getWallCode).thenComparing(CabinetPlanItemDTO::getxMm));
        int occupied = cabinets
            .stream()
            .filter(cabinet -> wall.getWallCode().equals(cabinet.getWallCode()))
            .map(CabinetPlanItemDTO::getWidthMm)
            .reduce(0, Integer::sum);
        if (occupied > wall.getLengthMm()) {
            messages.add(
                message(
                    "ERROR",
                    "WALL_OVERFILLED",
                    "Los módulos exceden el largo disponible de pared " + wall.getWallCode() + ".",
                    wall.getWallCode(),
                    null
                )
            );
        }
        return new WallPlan(cabinets);
    }

    private List<MeasuredWallSegmentDTO> sortedWalls(MeasuredLayoutRequestDTO layout) {
        return layout
            .getWalls()
            .stream()
            .sorted(Comparator.comparing(wall -> wall.getSortOrder() == null ? Integer.MAX_VALUE : wall.getSortOrder()))
            .toList();
    }

    private List<ReservedRange> reservedBaseRanges(MeasuredLayoutRequestDTO layout, MeasuredWallSegmentDTO wall) {
        List<ReservedRange> ranges = new ArrayList<>();
        if (layout.getZones() != null) {
            for (LayoutZoneDTO zone : layout.getZones()) {
                if (wall.getWallCode().equals(zone.getWallCode())) {
                    int width = positiveOrDefault(zone.getWidthMm(), defaultZoneWidth(zone.getZoneType()));
                    int clearanceLeft = positiveOrDefault(zone.getClearanceLeftMm(), 0);
                    int clearanceRight = positiveOrDefault(zone.getClearanceRightMm(), 0);
                    ranges.add(
                        new ReservedRange(
                            Math.max(0, zone.getxMm() - clearanceLeft),
                            Math.min(wall.getLengthMm(), zone.getxMm() + width + clearanceRight),
                            zone.getZoneType(),
                            zone.getZoneCode()
                        )
                    );
                }
            }
        }
        if (layout.getObstacles() != null) {
            for (LayoutObstacleDTO obstacle : layout.getObstacles()) {
                if (wall.getWallCode().equals(obstacle.getWallCode()) && blocksBaseCabinets(obstacle)) {
                    int width = positiveOrDefault(obstacle.getWidthMm(), 600);
                    ranges.add(
                        new ReservedRange(
                            Math.max(0, obstacle.getxMm()),
                            Math.min(wall.getLengthMm(), obstacle.getxMm() + width),
                            obstacle.getObstacleType().name(),
                            null
                        )
                    );
                }
            }
        }
        return ranges.stream().sorted(Comparator.comparing(ReservedRange::startMm)).toList();
    }

    private boolean blocksBaseCabinets(LayoutObstacleDTO obstacle) {
        if (obstacle.getObstacleType() == RoomObstacleType.WINDOW || obstacle.getObstacleType() == RoomObstacleType.OUTLET) {
            Integer zMm = obstacle.getzMm();
            return zMm != null && zMm < BASE_BLOCK_TOP_MM;
        }
        return (
            obstacle.getObstacleType() == RoomObstacleType.DOOR ||
            obstacle.getObstacleType() == RoomObstacleType.COLUMN ||
            obstacle.getObstacleType() == RoomObstacleType.APPLIANCE
        );
    }

    private CabinetPlanItemDTO cabinetFromReservedRange(
        MeasuredLayoutRequestDTO layout,
        MeasuredWallSegmentDTO wall,
        ReservedRange range,
        int sequence
    ) {
        String normalizedType = range.type() == null ? "APPLIANCE" : range.type().trim().toUpperCase();
        CabinetCategory category = switch (normalizedType) {
            case "SINK" -> CabinetCategory.SINK;
            case "RANGE", "STOVE", "COOKTOP", "REFRIGERATOR", "FRIDGE", "DISHWASHER", "APPLIANCE" -> CabinetCategory.APPLIANCE;
            default -> CabinetCategory.FILLER;
        };
        TemplateProfile template = templateFor(category);
        int width = Math.max(MIN_MODULE_WIDTH_MM, range.endMm() - range.startMm());
        CabinetPlanItemDTO cabinet = baseCabinet(layout, wall, range.startMm(), width, sequence, category, template);
        cabinet.setLabel(labelForReservedRange(category, range));
        cabinet.setNotes("Reserva generada desde zona/obstáculo medido: " + range.type());
        return cabinet;
    }

    private int fillGap(
        MeasuredLayoutRequestDTO layout,
        MeasuredWallSegmentDTO wall,
        int startMm,
        int endMm,
        int sequence,
        List<CabinetPlanItemDTO> cabinets,
        List<CabinetPlanValidationMessageDTO> messages
    ) {
        int gap = endMm - startMm;
        if (gap <= 0) {
            return sequence;
        }
        if (gap < MIN_MODULE_WIDTH_MM) {
            messages.add(
                message(
                    "WARNING",
                    "FILLER_RECOMMENDED",
                    "Quedan " + gap + " mm libres en pared " + wall.getWallCode() + "; considerar relleno.",
                    wall.getWallCode(),
                    null
                )
            );
            return sequence;
        }
        int cursor = startMm;
        while (endMm - cursor >= MIN_MODULE_WIDTH_MM) {
            int remaining = endMm - cursor;
            int width = remaining > DEFAULT_MODULE_WIDTH_MM + MIN_MODULE_WIDTH_MM ? DEFAULT_MODULE_WIDTH_MM : remaining;
            CabinetCategory category = width >= 700 ? CabinetCategory.DRAWER_BASE : CabinetCategory.LOWER;
            CabinetPlanItemDTO cabinet = baseCabinet(layout, wall, cursor, width, sequence++, category, templateFor(category));
            cabinet.setLabel(category == CabinetCategory.DRAWER_BASE ? "Base con cajones " + width : "Base puertas " + width);
            cabinets.add(cabinet);
            cursor += width;
        }
        if (endMm - cursor > 0) {
            messages.add(
                message(
                    "WARNING",
                    "FILLER_RECOMMENDED",
                    "Quedan " + (endMm - cursor) + " mm libres en pared " + wall.getWallCode() + "; considerar relleno.",
                    wall.getWallCode(),
                    null
                )
            );
        }
        return sequence;
    }

    private CabinetPlanItemDTO baseCabinet(
        MeasuredLayoutRequestDTO layout,
        MeasuredWallSegmentDTO wall,
        int xMm,
        int widthMm,
        int sequence,
        CabinetCategory category,
        TemplateProfile template
    ) {
        CabinetPlanItemDTO cabinet = new CabinetPlanItemDTO();
        cabinet.setCabinetCode(wall.getWallCode() + "-" + String.format("%03d", sequence));
        cabinet.setTemplateCode(template.code());
        cabinet.setCategory(category);
        cabinet.setWidthMm(widthMm);
        cabinet.setHeightMm(template.heightMm());
        cabinet.setDepthMm(
            category == CabinetCategory.UPPER
                ? DEFAULT_UPPER_DEPTH_MM
                : positiveOrDefault(layout.getDefaultBaseDepthMm(), template.depthMm())
        );
        cabinet.setDoors(category == CabinetCategory.DRAWER_BASE ? 0 : 2);
        cabinet.setDrawers(category == CabinetCategory.DRAWER_BASE ? 3 : 0);
        cabinet.setShelves(category == CabinetCategory.DRAWER_BASE ? 0 : 1);
        cabinet.setFinish(resolveFinish(null));
        cabinet.setWallCode(wall.getWallCode());
        cabinet.setxMm(xMm);
        cabinet.setyMm(0);
        cabinet.setzMm(0);
        cabinet.setRotationDeg(positiveOrDefault(wall.getAngleDeg(), 0));
        cabinet.setPositionSeq(sequence);
        cabinet.setMaterialCode("MEL-18-DEFAULT");
        cabinet.setNotes("Generado automáticamente desde layout medido.");
        return cabinet;
    }

    private TemplateProfile templateFor(CabinetCategory category) {
        return cabinetTemplateRepository
            .findByCategoryAndIsActiveTrueOrderBySortOrderAsc(category)
            .stream()
            .filter(this::hasUsableDimensions)
            .findFirst()
            .map(template -> new TemplateProfile(template.getCode(), template.getDefaultHeightMm(), template.getDefaultDepthMm()))
            .orElseGet(() -> fallbackTemplate(category));
    }

    private boolean hasUsableDimensions(CabinetTemplate template) {
        return (
            template.getDefaultHeightMm() != null &&
            template.getDefaultHeightMm() >= 300 &&
            template.getDefaultHeightMm() <= 2400 &&
            template.getDefaultDepthMm() != null &&
            template.getDefaultDepthMm() >= 250 &&
            template.getDefaultDepthMm() <= 900
        );
    }

    private TemplateProfile fallbackTemplate(CabinetCategory category) {
        return new TemplateProfile("AUTO_" + category.name(), DEFAULT_BASE_HEIGHT_MM, DEFAULT_BASE_DEPTH_MM);
    }

    private KitchenSpec ensureKitchenSpec(DesignSession session, MeasuredLayoutRequestDTO layout) {
        return kitchenSpecRepository
            .findBySessionId(session.getId())
            .or(() -> Optional.ofNullable(session.getSpec()))
            .orElseGet(() -> {
                KitchenSpec spec = new KitchenSpec()
                    .layout(layout.getLayout())
                    .totalWidthMm(layout.getWalls().stream().map(MeasuredWallSegmentDTO::getLengthMm).reduce(0, Integer::sum))
                    .totalHeightMm(layout.getRoomHeightMm())
                    .totalDepthMm(positiveOrDefault(layout.getDefaultBaseDepthMm(), DEFAULT_BASE_DEPTH_MM))
                    .style(session.getSelectedStyle() == null ? "Sin estilo" : session.getSelectedStyle())
                    .primaryFinish(resolveFinish(session.getSelectedStyle()))
                    .confirmedByClient(false)
                    .primaryMaterial(ensureMaterial());
                KitchenSpec savedSpec = kitchenSpecRepository.save(spec);
                session.setSpec(savedSpec);
                return savedSpec;
            });
    }

    private Material ensureMaterial() {
        return materialRepository
            .findFirstByMaterialKindAndIsActiveTrueOrderByIdAsc(MaterialKind.MELAMINE)
            .or(() -> materialRepository.findFirstByIsActiveTrueOrderByIdAsc())
            .orElseGet(() ->
                materialRepository.save(
                    new Material()
                        .code("MEL-18-DEFAULT")
                        .name("Melamina 18 mm default")
                        .materialKind(MaterialKind.MELAMINE)
                        .thicknessMm(18)
                        .isActive(true)
                )
            );
    }

    private Cabinet toCabinet(CabinetPlanItemDTO dto, KitchenSpec spec, Material material) {
        return new Cabinet()
            .cabinetCode(dto.getCabinetCode())
            .category(dto.getCategory())
            .label(dto.getLabel())
            .widthMm(dto.getWidthMm())
            .heightMm(dto.getHeightMm())
            .depthMm(dto.getDepthMm())
            .doors(positiveOrDefault(dto.getDoors(), 0))
            .drawers(positiveOrDefault(dto.getDrawers(), 0))
            .shelves(positiveOrDefault(dto.getShelves(), 0))
            .finish(dto.getFinish() == null ? FinishType.CUSTOM : dto.getFinish())
            .positionX(dto.getxMm())
            .positionY(dto.getyMm())
            .positionZ(dto.getzMm())
            .rotationDeg(dto.getRotationDeg())
            .positionSeq(dto.getPositionSeq())
            .csvRowJson(write(dto))
            .notes(limit(dto.getNotes(), 300))
            .material(material)
            .spec(spec);
    }

    private void persistCabinetPlanSnapshot(DesignSession session, CabinetPlanResponseDTO response) {
        DesignArtifact artifact = designArtifactRepository
            .findFirstBySessionIdAndFileNameOrderByCreatedAtDesc(session.getId(), CABINET_PLAN_FILE_NAME)
            .orElseGet(DesignArtifact::new);

        artifact
            .session(session)
            .artifactType(ArtifactType.OTHER)
            .fileName(CABINET_PLAN_FILE_NAME)
            .filePath("inline://design-sessions/" + session.getSessionCode() + "/cabinet-plan")
            .mimeType("application/json")
            .createdAt(artifact.getCreatedAt() != null ? artifact.getCreatedAt() : Instant.now())
            .metadataJson(write(response));
        designArtifactRepository.save(artifact);
    }

    private Optional<CabinetPlanResponseDTO> readCabinetPlanSnapshot(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(metadataJson, CabinetPlanResponseDTO.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cabinet plan could not be serialized", e);
        }
    }

    private CabinetPlanValidationMessageDTO message(String severity, String code, String message, String wallCode, String cabinetCode) {
        CabinetPlanValidationMessageDTO dto = new CabinetPlanValidationMessageDTO();
        dto.setSeverity(severity);
        dto.setCode(code);
        dto.setMessage(message);
        dto.setWallCode(wallCode);
        dto.setCabinetCode(cabinetCode);
        return dto;
    }

    private int defaultZoneWidth(String zoneType) {
        if (zoneType == null) {
            return DEFAULT_MODULE_WIDTH_MM;
        }
        return switch (zoneType.trim().toUpperCase()) {
            case "RANGE", "STOVE", "COOKTOP" -> 760;
            case "REFRIGERATOR", "FRIDGE" -> 900;
            case "DISHWASHER" -> 600;
            default -> DEFAULT_MODULE_WIDTH_MM;
        };
    }

    private String labelForReservedRange(CabinetCategory category, ReservedRange range) {
        return switch (category) {
            case SINK -> "Base tarja " + (range.endMm() - range.startMm());
            case APPLIANCE -> "Módulo electrodoméstico " + (range.endMm() - range.startMm());
            default -> "Reserva " + (range.endMm() - range.startMm());
        };
    }

    private FinishType resolveFinish(String selectedStyle) {
        if (selectedStyle == null) {
            return FinishType.CUSTOM;
        }
        String normalized = selectedStyle.toLowerCase();
        if (normalized.contains("blanco") || normalized.contains("white")) {
            return normalized.contains("brillo") || normalized.contains("gloss") ? FinishType.HIGH_GLOSS_WHITE : FinishType.MATTE_WHITE;
        }
        if (normalized.contains("negro") || normalized.contains("black")) {
            return FinishType.MATTE_BLACK;
        }
        if (normalized.contains("gris") || normalized.contains("gray")) {
            return FinishType.MATTE_GRAY;
        }
        if (normalized.contains("madera") || normalized.contains("wood")) {
            return FinishType.WOOD_OAK;
        }
        return FinishType.CUSTOM;
    }

    private int positiveOrDefault(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private record ReservedRange(int startMm, int endMm, String type, String code) {}

    private record WallPlan(List<CabinetPlanItemDTO> cabinets) {}

    private record TemplateProfile(String code, int heightMm, int depthMm) {}
}
