package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.domain.enumeration.ImageType;
import com.kalitron.studio.repository.DesignImageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/design-sessions")
public class SketchImageResource {

    private final DesignImageRepository designImageRepository;
    private final Path outputDir;

    public SketchImageResource(DesignImageRepository designImageRepository, @Value("${app.output.dir:./outputs}") String outputDir) {
        this.designImageRepository = designImageRepository;
        this.outputDir = Path.of(outputDir);
    }

    @GetMapping("/{sessionId}/sketch-image")
    public ResponseEntity<byte[]> getSketchImage(@PathVariable Long sessionId) {
        var imageOpt = designImageRepository.findFirstBySessionIdAndImageTypeAndIsActiveTrueOrderByUploadedAtDesc(
            sessionId,
            ImageType.SKETCH
        );
        if (imageOpt.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var image = imageOpt.get();
        try {
            Path filePath = outputDir.resolve(image.getFilePath()).normalize();
            byte[] bytes = Files.readAllBytes(filePath);
            String mimeType = image.getMimeType() != null ? image.getMimeType() : "image/jpeg";
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(bytes);
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
    }
}
