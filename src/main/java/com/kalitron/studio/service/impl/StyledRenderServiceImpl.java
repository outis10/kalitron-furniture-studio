package com.kalitron.studio.service.impl;

import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.GenerationJob;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.domain.enumeration.GenerationJobStatus;
import com.kalitron.studio.domain.enumeration.GenerationJobType;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.repository.DesignSessionRepository;
import com.kalitron.studio.repository.GenerationJobRepository;
import com.kalitron.studio.service.FastApiGateway;
import com.kalitron.studio.service.StyledRenderService;
import com.kalitron.studio.service.dto.StyledRenderArtifactItemDTO;
import com.kalitron.studio.service.dto.StyledRenderJobDTO;
import com.kalitron.studio.service.dto.StyledRenderRequestDTO;
import com.kalitron.studio.service.dto.VisualConceptResponseDTO;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StyledRenderServiceImpl implements StyledRenderService {

    private final DesignSessionRepository designSessionRepository;
    private final DesignArtifactRepository designArtifactRepository;
    private final GenerationJobRepository generationJobRepository;
    private final FastApiGateway fastApiGateway;
    private final Path outputDir;

    public StyledRenderServiceImpl(
        DesignSessionRepository designSessionRepository,
        DesignArtifactRepository designArtifactRepository,
        GenerationJobRepository generationJobRepository,
        FastApiGateway fastApiGateway,
        @Value("${app.output.dir:./outputs}") String outputDir
    ) {
        this.designSessionRepository = designSessionRepository;
        this.designArtifactRepository = designArtifactRepository;
        this.generationJobRepository = generationJobRepository;
        this.fastApiGateway = fastApiGateway;
        this.outputDir = Path.of(outputDir);
    }

    @Override
    public StyledRenderJobDTO generateStyledRender(Long sessionId, StyledRenderRequestDTO request) {
        DesignSession session = designSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Design session not found"));

        DesignArtifact prototypeArtifact = designArtifactRepository
            .findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(sessionId, List.of(ArtifactType.PROTOTYPE_PREVIEW))
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No prototype preview found — generate the blockout prototype first"));

        GenerationJob job = new GenerationJob()
            .session(session)
            .jobType(GenerationJobType.AI_RENDER)
            .status(GenerationJobStatus.RUNNING)
            .createdAt(Instant.now())
            .startedAt(Instant.now());
        generationJobRepository.save(job);

        try {
            String prototypeBase64 = readArtifactAsBase64(prototypeArtifact);
            String designBrief = buildDesignBrief(request);

            FastApiGateway.GatewayGenerateRequest gatewayRequest = new FastApiGateway.GatewayGenerateRequest(
                session.getSessionCode(),
                prototypeBase64,
                request.getStyle(),
                null,
                request.getFinish(),
                session.getProjectType() != null ? session.getProjectType().name() : "KITCHEN",
                designBrief,
                session.getId(),
                session.getSessionCode()
            );

            VisualConceptResponseDTO concept = fastApiGateway.generateVisualConcept(gatewayRequest);

            byte[] imageBytes = downloadImage(concept.getImageUrl());
            String relativePath = "styled-render/" + session.getSessionCode() + "/styled-render.png";
            writeOutputFile(relativePath, imageBytes);

            DesignArtifact styledArtifact = new DesignArtifact()
                .session(session)
                .artifactType(ArtifactType.STYLED_RENDER)
                .fileName("styled-render.png")
                .filePath(relativePath)
                .mimeType("image/png")
                .fileSizeKb(Math.max(1L, imageBytes.length / 1024))
                .createdAt(Instant.now());
            designArtifactRepository.save(styledArtifact);

            job.setStatus(GenerationJobStatus.DONE);
            job.setFinishedAt(Instant.now());
            job.setArtifact(styledArtifact);
            generationJobRepository.save(job);

            List<String> warnings = new ArrayList<>();
            if ("txt2img".equals(concept.getPipeline())) {
                warnings.add("El render fue generado sin imagen base (txt2img). Revisa que el prototipo esté disponible.");
            }

            return toJobDTO(job, session, styledArtifact, concept.getPromptUsed(), concept.getPipeline(), warnings);
        } catch (Exception e) {
            job.setStatus(GenerationJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setFinishedAt(Instant.now());
            generationJobRepository.save(job);
            throw new IllegalStateException("Styled render generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StyledRenderJobDTO> getLatestJob(Long sessionId) {
        return generationJobRepository
            .findFirstBySessionIdAndJobTypeOrderByCreatedAtDesc(sessionId, GenerationJobType.AI_RENDER)
            .map(job -> {
                DesignArtifact artifact = job.getArtifact();
                if (artifact == null || artifact.getArtifactType() != ArtifactType.STYLED_RENDER) {
                    artifact = designArtifactRepository
                        .findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(sessionId, List.of(ArtifactType.STYLED_RENDER))
                        .stream()
                        .findFirst()
                        .orElse(null);
                }
                return toJobDTO(job, job.getSession(), artifact, null, null, List.of());
            });
    }

    private String readArtifactAsBase64(DesignArtifact artifact) {
        try {
            Path filePath = outputDir.resolve(artifact.getFilePath()).normalize();
            byte[] bytes = Files.readAllBytes(filePath);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read prototype artifact: " + artifact.getFilePath(), e);
        }
    }

    private byte[] downloadImage(String imageUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(imageUrl)).GET().build();
            HttpResponse<byte[]> response = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Image download failed with status " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Could not download styled render image from: " + imageUrl, e);
        }
    }

    private void writeOutputFile(String relativePath, byte[] bytes) {
        try {
            Path target = outputDir.resolve(relativePath).normalize();
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write styled render file", e);
        }
    }

    private String buildDesignBrief(StyledRenderRequestDTO request) {
        List<String> parts = new ArrayList<>();
        if (request.getCountertopMaterial() != null) parts.add("countertop: " + request.getCountertopMaterial());
        if (request.getBacksplashNotes() != null) parts.add("backsplash: " + request.getBacksplashNotes());
        if (request.getHandleStyle() != null) parts.add("handles: " + request.getHandleStyle());
        if (request.getWallColor() != null) parts.add("wall color: " + request.getWallColor());
        if (request.getNotes() != null) parts.add(request.getNotes());
        return String.join(", ", parts);
    }

    private StyledRenderJobDTO toJobDTO(
        GenerationJob job,
        DesignSession session,
        DesignArtifact artifact,
        String promptUsed,
        String pipeline,
        List<String> warnings
    ) {
        StyledRenderJobDTO dto = new StyledRenderJobDTO();
        dto.setJobId(job.getId());
        dto.setSessionId(session.getId());
        dto.setSessionCode(session.getSessionCode());
        dto.setStatus(job.getStatus().name());
        dto.setPromptUsed(promptUsed);
        dto.setPipeline(pipeline);
        dto.setStartedAt(job.getStartedAt());
        dto.setFinishedAt(job.getFinishedAt());
        dto.setWarnings(warnings != null ? warnings : List.of());

        List<StyledRenderArtifactItemDTO> artifactDTOs = new ArrayList<>();
        if (artifact != null) {
            StyledRenderArtifactItemDTO item = new StyledRenderArtifactItemDTO();
            item.setArtifactId(artifact.getId());
            item.setArtifactType(artifact.getArtifactType().name());
            item.setFileName(artifact.getFileName());
            item.setMimeType(artifact.getMimeType());
            artifactDTOs.add(item);
        }
        dto.setArtifacts(artifactDTOs);
        return dto;
    }
}
