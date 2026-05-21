package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.CabinetTemplate;
import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.KitchenLayout;
import com.kalitron.studio.domain.enumeration.RoomObstacleType;
import com.kalitron.studio.repository.CabinetTemplateRepository;
import com.kalitron.studio.service.dto.CabinetPlanItemDTO;
import com.kalitron.studio.service.dto.CabinetPlanValidationMessageDTO;
import com.kalitron.studio.service.dto.LayoutObstacleDTO;
import com.kalitron.studio.service.dto.LayoutZoneDTO;
import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import com.kalitron.studio.service.dto.MeasuredWallSegmentDTO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CabinetPlanValidator {

    private static final int BASE_BLOCK_TOP_MM = 900;

    private final CabinetTemplateRepository cabinetTemplateRepository;

    public CabinetPlanValidator(CabinetTemplateRepository cabinetTemplateRepository) {
        this.cabinetTemplateRepository = cabinetTemplateRepository;
    }

    public List<CabinetPlanValidationMessageDTO> validate(MeasuredLayoutRequestDTO layout, List<CabinetPlanItemDTO> cabinets) {
        List<CabinetPlanValidationMessageDTO> messages = new ArrayList<>();
        Map<String, MeasuredWallSegmentDTO> wallsByCode = wallsByCode(layout);

        validateLayoutWallCount(layout, wallsByCode.size(), messages);
        validateCabinetPlacement(wallsByCode, cabinets, messages);
        validateCabinetDimensions(cabinets, messages);
        validateZones(layout, cabinets, messages);
        validateObstacles(layout, cabinets, messages);
        return messages;
    }

    private void validateLayoutWallCount(MeasuredLayoutRequestDTO layout, int wallCount, List<CabinetPlanValidationMessageDTO> messages) {
        KitchenLayout kitchenLayout = layout.getLayout();
        int expectedMinWalls = switch (kitchenLayout) {
            case L_SHAPE -> 2;
            case U_SHAPE -> 3;
            case GALLEY -> 2;
            default -> 1;
        };
        if (wallCount < expectedMinWalls) {
            messages.add(
                message(
                    "WARNING",
                    "LAYOUT_WALL_COUNT_REVIEW",
                    "El layout " + kitchenLayout + " normalmente requiere al menos " + expectedMinWalls + " segmentos de pared.",
                    null,
                    null
                )
            );
        }
    }

    private void validateCabinetPlacement(
        Map<String, MeasuredWallSegmentDTO> wallsByCode,
        List<CabinetPlanItemDTO> cabinets,
        List<CabinetPlanValidationMessageDTO> messages
    ) {
        Map<String, List<CabinetPlanItemDTO>> cabinetsByWall = new HashMap<>();
        for (CabinetPlanItemDTO cabinet : cabinets) {
            MeasuredWallSegmentDTO wall = wallsByCode.get(cabinet.getWallCode());
            if (wall == null) {
                messages.add(
                    message(
                        "ERROR",
                        "UNKNOWN_WALL",
                        "El mueble " + cabinet.getCabinetCode() + " apunta a una pared que no existe: " + cabinet.getWallCode() + ".",
                        cabinet.getWallCode(),
                        cabinet.getCabinetCode()
                    )
                );
                continue;
            }
            if (cabinet.getxMm() < 0 || cabinet.getxMm() + cabinet.getWidthMm() > wall.getLengthMm()) {
                messages.add(
                    message(
                        "ERROR",
                        "CABINET_OUT_OF_WALL",
                        "El mueble " + cabinet.getCabinetCode() + " queda fuera del largo de pared " + wall.getWallCode() + ".",
                        wall.getWallCode(),
                        cabinet.getCabinetCode()
                    )
                );
            }
            cabinetsByWall.computeIfAbsent(cabinet.getWallCode(), ignored -> new ArrayList<>()).add(cabinet);
        }

        for (Map.Entry<String, List<CabinetPlanItemDTO>> entry : cabinetsByWall.entrySet()) {
            List<CabinetPlanItemDTO> wallCabinets = entry.getValue();
            wallCabinets.sort(Comparator.comparing(CabinetPlanItemDTO::getxMm));
            int occupiedWidth = wallCabinets.stream().map(CabinetPlanItemDTO::getWidthMm).reduce(0, Integer::sum);
            Integer wallLength = wallsByCode.get(entry.getKey()).getLengthMm();
            if (occupiedWidth > wallLength) {
                messages.add(
                    message(
                        "ERROR",
                        "WALL_OVERFILLED",
                        "La suma de módulos excede el largo disponible de pared " + entry.getKey() + ".",
                        entry.getKey(),
                        null
                    )
                );
            }
            for (int index = 1; index < wallCabinets.size(); index++) {
                CabinetPlanItemDTO previous = wallCabinets.get(index - 1);
                CabinetPlanItemDTO current = wallCabinets.get(index);
                if (previous.getxMm() + previous.getWidthMm() > current.getxMm()) {
                    messages.add(
                        message(
                            "ERROR",
                            "CABINET_OVERLAP",
                            "Los muebles " + previous.getCabinetCode() + " y " + current.getCabinetCode() + " se traslapan.",
                            entry.getKey(),
                            current.getCabinetCode()
                        )
                    );
                }
            }
        }
    }

    private void validateCabinetDimensions(List<CabinetPlanItemDTO> cabinets, List<CabinetPlanValidationMessageDTO> messages) {
        for (CabinetPlanItemDTO cabinet : cabinets) {
            DimensionLimits limits = limitsFor(cabinet);
            if (cabinet.getWidthMm() < limits.minWidthMm() || cabinet.getWidthMm() > limits.maxWidthMm()) {
                messages.add(
                    message(
                        "ERROR",
                        "CABINET_WIDTH_OUT_OF_RANGE",
                        "El ancho de " +
                            cabinet.getCabinetCode() +
                            " no está dentro del rango permitido: " +
                            limits.minWidthMm() +
                            "-" +
                            limits.maxWidthMm() +
                            " mm.",
                        cabinet.getWallCode(),
                        cabinet.getCabinetCode()
                    )
                );
            }
            if (cabinet.getHeightMm() < limits.minHeightMm() || cabinet.getHeightMm() > limits.maxHeightMm()) {
                messages.add(
                    message(
                        "ERROR",
                        "CABINET_HEIGHT_OUT_OF_RANGE",
                        "La altura de " +
                            cabinet.getCabinetCode() +
                            " no está dentro del rango esperado para " +
                            cabinet.getCategory() +
                            ".",
                        cabinet.getWallCode(),
                        cabinet.getCabinetCode()
                    )
                );
            }
            if (cabinet.getDepthMm() < limits.minDepthMm() || cabinet.getDepthMm() > limits.maxDepthMm()) {
                messages.add(
                    message(
                        "ERROR",
                        "CABINET_DEPTH_OUT_OF_RANGE",
                        "La profundidad de " +
                            cabinet.getCabinetCode() +
                            " no está dentro del rango esperado para " +
                            cabinet.getCategory() +
                            ".",
                        cabinet.getWallCode(),
                        cabinet.getCabinetCode()
                    )
                );
            }
        }
    }

    private void validateZones(
        MeasuredLayoutRequestDTO layout,
        List<CabinetPlanItemDTO> cabinets,
        List<CabinetPlanValidationMessageDTO> messages
    ) {
        if (layout.getZones() == null) {
            return;
        }
        for (LayoutZoneDTO zone : layout.getZones()) {
            int zoneStart = zone.getxMm();
            int zoneWidth = positiveOrDefault(zone.getWidthMm(), defaultZoneWidth(zone.getZoneType()));
            int zoneEnd = zoneStart + zoneWidth;
            CabinetCategory expectedCategory = expectedCategoryForZone(zone.getZoneType());
            boolean represented = cabinets
                .stream()
                .filter(cabinet -> Objects.equals(cabinet.getWallCode(), zone.getWallCode()))
                .filter(cabinet -> expectedCategory == null || cabinet.getCategory() == expectedCategory)
                .anyMatch(cabinet -> representsZone(cabinet, expectedCategory, zoneStart, zoneEnd));
            if (!represented) {
                messages.add(
                    message(
                        "ERROR",
                        "ZONE_NOT_REPRESENTED",
                        "La zona " + zone.getZoneCode() + " no está representada por un módulo compatible.",
                        zone.getWallCode(),
                        null
                    )
                );
            }
            if (positiveOrDefault(zone.getClearanceLeftMm(), 0) > 0 || positiveOrDefault(zone.getClearanceRightMm(), 0) > 0) {
                messages.add(
                    message(
                        "WARNING",
                        "CLEARANCE_REVIEW",
                        "La zona " + zone.getZoneCode() + " requiere revisar holguras laterales antes de fabricación.",
                        zone.getWallCode(),
                        null
                    )
                );
            }
        }
    }

    private boolean representsZone(CabinetPlanItemDTO cabinet, CabinetCategory expectedCategory, int zoneStart, int zoneEnd) {
        int cabinetStart = cabinet.getxMm();
        int cabinetEnd = cabinetStart + cabinet.getWidthMm();
        if (expectedCategory == CabinetCategory.SINK) {
            int zoneCenter = zoneStart + ((zoneEnd - zoneStart) / 2);
            return cabinetStart <= zoneCenter && cabinetEnd >= zoneCenter;
        }
        if (expectedCategory == CabinetCategory.APPLIANCE) {
            return cabinetStart <= zoneStart && cabinetEnd >= zoneEnd;
        }
        return overlaps(cabinetStart, cabinetEnd, zoneStart, zoneEnd);
    }

    private void validateObstacles(
        MeasuredLayoutRequestDTO layout,
        List<CabinetPlanItemDTO> cabinets,
        List<CabinetPlanValidationMessageDTO> messages
    ) {
        if (layout.getObstacles() == null) {
            return;
        }
        for (LayoutObstacleDTO obstacle : layout.getObstacles()) {
            if (!blocksBaseCabinets(obstacle)) {
                continue;
            }
            int obstacleStart = obstacle.getxMm();
            int obstacleEnd = obstacleStart + positiveOrDefault(obstacle.getWidthMm(), 600);
            Optional<CabinetPlanItemDTO> blockingCabinet = cabinets
                .stream()
                .filter(cabinet -> Objects.equals(cabinet.getWallCode(), obstacle.getWallCode()))
                .filter(cabinet -> overlaps(cabinet.getxMm(), cabinet.getxMm() + cabinet.getWidthMm(), obstacleStart, obstacleEnd))
                .filter(
                    cabinet ->
                        obstacle.getObstacleType() != RoomObstacleType.APPLIANCE || cabinet.getCategory() != CabinetCategory.APPLIANCE
                )
                .findFirst();
            blockingCabinet.ifPresent(cabinet ->
                messages.add(
                    message(
                        "ERROR",
                        "OBSTACLE_BLOCKED",
                        "El mueble " + cabinet.getCabinetCode() + " invade el obstáculo " + obstacle.getLabel() + ".",
                        obstacle.getWallCode(),
                        cabinet.getCabinetCode()
                    )
                )
            );
        }
    }

    private DimensionLimits limitsFor(CabinetPlanItemDTO cabinet) {
        if (cabinet.getTemplateCode() != null && !cabinet.getTemplateCode().startsWith("AUTO_")) {
            Optional<CabinetTemplate> template = cabinetTemplateRepository.findFirstByCodeAndIsActiveTrue(cabinet.getTemplateCode());
            if (template.isPresent()) {
                CabinetTemplate cabinetTemplate = template.get();
                return new DimensionLimits(
                    positiveOrDefault(cabinetTemplate.getMinWidthMm(), 250),
                    positiveOrDefault(cabinetTemplate.getMaxWidthMm(), 1200),
                    Math.max(100, positiveOrDefault(cabinetTemplate.getDefaultHeightMm(), cabinet.getHeightMm()) - 100),
                    positiveOrDefault(cabinetTemplate.getDefaultHeightMm(), cabinet.getHeightMm()) + 100,
                    Math.max(100, positiveOrDefault(cabinetTemplate.getDefaultDepthMm(), cabinet.getDepthMm()) - 100),
                    positiveOrDefault(cabinetTemplate.getDefaultDepthMm(), cabinet.getDepthMm()) + 100
                );
            }
        }
        return fallbackLimits(cabinet.getCategory());
    }

    private DimensionLimits fallbackLimits(CabinetCategory category) {
        return switch (category) {
            case UPPER -> new DimensionLimits(250, 1200, 300, 1200, 250, 450);
            case TALL -> new DimensionLimits(300, 1200, 1200, 2600, 450, 700);
            case FILLER -> new DimensionLimits(20, 300, 100, 2400, 20, 700);
            case PANEL -> new DimensionLimits(20, 1200, 100, 2600, 10, 80);
            default -> new DimensionLimits(250, 1200, 650, 900, 450, 700);
        };
    }

    private CabinetCategory expectedCategoryForZone(String zoneType) {
        if (zoneType == null) {
            return null;
        }
        return switch (zoneType.trim().toUpperCase()) {
            case "SINK" -> CabinetCategory.SINK;
            case "RANGE", "STOVE", "COOKTOP", "REFRIGERATOR", "FRIDGE", "DISHWASHER", "APPLIANCE" -> CabinetCategory.APPLIANCE;
            default -> null;
        };
    }

    private Map<String, MeasuredWallSegmentDTO> wallsByCode(MeasuredLayoutRequestDTO layout) {
        Map<String, MeasuredWallSegmentDTO> wallsByCode = new HashMap<>();
        for (MeasuredWallSegmentDTO wall : layout.getWalls()) {
            wallsByCode.put(wall.getWallCode(), wall);
        }
        return wallsByCode;
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

    private boolean overlaps(int startA, int endA, int startB, int endB) {
        return startA < endB && startB < endA;
    }

    private int defaultZoneWidth(String zoneType) {
        if (zoneType == null) {
            return 800;
        }
        return switch (zoneType.trim().toUpperCase()) {
            case "RANGE", "STOVE", "COOKTOP" -> 760;
            case "REFRIGERATOR", "FRIDGE" -> 900;
            case "DISHWASHER" -> 600;
            default -> 800;
        };
    }

    private int positiveOrDefault(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
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

    private record DimensionLimits(int minWidthMm, int maxWidthMm, int minHeightMm, int maxHeightMm, int minDepthMm, int maxDepthMm) {}
}
