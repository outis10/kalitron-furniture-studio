package com.kalitron.studio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.GenerationJob;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.domain.enumeration.CabinetCategory;
import com.kalitron.studio.domain.enumeration.GenerationJobStatus;
import com.kalitron.studio.domain.enumeration.GenerationJobType;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.GenerationJobRepository;
import com.kalitron.studio.service.CabinetPlanService;
import com.kalitron.studio.service.MeasuredLayoutService;
import com.kalitron.studio.service.Prototype3dService;
import com.kalitron.studio.service.dto.CabinetPlanItemDTO;
import com.kalitron.studio.service.dto.CabinetPlanResponseDTO;
import com.kalitron.studio.service.dto.LayoutObstacleDTO;
import com.kalitron.studio.service.dto.LayoutZoneDTO;
import com.kalitron.studio.service.dto.MeasuredLayoutRequestDTO;
import com.kalitron.studio.service.dto.MeasuredWallSegmentDTO;
import com.kalitron.studio.service.dto.Prototype3dArtifactItemDTO;
import com.kalitron.studio.service.dto.Prototype3dJobDTO;
import com.kalitron.studio.service.dto.Prototype3dRequestDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Prototype3dServiceImpl implements Prototype3dService {

    private static final int CANVAS_W = 1400;
    private static final int CANVAS_H = 1400;
    private static final int FLOOR_PLAN_H = 950;
    private static final int PADDING = 100;
    private static final int LEGEND_W = 180;
    private static final float WALL_STROKE = 8f;
    // Cabinets mounted at or above this height are shown as overhead (dashed) in the floor plan
    private static final int OVERHEAD_THRESHOLD_MM = 900;
    private static final float OBSTACLE_STROKE = 2f;

    private final DesignSessionRepository designSessionRepository;
    private final MeasuredLayoutService measuredLayoutService;
    private final CabinetPlanService cabinetPlanService;
    private final DesignArtifactRepository designArtifactRepository;
    private final GenerationJobRepository generationJobRepository;
    private final ObjectMapper objectMapper;
    private final Path outputDir;

    public Prototype3dServiceImpl(
        DesignSessionRepository designSessionRepository,
        MeasuredLayoutService measuredLayoutService,
        CabinetPlanService cabinetPlanService,
        DesignArtifactRepository designArtifactRepository,
        GenerationJobRepository generationJobRepository,
        ObjectMapper objectMapper,
        @Value("${app.output.dir:./outputs}") String outputDir
    ) {
        this.designSessionRepository = designSessionRepository;
        this.measuredLayoutService = measuredLayoutService;
        this.cabinetPlanService = cabinetPlanService;
        this.designArtifactRepository = designArtifactRepository;
        this.generationJobRepository = generationJobRepository;
        this.objectMapper = objectMapper;
        this.outputDir = Path.of(outputDir);
    }

    @Override
    public Prototype3dJobDTO generatePrototype(Long sessionId, Prototype3dRequestDTO request) {
        DesignSession session = designSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Design session not found"));

        MeasuredLayoutRequestDTO layout = measuredLayoutService
            .findMeasuredLayout(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Measured layout not found"));

        CabinetPlanResponseDTO cabinetPlan = cabinetPlanService
            .findCabinetPlan(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Cabinet plan not found"));

        GenerationJob job = new GenerationJob()
            .session(session)
            .jobType(GenerationJobType.PROTOTYPE_3D)
            .status(GenerationJobStatus.RUNNING)
            .createdAt(Instant.now())
            .startedAt(Instant.now());
        generationJobRepository.save(job);

        try {
            String sessionCode = session.getSessionCode();
            List<DesignArtifact> artifacts = new ArrayList<>();

            byte[] previewBytes = generateFloorPlanPng(layout, cabinetPlan, request);
            String previewPath = "prototype/" + sessionCode + "/prototype-preview.png";
            writeOutputFile(previewPath, previewBytes);
            DesignArtifact previewArtifact = saveArtifact(
                session,
                ArtifactType.PROTOTYPE_PREVIEW,
                "prototype-preview.png",
                previewPath,
                "image/png",
                previewBytes.length
            );
            artifacts.add(previewArtifact);

            Map<String, Object> metadata = buildMetadata(session, layout, cabinetPlan, request);
            byte[] metadataBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(metadata);
            String metadataPath = "prototype/" + sessionCode + "/prototype-metadata.json";
            writeOutputFile(metadataPath, metadataBytes);
            DesignArtifact metadataArtifact = saveArtifact(
                session,
                ArtifactType.PROTOTYPE_METADATA,
                "prototype-metadata.json",
                metadataPath,
                "application/json",
                metadataBytes.length
            );
            artifacts.add(metadataArtifact);

            job.setStatus(GenerationJobStatus.DONE);
            job.setFinishedAt(Instant.now());
            job.setArtifact(previewArtifact);
            generationJobRepository.save(job);

            List<String> warnings = new ArrayList<>();
            boolean fromSketch = cabinetPlan
                .getCabinets()
                .stream()
                .anyMatch(c -> c.getNotes() != null && c.getNotes().contains("boceto"));
            if (fromSketch) {
                warnings.add("Prototipo generado desde extracción de boceto. Revisar dimensiones antes de fabricación.");
            }
            cabinetPlan
                .getValidationMessages()
                .stream()
                .filter(m -> "ERROR".equals(m.getSeverity()))
                .map(m -> "Advertencia de validación: " + m.getMessage())
                .forEach(warnings::add);

            return toJobDTO(job, session, artifacts, warnings, request.getPrototypeMode());
        } catch (Exception e) {
            job.setStatus(GenerationJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setFinishedAt(Instant.now());
            generationJobRepository.save(job);
            throw new IllegalStateException("Prototype generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prototype3dJobDTO> getJobStatus(Long sessionId, Long jobId) {
        return generationJobRepository
            .findBySessionIdAndJobTypeAndId(sessionId, GenerationJobType.PROTOTYPE_3D, jobId)
            .map(job -> {
                List<DesignArtifact> artifacts = designArtifactRepository.findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(
                    sessionId,
                    List.of(ArtifactType.PROTOTYPE_PREVIEW, ArtifactType.PROTOTYPE_METADATA)
                );
                return toJobDTO(job, job.getSession(), artifacts, List.of(), "BLOCKOUT");
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prototype3dJobDTO> getLatestJob(Long sessionId) {
        return generationJobRepository
            .findFirstBySessionIdAndJobTypeOrderByCreatedAtDesc(sessionId, GenerationJobType.PROTOTYPE_3D)
            .map(job -> {
                List<DesignArtifact> artifacts = designArtifactRepository.findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(
                    sessionId,
                    List.of(ArtifactType.PROTOTYPE_PREVIEW, ArtifactType.PROTOTYPE_METADATA)
                );
                return toJobDTO(job, job.getSession(), artifacts, List.of(), "BLOCKOUT");
            });
    }

    // ── Floor plan generator ──────────────────────────────────────────────────

    private record WallData(String code, double startX, double startY, double endX, double endY, double angleDeg) {}

    private byte[] generateFloorPlanPng(MeasuredLayoutRequestDTO layout, CabinetPlanResponseDTO plan, Prototype3dRequestDTO req)
        throws IOException {
        List<WallData> walls = resolveWallPositions(layout.getWalls());

        double minX = walls
            .stream()
            .mapToDouble(w -> Math.min(w.startX(), w.endX()))
            .min()
            .orElse(0);
        double minY = walls
            .stream()
            .mapToDouble(w -> Math.min(w.startY(), w.endY()))
            .min()
            .orElse(0);
        double maxX = walls
            .stream()
            .mapToDouble(w -> Math.max(w.startX(), w.endX()))
            .max()
            .orElse(1000);
        double maxY = walls
            .stream()
            .mapToDouble(w -> Math.max(w.startY(), w.endY()))
            .max()
            .orElse(1000);

        // Expand bounding box to include cabinet depths
        for (CabinetPlanItemDTO c : plan.getCabinets()) {
            WallData wall = findWall(walls, c.getWallCode());
            if (wall == null || c.getxMm() == null || c.getWidthMm() == null || c.getDepthMm() == null) continue;
            double[] bounds = cabinetBounds(wall, c.getxMm(), c.getWidthMm(), c.getDepthMm());
            minX = Math.min(minX, bounds[0]);
            minY = Math.min(minY, bounds[1]);
            maxX = Math.max(maxX, bounds[2]);
            maxY = Math.max(maxY, bounds[3]);
        }

        double roomW = Math.max(maxX - minX, 1);
        double roomH = Math.max(maxY - minY, 1);
        int drawW = CANVAS_W - PADDING * 2 - LEGEND_W;
        int drawH = FLOOR_PLAN_H - PADDING * 2;
        double scale = Math.min(drawW / roomW, drawH / roomH);

        BufferedImage img = new BufferedImage(CANVAS_W, CANVAS_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g.setColor(new Color(250, 250, 248));
        g.fillRect(0, 0, CANVAS_W, CANVAS_H);

        // Grid
        drawGrid(g, minX, minY, roomW, roomH, scale, PADDING);

        // Zones
        if (req.isIncludeZones()) {
            for (LayoutZoneDTO zone : layout.getZones()) {
                WallData wall = findWall(walls, zone.getWallCode());
                if (wall == null || zone.getxMm() == null || zone.getWidthMm() == null) continue;
                drawZone(g, wall, zone, minX, minY, scale, req.isIncludeLabels());
            }
        }

        // Obstacles
        if (req.isIncludeObstacles()) {
            for (LayoutObstacleDTO obs : layout.getObstacles()) {
                WallData wall = findWall(walls, obs.getWallCode());
                if (wall == null || obs.getxMm() == null || obs.getWidthMm() == null) continue;
                drawObstacle(g, wall, obs, minX, minY, scale, req.isIncludeLabels());
            }
        }

        // Cabinets — floor-level first, overhead last (dashed = mounted above OVERHEAD_THRESHOLD_MM)
        for (CabinetPlanItemDTO cabinet : plan.getCabinets()) {
            if (isOverhead(cabinet)) continue;
            WallData wall = findWall(walls, cabinet.getWallCode());
            if (wall == null || cabinet.getxMm() == null || cabinet.getWidthMm() == null || cabinet.getDepthMm() == null) continue;
            drawCabinet(g, wall, cabinet, minX, minY, scale, req.isIncludeLabels());
        }
        for (CabinetPlanItemDTO cabinet : plan.getCabinets()) {
            if (!isOverhead(cabinet)) continue;
            WallData wall = findWall(walls, cabinet.getWallCode());
            if (wall == null || cabinet.getxMm() == null || cabinet.getWidthMm() == null || cabinet.getDepthMm() == null) continue;
            drawCabinet(g, wall, cabinet, minX, minY, scale, req.isIncludeLabels());
        }

        // Walls (drawn on top of fill)
        g.setColor(new Color(40, 40, 40));
        g.setStroke(new BasicStroke(WALL_STROKE, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        for (WallData wall : walls) {
            int x1 = toCanvasX(wall.startX(), minX, scale);
            int y1 = toCanvasY(wall.startY(), minY, scale);
            int x2 = toCanvasX(wall.endX(), minX, scale);
            int y2 = toCanvasY(wall.endY(), minY, scale);
            g.drawLine(x1, y1, x2, y2);
        }

        // Wall codes
        if (req.isIncludeLabels()) {
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.setColor(new Color(30, 30, 30));
            for (WallData wall : walls) {
                int mx = toCanvasX((wall.startX() + wall.endX()) / 2, minX, scale);
                int my = toCanvasY((wall.startY() + wall.endY()) / 2, minY, scale);
                g.drawString(wall.code(), mx + 4, my - 6);
            }
        }

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(new Color(30, 30, 30));
        g.drawString("Prototipo de planta — " + layout.getLayout().name().replace('_', ' '), PADDING, 30);

        // Scale indicator
        int scaleMm = 500;
        int scalePx = (int) (scaleMm * scale);
        int scaleX = PADDING;
        int scaleY = FLOOR_PLAN_H - 30;
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(80, 80, 80));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawLine(scaleX, scaleY, scaleX + scalePx, scaleY);
        g.drawLine(scaleX, scaleY - 4, scaleX, scaleY + 4);
        g.drawLine(scaleX + scalePx, scaleY - 4, scaleX + scalePx, scaleY + 4);
        g.drawString("500 mm", scaleX + scalePx / 2 - 20, scaleY - 6);

        // Legend
        drawLegend(g, plan.getCabinets(), layout.getZones());

        // Elevation views below the floor plan
        drawElevations(g, walls, plan.getCabinets(), layout, req.isIncludeLabels());

        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }

    private List<WallData> resolveWallPositions(List<MeasuredWallSegmentDTO> walls) {
        List<WallData> result = new ArrayList<>();
        double curX = 0;
        double curY = 0;
        for (MeasuredWallSegmentDTO wall : walls
            .stream()
            .sorted((a, b) -> {
                int sa = a.getSortOrder() != null ? a.getSortOrder() : 999;
                int sb = b.getSortOrder() != null ? b.getSortOrder() : 999;
                return Integer.compare(sa, sb);
            })
            .toList()) {
            double sx = wall.getStartXMm() != null ? wall.getStartXMm() : curX;
            double sy = wall.getStartYMm() != null ? wall.getStartYMm() : curY;
            double angleDeg = wall.getAngleDeg() != null ? wall.getAngleDeg() : 0;
            double rad = Math.toRadians(angleDeg);
            double ex = sx + wall.getLengthMm() * Math.cos(rad);
            double ey = sy + wall.getLengthMm() * Math.sin(rad);
            result.add(new WallData(wall.getWallCode(), sx, sy, ex, ey, angleDeg));
            curX = ex;
            curY = ey;
        }
        return result;
    }

    private boolean isOverhead(CabinetPlanItemDTO cabinet) {
        if (cabinet.getzMm() != null && cabinet.getzMm() >= OVERHEAD_THRESHOLD_MM) return true;
        return cabinet.getCategory() == CabinetCategory.UPPER;
    }

    private WallData findWall(List<WallData> walls, String wallCode) {
        if (wallCode == null) return null;
        return walls
            .stream()
            .filter(w -> wallCode.equalsIgnoreCase(w.code()))
            .findFirst()
            .orElse(null);
    }

    private double[] cabinetBounds(WallData wall, int xMm, int widthMm, int depthMm) {
        double rad = Math.toRadians(wall.angleDeg());
        double ux = Math.cos(rad),
            uy = Math.sin(rad);
        double px = -uy,
            py = ux; // left perpendicular
        double sx = wall.startX() + xMm * ux;
        double sy = wall.startY() + xMm * uy;
        double ex = sx + widthMm * ux;
        double ey = sy + widthMm * uy;
        double minX = Math.min(Math.min(sx, ex), Math.min(sx + depthMm * px, ex + depthMm * px));
        double minY = Math.min(Math.min(sy, ey), Math.min(sy + depthMm * py, ey + depthMm * py));
        double maxX = Math.max(Math.max(sx, ex), Math.max(sx + depthMm * px, ex + depthMm * px));
        double maxY = Math.max(Math.max(sy, ey), Math.max(sy + depthMm * py, ey + depthMm * py));
        return new double[] { minX, minY, maxX, maxY };
    }

    private void drawCabinet(
        Graphics2D g,
        WallData wall,
        CabinetPlanItemDTO cabinet,
        double minX,
        double minY,
        double scale,
        boolean showLabels
    ) {
        double rad = Math.toRadians(wall.angleDeg());
        double ux = Math.cos(rad),
            uy = Math.sin(rad);
        double px = -uy,
            py = ux;

        double sx = wall.startX() + cabinet.getxMm() * ux;
        double sy = wall.startY() + cabinet.getxMm() * uy;
        double depth = cabinet.getDepthMm();

        int[] xp = {
            toCanvasX(sx, minX, scale),
            toCanvasX(sx + cabinet.getWidthMm() * ux, minX, scale),
            toCanvasX(sx + cabinet.getWidthMm() * ux + depth * px, minX, scale),
            toCanvasX(sx + depth * px, minX, scale),
        };
        int[] yp = {
            toCanvasY(sy, minY, scale),
            toCanvasY(sy + cabinet.getWidthMm() * uy, minY, scale),
            toCanvasY(sy + cabinet.getWidthMm() * uy + depth * py, minY, scale),
            toCanvasY(sy + depth * py, minY, scale),
        };

        boolean isUpper = isOverhead(cabinet);
        Color fill = categoryColor(cabinet.getCategory());
        if (isUpper) {
            // Upper cabinets: semi-transparent fill + dashed border (architectural convention for overhead elements)
            g.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 90));
            g.fillPolygon(xp, yp, 4);
            g.setColor(fill.darker());
            float[] dash = { 6f, 4f };
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        } else {
            g.setColor(fill);
            g.fillPolygon(xp, yp, 4);
            g.setColor(fill.darker());
            g.setStroke(new BasicStroke(1.5f));
        }
        g.drawPolygon(xp, yp, 4);

        if (showLabels && cabinet.getCabinetCode() != null) {
            int cx = (xp[0] + xp[1] + xp[2] + xp[3]) / 4;
            int cy = (yp[0] + yp[1] + yp[2] + yp[3]) / 4;
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.setColor(Color.BLACK);
            FontMetrics fm = g.getFontMetrics();
            String label = cabinet.getCabinetCode();
            g.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2);
        }
    }

    private void drawZone(Graphics2D g, WallData wall, LayoutZoneDTO zone, double minX, double minY, double scale, boolean showLabels) {
        double rad = Math.toRadians(wall.angleDeg());
        double ux = Math.cos(rad),
            uy = Math.sin(rad);
        double px = -uy,
            py = ux;
        double depth = zone.getDepthMm() != null ? zone.getDepthMm() : 600;
        double sx = wall.startX() + zone.getxMm() * ux;
        double sy = wall.startY() + zone.getxMm() * uy;

        int[] xp = {
            toCanvasX(sx, minX, scale),
            toCanvasX(sx + zone.getWidthMm() * ux, minX, scale),
            toCanvasX(sx + zone.getWidthMm() * ux + depth * px, minX, scale),
            toCanvasX(sx + depth * px, minX, scale),
        };
        int[] yp = {
            toCanvasY(sy, minY, scale),
            toCanvasY(sy + zone.getWidthMm() * uy, minY, scale),
            toCanvasY(sy + zone.getWidthMm() * uy + depth * py, minY, scale),
            toCanvasY(sy + depth * py, minY, scale),
        };

        Color zoneColor = zoneColor(zone.getZoneType());
        g.setColor(new Color(zoneColor.getRed(), zoneColor.getGreen(), zoneColor.getBlue(), 60));
        g.fillPolygon(xp, yp, 4);
        g.setColor(zoneColor.darker());
        g.setStroke(new BasicStroke(1f));
        g.drawPolygon(xp, yp, 4);

        if (showLabels) {
            int cx = (xp[0] + xp[1] + xp[2] + xp[3]) / 4;
            int cy = (yp[0] + yp[1] + yp[2] + yp[3]) / 4;
            g.setFont(new Font("SansSerif", Font.ITALIC, 9));
            g.setColor(zoneColor.darker());
            FontMetrics fm = g.getFontMetrics();
            String label = zone.getZoneCode();
            g.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2);
        }
    }

    private void drawObstacle(
        Graphics2D g,
        WallData wall,
        LayoutObstacleDTO obs,
        double minX,
        double minY,
        double scale,
        boolean showLabels
    ) {
        double rad = Math.toRadians(wall.angleDeg());
        double ux = Math.cos(rad),
            uy = Math.sin(rad);
        double depth = obs.getDepthMm() != null ? obs.getDepthMm() : 100;
        double sx = wall.startX() + obs.getxMm() * ux;
        double sy = wall.startY() + obs.getxMm() * uy;

        int x1 = toCanvasX(sx, minX, scale);
        int y1 = toCanvasY(sy, minY, scale);
        int x2 = toCanvasX(sx + obs.getWidthMm() * ux, minX, scale);
        int y2 = toCanvasY(sy + obs.getWidthMm() * uy, minY, scale);

        int cx = (x1 + x2) / 2;
        int cy = (y1 + y2) / 2;
        int halfW = (int) ((obs.getWidthMm() * scale) / 2);
        int halfD = (int) ((depth * scale) / 2);
        halfW = Math.max(halfW, 4);
        halfD = Math.max(halfD, 4);

        g.setColor(new Color(220, 80, 80, 100));
        g.fillRect(cx - halfW, cy - halfD, halfW * 2, halfD * 2);
        g.setColor(new Color(180, 40, 40));
        g.setStroke(new BasicStroke(OBSTACLE_STROKE));
        g.drawRect(cx - halfW, cy - halfD, halfW * 2, halfD * 2);

        if (showLabels && obs.getLabel() != null && !obs.getLabel().isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 8));
            g.setColor(new Color(120, 0, 0));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(obs.getLabel(), cx - fm.stringWidth(obs.getLabel()) / 2, cy + fm.getAscent() / 2);
        }
    }

    private void drawGrid(Graphics2D g, double minX, double minY, double roomW, double roomH, double scale, int padding) {
        g.setColor(new Color(220, 220, 220));
        g.setStroke(new BasicStroke(0.5f));
        double gridStep = 500;
        double x = Math.floor(minX / gridStep) * gridStep;
        while (x <= minX + roomW + gridStep) {
            int cx = toCanvasX(x, minX, scale);
            g.drawLine(cx, padding, cx, FLOOR_PLAN_H - padding);
            x += gridStep;
        }
        double y = Math.floor(minY / gridStep) * gridStep;
        while (y <= minY + roomH + gridStep) {
            int cy = toCanvasY(y, minY, scale);
            g.drawLine(padding, cy, CANVAS_W - padding - LEGEND_W, cy);
            y += gridStep;
        }
    }

    private void drawElevations(
        Graphics2D g,
        List<WallData> walls,
        List<CabinetPlanItemDTO> cabinets,
        MeasuredLayoutRequestDTO layout,
        boolean showLabels
    ) {
        List<WallData> wallsWithCabinets = walls
            .stream()
            .filter(w -> cabinets.stream().anyMatch(c -> w.code().equalsIgnoreCase(c.getWallCode())))
            .toList();
        if (wallsWithCabinets.isEmpty()) return;

        int elevStart = FLOOR_PLAN_H + 10;

        // Section separator and title
        g.setColor(new Color(180, 180, 180));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(PADDING / 2, FLOOR_PLAN_H + 2, CANVAS_W - PADDING / 2, FLOOR_PLAN_H + 2);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(30, 30, 30));
        g.drawString("Alzadas por pared (vista frontal)", PADDING, elevStart + 13);

        int panelTopY = elevStart + 22;
        int panelH = CANVAS_H - panelTopY - 8;
        int totalDrawW = CANVAS_W - PADDING * 2;
        int gapBetween = 14;
        int n = wallsWithCabinets.size();
        int panelW = (totalDrawW - gapBetween * (n - 1)) / n;

        int roomHeightMm = (layout.getRoomHeightMm() != null && layout.getRoomHeightMm() > 0) ? layout.getRoomHeightMm() : 2400;

        for (int i = 0; i < n; i++) {
            WallData wall = wallsWithCabinets.get(i);
            int panelX = PADDING + i * (panelW + gapBetween);
            drawWallElevation(g, wall, cabinets, panelX, panelTopY, panelW, panelH, roomHeightMm, showLabels);
        }
    }

    private void drawWallElevation(
        Graphics2D g,
        WallData wall,
        List<CabinetPlanItemDTO> allCabinets,
        int panelX,
        int panelY,
        int panelW,
        int panelH,
        int roomHeightMm,
        boolean showLabels
    ) {
        List<CabinetPlanItemDTO> wallCabinets = allCabinets
            .stream()
            .filter(c -> wall.code().equalsIgnoreCase(c.getWallCode()))
            .toList();
        if (wallCabinets.isEmpty()) return;

        double wallLengthMm = Math.sqrt(Math.pow(wall.endX() - wall.startX(), 2) + Math.pow(wall.endY() - wall.startY(), 2));

        // Panel background and border
        g.setColor(new Color(248, 248, 246));
        g.fillRect(panelX, panelY, panelW, panelH);
        g.setColor(new Color(190, 190, 190));
        g.setStroke(new BasicStroke(1f));
        g.drawRect(panelX, panelY, panelW, panelH);

        // Panel title
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.setColor(new Color(40, 40, 40));
        g.drawString("Pared " + wall.code() + "  (" + (int) wallLengthMm + " mm)", panelX + 4, panelY + 12);

        // Drawing area within panel
        int leftMargin = 28; // space for height axis labels
        int bottomMargin = showLabels ? 18 : 6;
        int topPad = 16;
        int rightPad = 4;
        int drawX = panelX + leftMargin;
        int drawY = panelY + topPad;
        int drawW = panelW - leftMargin - rightPad;
        int drawH = panelH - topPad - bottomMargin;
        if (drawW <= 0 || drawH <= 0) return;

        double xScale = drawW / wallLengthMm;
        double zScale = (double) drawH / roomHeightMm;
        double scale = Math.min(xScale, zScale);

        int floorY = drawY + drawH;
        int wallEndX = drawX + (int) (wallLengthMm * scale);

        // Light horizontal grid every 500 mm
        g.setColor(new Color(225, 225, 220));
        g.setStroke(new BasicStroke(0.5f));
        for (int hMm = 0; hMm <= roomHeightMm; hMm += 500) {
            int hy = floorY - (int) (hMm * scale);
            if (hy < drawY) break;
            g.drawLine(drawX, hy, wallEndX, hy);
        }

        // Counter-height reference at 900 mm (dashed)
        int counterY = floorY - (int) (900 * scale);
        if (counterY > drawY) {
            float[] dash = { 6f, 3f };
            g.setColor(new Color(160, 150, 100, 180));
            g.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
            g.drawLine(drawX, counterY, wallEndX, counterY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 7));
            g.setColor(new Color(140, 130, 80));
            g.drawString("900", panelX + 1, counterY + 3);
        }

        // Ceiling reference (long-dash)
        int ceilingY = floorY - (int) (roomHeightMm * scale);
        if (ceilingY >= drawY) {
            float[] dashLong = { 10f, 5f };
            g.setColor(new Color(130, 130, 180, 160));
            g.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashLong, 0f));
            g.drawLine(drawX, ceilingY, wallEndX, ceilingY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 7));
            g.setColor(new Color(100, 100, 160));
            g.drawString(roomHeightMm + "", panelX + 1, ceilingY + 3);
        }

        // Cabinets
        g.setStroke(new BasicStroke(1f));
        for (CabinetPlanItemDTO cabinet : wallCabinets) {
            if (cabinet.getxMm() == null || cabinet.getWidthMm() == null || cabinet.getHeightMm() == null) continue;
            int zStart = cabinet.getzMm() != null ? cabinet.getzMm() : 0;
            int cx1 = drawX + (int) (cabinet.getxMm() * scale);
            int cz1 = floorY - (int) ((zStart + cabinet.getHeightMm()) * scale);
            int cw = Math.max((int) (cabinet.getWidthMm() * scale), 2);
            int ch = Math.max((int) (cabinet.getHeightMm() * scale), 2);
            cz1 = Math.max(cz1, drawY);

            Color fill = categoryColor(cabinet.getCategory());
            g.setColor(fill);
            g.fillRect(cx1, cz1, cw, ch);
            g.setColor(fill.darker());
            g.drawRect(cx1, cz1, cw, ch);

            if (showLabels && cabinet.getCabinetCode() != null) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g.setColor(Color.BLACK);
                FontMetrics fm = g.getFontMetrics();
                String label = cabinet.getCabinetCode();
                if (fm.stringWidth(label) <= cw - 2 && ch >= fm.getHeight()) {
                    g.drawString(label, cx1 + cw / 2 - fm.stringWidth(label) / 2, cz1 + ch / 2 + fm.getAscent() / 2);
                }
            }
        }

        // Floor line and wall boundary
        g.setColor(new Color(80, 80, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(drawX, floorY, wallEndX, floorY);
        g.drawLine(drawX, drawY, drawX, floorY);
        g.drawLine(wallEndX, drawY, wallEndX, floorY);

        // X-axis ticks
        if (showLabels) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 7));
            g.setColor(new Color(100, 100, 100));
            g.setStroke(new BasicStroke(0.5f));
            for (double xMm = 0; xMm <= wallLengthMm + 1; xMm += 500) {
                int tx = drawX + (int) (xMm * scale);
                FontMetrics fm = g.getFontMetrics();
                g.drawLine(tx, floorY, tx, floorY + 4);
                String tickLabel = (int) xMm + "";
                g.drawString(tickLabel, tx - fm.stringWidth(tickLabel) / 2, floorY + 13);
            }
        }

        // Height labels on left margin
        if (showLabels) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 7));
            g.setColor(new Color(100, 100, 100));
            g.setStroke(new BasicStroke(0.5f));
            for (int hMm = 0; hMm <= roomHeightMm; hMm += 500) {
                int hy = floorY - (int) (hMm * scale);
                if (hy < drawY) break;
                g.drawLine(drawX - 3, hy, drawX, hy);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(hMm + "", panelX + 1, hy + 3);
            }
        }
    }

    private void drawLegend(Graphics2D g, List<CabinetPlanItemDTO> cabinets, List<LayoutZoneDTO> zones) {
        int lx = CANVAS_W - LEGEND_W + 10;
        int ly = PADDING;
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(40, 40, 40));
        g.drawString("Muebles", lx, ly);
        ly += 18;

        java.util.Set<CabinetCategory> seen = new java.util.LinkedHashSet<>();
        for (CabinetPlanItemDTO c : cabinets) {
            if (c.getCategory() != null) seen.add(c.getCategory());
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (CabinetCategory cat : seen) {
            Color c = categoryColor(cat);
            boolean isUpper = cat == CabinetCategory.UPPER;
            g.setColor(isUpper ? new Color(c.getRed(), c.getGreen(), c.getBlue(), 90) : c);
            g.fillRect(lx, ly - 10, 14, 14);
            g.setColor(c.darker());
            if (isUpper) {
                float[] dash = { 4f, 3f };
                g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
            } else {
                g.setStroke(new BasicStroke(1f));
            }
            g.drawRect(lx, ly - 10, 14, 14);
            g.setStroke(new BasicStroke(1f));
            g.setColor(new Color(40, 40, 40));
            String label = cat.name().replace('_', ' ') + (isUpper ? " (aéreo)" : "");
            g.drawString(label, lx + 18, ly);
            ly += 18;
        }

        if (zones != null && !zones.isEmpty()) {
            ly += 10;
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.setColor(new Color(40, 40, 40));
            g.drawString("Zonas", lx, ly);
            ly += 18;
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            java.util.LinkedHashMap<String, Color> seenZones = new java.util.LinkedHashMap<>();
            for (LayoutZoneDTO zone : zones) {
                if (zone.getZoneType() != null) {
                    seenZones.putIfAbsent(zone.getZoneType(), zoneColor(zone.getZoneType()));
                }
            }
            g.setStroke(new BasicStroke(1f));
            for (java.util.Map.Entry<String, Color> entry : seenZones.entrySet()) {
                Color c = entry.getValue();
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
                g.fillRect(lx, ly - 10, 14, 14);
                g.setColor(c);
                g.drawRect(lx, ly - 10, 14, 14);
                g.setColor(new Color(40, 40, 40));
                g.drawString(entry.getKey(), lx + 18, ly);
                ly += 18;
            }
        }
    }

    private int toCanvasX(double roomX, double minX, double scale) {
        return PADDING + (int) ((roomX - minX) * scale);
    }

    private int toCanvasY(double roomY, double minY, double scale) {
        return FLOOR_PLAN_H - PADDING - (int) ((roomY - minY) * scale);
    }

    private Color categoryColor(CabinetCategory cat) {
        if (cat == null) return new Color(200, 200, 200);
        return switch (cat) {
            case LOWER -> new Color(139, 157, 195);
            case UPPER -> new Color(184, 197, 224);
            case CORNER -> new Color(155, 142, 160);
            case TALL -> new Color(122, 158, 126);
            case SINK -> new Color(118, 176, 192);
            case ISLAND -> new Color(192, 168, 130);
            case DRAWER_BASE -> new Color(160, 176, 208);
            case APPLIANCE -> new Color(208, 160, 160);
            default -> new Color(200, 200, 200);
        };
    }

    private Color zoneColor(String zoneType) {
        if (zoneType == null) return new Color(150, 200, 150);
        return switch (zoneType.toUpperCase()) {
            case "SINK" -> new Color(100, 160, 200);
            case "STOVE", "GAS", "COOKING" -> new Color(220, 130, 80);
            case "REFRIGERATOR", "FRIDGE" -> new Color(80, 180, 160);
            case "WORK", "PREP" -> new Color(200, 210, 150);
            default -> new Color(180, 200, 180);
        };
    }

    // ── Persistence helpers ──────────────────────────────────────────────────

    private DesignArtifact saveArtifact(
        DesignSession session,
        ArtifactType type,
        String fileName,
        String filePath,
        String mime,
        int sizeBytes
    ) {
        DesignArtifact artifact = new DesignArtifact()
            .session(session)
            .artifactType(type)
            .fileName(fileName)
            .filePath(filePath)
            .mimeType(mime)
            .fileSizeKb(Math.max(1L, sizeBytes / 1024))
            .createdAt(Instant.now());
        return designArtifactRepository.save(artifact);
    }

    private void writeOutputFile(String relativePath, byte[] bytes) {
        try {
            Path target = outputDir.resolve(relativePath).normalize();
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write prototype file", e);
        }
    }

    private Map<String, Object> buildMetadata(
        DesignSession session,
        MeasuredLayoutRequestDTO layout,
        CabinetPlanResponseDTO plan,
        Prototype3dRequestDTO req
    ) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("schemaVersion", "1.0");
        meta.put("designSessionId", session.getId());
        meta.put("sessionCode", session.getSessionCode());
        meta.put("prototypeMode", req.getPrototypeMode());
        meta.put("generatedAt", Instant.now().toString());
        meta.put("wallCount", layout.getWalls().size());
        meta.put("zoneCount", layout.getZones() != null ? layout.getZones().size() : 0);
        meta.put("obstacleCount", layout.getObstacles() != null ? layout.getObstacles().size() : 0);
        meta.put("cabinetCount", plan.getCabinetCount());
        meta.put("layout", layout.getLayout().name());
        meta.put("roomHeightMm", layout.getRoomHeightMm());
        return meta;
    }

    private Prototype3dJobDTO toJobDTO(
        GenerationJob job,
        DesignSession session,
        List<DesignArtifact> artifacts,
        List<String> warnings,
        String prototypeMode
    ) {
        Prototype3dJobDTO dto = new Prototype3dJobDTO();
        dto.setJobId(job.getId());
        dto.setSessionId(session.getId());
        dto.setSessionCode(session.getSessionCode());
        dto.setStatus(job.getStatus().name());
        dto.setPrototypeMode(prototypeMode);
        dto.setStartedAt(job.getStartedAt());
        dto.setFinishedAt(job.getFinishedAt());
        dto.setWarnings(warnings);
        List<Prototype3dArtifactItemDTO> artifactDTOs = new ArrayList<>();
        for (DesignArtifact a : artifacts) {
            Prototype3dArtifactItemDTO item = new Prototype3dArtifactItemDTO();
            item.setArtifactId(a.getId());
            item.setArtifactType(a.getArtifactType().name());
            item.setFileName(a.getFileName());
            item.setMimeType(a.getMimeType());
            artifactDTOs.add(item);
        }
        dto.setArtifacts(artifactDTOs);
        return dto;
    }
}
