package com.kalitron.studio.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalitron.studio.IntegrationTest;
import com.kalitron.studio.domain.DesignImage;
import com.kalitron.studio.domain.DesignSession;
import com.kalitron.studio.domain.SiteMeasurement;
import com.kalitron.studio.domain.enumeration.ImageType;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import com.kalitron.studio.domain.enumeration.SiteMeasurementStatus;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence rules of E12 #107: unique client UUIDs, at most one confirmed
 * measurement per session and project type, and the payload stored verbatim.
 */
@IntegrationTest
@Transactional
class SiteMeasurementRepositoryIT {

    @Autowired
    private SiteMeasurementRepository siteMeasurementRepository;

    @Autowired
    private DesignSessionRepository designSessionRepository;

    @Autowired
    private DesignImageRepository designImageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private DesignSession session;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        session = designSessionRepository.saveAndFlush(
            new DesignSession()
                .sessionCode("KD-IT-" + RandomStringUtils.insecure().nextAlphanumeric(8).toUpperCase())
                .projectType(ProjectType.BOTH)
                .status(SessionStatus.SPECS_READY)
                .clientName("Cliente IT")
                .createdAt(now)
                .updatedAt(now)
        );
    }

    private SiteMeasurement measurement(ProjectType projectType, SiteMeasurementStatus status) {
        return new SiteMeasurement()
            .measurementUuid(UUID.randomUUID())
            .projectType(projectType)
            .revision(1)
            .status(status)
            .schemaVersion(1)
            .catalogVersion("2026-09-30.1")
            .payload("{}")
            .payloadSha256("0".repeat(64))
            .receivedAt(Instant.now())
            .session(session);
    }

    @Test
    void measurementUuidIsUnique() {
        SiteMeasurement first = siteMeasurementRepository.saveAndFlush(measurement(ProjectType.KITCHEN, SiteMeasurementStatus.DRAFT));
        SiteMeasurement duplicate = measurement(ProjectType.KITCHEN, SiteMeasurementStatus.DRAFT).measurementUuid(
            first.getMeasurementUuid()
        );

        assertThatThrownBy(() -> siteMeasurementRepository.saveAndFlush(duplicate)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void onlyOneConfirmedMeasurementPerSessionAndProjectType() {
        siteMeasurementRepository.saveAndFlush(measurement(ProjectType.KITCHEN, SiteMeasurementStatus.CONFIRMED));
        // other project type, drafts and superseded rows are allowed alongside it
        siteMeasurementRepository.saveAndFlush(measurement(ProjectType.CLOSET, SiteMeasurementStatus.CONFIRMED));
        siteMeasurementRepository.saveAndFlush(measurement(ProjectType.KITCHEN, SiteMeasurementStatus.DRAFT));
        siteMeasurementRepository.saveAndFlush(measurement(ProjectType.KITCHEN, SiteMeasurementStatus.SUPERSEDED));

        SiteMeasurement secondConfirmed = measurement(ProjectType.KITCHEN, SiteMeasurementStatus.CONFIRMED);
        assertThatThrownBy(() -> siteMeasurementRepository.saveAndFlush(secondConfirmed)).isInstanceOf(
            DataIntegrityViolationException.class
        );
    }

    @Test
    void payloadIsStoredVerbatim() throws Exception {
        String vector = new ClassPathResource("site-measurement/validation-vectors/01-survey-only-valid-kitchen.json").getContentAsString(
            StandardCharsets.UTF_8
        );
        String payload = objectMapper.writeValueAsString(objectMapper.readTree(vector).get("input"));
        SiteMeasurement saved = siteMeasurementRepository.saveAndFlush(
            measurement(ProjectType.KITCHEN, SiteMeasurementStatus.DRAFT).payload(payload)
        );

        siteMeasurementRepository.flush();
        assertThat(siteMeasurementRepository.findById(saved.getId()).orElseThrow().getPayload()).isEqualTo(payload);
    }

    @Test
    void photoUuidIsUnique() {
        UUID photoUuid = UUID.randomUUID();
        designImageRepository.saveAndFlush(photo(photoUuid));

        assertThatThrownBy(() -> designImageRepository.saveAndFlush(photo(photoUuid))).isInstanceOf(DataIntegrityViolationException.class);
    }

    private DesignImage photo(UUID photoUuid) {
        return new DesignImage()
            .imageType(ImageType.SITE_PHOTO)
            .fileName(photoUuid + ".jpg")
            .filePath("site-photos/" + photoUuid + ".jpg")
            .isActive(true)
            .uploadedAt(Instant.now())
            .wallCode("A")
            .photoUuid(photoUuid)
            .sha256("a".repeat(64))
            .session(session);
    }
}
