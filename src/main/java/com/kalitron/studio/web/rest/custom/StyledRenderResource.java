package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.domain.DesignArtifact;
import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.service.StyledRenderService;
import com.kalitron.studio.service.dto.StyledRenderJobDTO;
import com.kalitron.studio.service.dto.StyledRenderRequestDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/design-sessions")
public class StyledRenderResource {

    private final StyledRenderService styledRenderService;
    private final DesignArtifactRepository designArtifactRepository;
    private final Path outputDir;

    public StyledRenderResource(
        StyledRenderService styledRenderService,
        DesignArtifactRepository designArtifactRepository,
        @Value("${app.output.dir:./outputs}") String outputDir
    ) {
        this.styledRenderService = styledRenderService;
        this.designArtifactRepository = designArtifactRepository;
        this.outputDir = Path.of(outputDir);
    }

    @PostMapping("/{sessionId}/styled-render")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StyledRenderJobDTO> generateStyledRender(
        @PathVariable Long sessionId,
        @Valid @RequestBody StyledRenderRequestDTO request
    ) {
        StyledRenderJobDTO job = styledRenderService.generateStyledRender(sessionId, request);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{sessionId}/styled-render/latest")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StyledRenderJobDTO> getLatestStyledRender(@PathVariable Long sessionId) {
        return styledRenderService.getLatestJob(sessionId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{sessionId}/styled-render/{artifactId}/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getStyledRenderImage(@PathVariable Long sessionId, @PathVariable Long artifactId) {
        DesignArtifact artifact = designArtifactRepository
            .findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(sessionId, List.of(ArtifactType.STYLED_RENDER))
            .stream()
            .filter(a -> a.getId().equals(artifactId))
            .findFirst()
            .orElse(null);

        if (artifact == null || artifact.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = outputDir.resolve(artifact.getFilePath()).normalize();
            byte[] bytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"styled-render.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
