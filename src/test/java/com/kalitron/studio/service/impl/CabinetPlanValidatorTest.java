package com.kalitron.studio.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
import java.util.List;
import org.junit.jupiter.api.Test;

class CabinetPlanValidatorTest {

    private final CabinetPlanValidator validator = new CabinetPlanValidator(mock(CabinetTemplateRepository.class));

    @Test
    void validatesPlanWithoutWarningsForRepresentedSinkAndWallFit() {
        MeasuredLayoutRequestDTO layout = layout(KitchenLayout.LINEAR, List.of(wall("A", 1600)));
        layout.setZones(List.of(zone("SINK-1", "SINK", "A", 0, 800)));

        List<CabinetPlanValidationMessageDTO> messages = validator.validate(
            layout,
            List.of(cabinet("A-001", CabinetCategory.SINK, "A", 0, 800), cabinet("A-002", CabinetCategory.LOWER, "A", 800, 800))
        );

        assertThat(messages).isEmpty();
    }

    @Test
    void returnsStructuredWarningsAndErrorsForInvalidPlan() {
        MeasuredLayoutRequestDTO layout = layout(KitchenLayout.L_SHAPE, List.of(wall("A", 1000)));
        layout.setZones(List.of(zone("SINK-1", "SINK", "A", 500, 300)));
        layout.setObstacles(List.of(obstacle(RoomObstacleType.DOOR, "Puerta", "A", 50, 0, 200)));

        List<CabinetPlanValidationMessageDTO> messages = validator.validate(
            layout,
            List.of(cabinet("A-001", CabinetCategory.LOWER, "A", 900, 300), cabinet("A-002", CabinetCategory.LOWER, "A", 100, 100))
        );

        assertThat(messages)
            .extracting(CabinetPlanValidationMessageDTO::getCode)
            .contains(
                "LAYOUT_WALL_COUNT_REVIEW",
                "CABINET_OUT_OF_WALL",
                "CABINET_WIDTH_OUT_OF_RANGE",
                "ZONE_NOT_REPRESENTED",
                "OBSTACLE_BLOCKED"
            );
        assertThat(messages).anyMatch(message -> "ERROR".equals(message.getSeverity()));
    }

    private MeasuredLayoutRequestDTO layout(KitchenLayout kitchenLayout, List<MeasuredWallSegmentDTO> walls) {
        MeasuredLayoutRequestDTO layout = new MeasuredLayoutRequestDTO();
        layout.setLayout(kitchenLayout);
        layout.setRoomHeightMm(2400);
        layout.setDefaultBaseDepthMm(600);
        layout.setDefaultUpperDepthMm(350);
        layout.setWalls(walls);
        return layout;
    }

    private MeasuredWallSegmentDTO wall(String wallCode, int lengthMm) {
        MeasuredWallSegmentDTO wall = new MeasuredWallSegmentDTO();
        wall.setWallCode(wallCode);
        wall.setLengthMm(lengthMm);
        wall.setHeightMm(2400);
        wall.setAngleDeg(0);
        wall.setSortOrder(1);
        return wall;
    }

    private CabinetPlanItemDTO cabinet(String code, CabinetCategory category, String wallCode, int xMm, int widthMm) {
        CabinetPlanItemDTO cabinet = new CabinetPlanItemDTO();
        cabinet.setCabinetCode(code);
        cabinet.setTemplateCode("AUTO_" + category.name());
        cabinet.setCategory(category);
        cabinet.setLabel(code);
        cabinet.setWallCode(wallCode);
        cabinet.setxMm(xMm);
        cabinet.setyMm(0);
        cabinet.setzMm(0);
        cabinet.setWidthMm(widthMm);
        cabinet.setHeightMm(720);
        cabinet.setDepthMm(600);
        return cabinet;
    }

    private LayoutZoneDTO zone(String zoneCode, String zoneType, String wallCode, int xMm, int widthMm) {
        LayoutZoneDTO zone = new LayoutZoneDTO();
        zone.setZoneCode(zoneCode);
        zone.setZoneType(zoneType);
        zone.setWallCode(wallCode);
        zone.setxMm(xMm);
        zone.setWidthMm(widthMm);
        return zone;
    }

    private LayoutObstacleDTO obstacle(RoomObstacleType obstacleType, String label, String wallCode, int xMm, int zMm, int widthMm) {
        LayoutObstacleDTO obstacle = new LayoutObstacleDTO();
        obstacle.setObstacleType(obstacleType);
        obstacle.setLabel(label);
        obstacle.setWallCode(wallCode);
        obstacle.setxMm(xMm);
        obstacle.setzMm(zMm);
        obstacle.setWidthMm(widthMm);
        obstacle.setHeightMm(2100);
        return obstacle;
    }
}
