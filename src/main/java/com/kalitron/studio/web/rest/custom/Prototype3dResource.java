package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.domain.enumeration.ArtifactType;
import com.kalitron.studio.repository.DesignArtifactRepository;
import com.kalitron.studio.service.Prototype3dService;
import com.kalitron.studio.service.dto.Prototype3dJobDTO;
import com.kalitron.studio.service.dto.Prototype3dRequestDTO;
import com.kalitron.studio.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/design-sessions")
public class Prototype3dResource {

    private static final String ENTITY_NAME = "prototype3d";

    private final Prototype3dService prototype3dService;
    private final DesignArtifactRepository designArtifactRepository;
    private final Path outputDir;

    public Prototype3dResource(
        Prototype3dService prototype3dService,
        DesignArtifactRepository designArtifactRepository,
        @Value("${app.output.dir:./outputs}") String outputDir
    ) {
        this.prototype3dService = prototype3dService;
        this.designArtifactRepository = designArtifactRepository;
        this.outputDir = Path.of(outputDir);
    }

    @PostMapping("/{sessionId}/prototype-3d")
    public ResponseEntity<Prototype3dJobDTO> generatePrototype(
        @PathVariable Long sessionId,
        @RequestBody(required = false) Prototype3dRequestDTO request
    ) {
        try {
            Prototype3dJobDTO job = prototype3dService.generatePrototype(
                sessionId,
                request != null ? request : new Prototype3dRequestDTO()
            );
            return ResponseEntity.accepted().body(job);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("not found"))) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
            }
            throw new BadRequestAlertException(msg, ENTITY_NAME, "invalidrequest");
        }
    }

    @GetMapping("/{sessionId}/prototype-3d/jobs/{jobId}")
    public ResponseEntity<Prototype3dJobDTO> getJobStatus(@PathVariable Long sessionId, @PathVariable Long jobId) {
        return prototype3dService.getJobStatus(sessionId, jobId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{sessionId}/prototype-3d/latest")
    public ResponseEntity<Prototype3dJobDTO> getLatestJob(@PathVariable Long sessionId) {
        return prototype3dService.getLatestJob(sessionId).map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{sessionId}/prototype-3d/preview")
    public ResponseEntity<byte[]> getPreview(@PathVariable Long sessionId) {
        var artifactOpt = designArtifactRepository
            .findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(sessionId, java.util.List.of(ArtifactType.PROTOTYPE_PREVIEW))
            .stream()
            .findFirst();
        if (artifactOpt.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var artifact = artifactOpt.get();
        try {
            Path filePath = outputDir.resolve(artifact.getFilePath()).normalize();
            byte[] bytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(bytes);
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
    }
}
