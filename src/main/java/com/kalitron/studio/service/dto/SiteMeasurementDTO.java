package com.kalitron.studio.service.dto;

import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SiteMeasurementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.kalitron.studio.domain.SiteMeasurement} entity.
 */
@Schema(
    description = "On-site laser measurement snapshot synced from KFS-APP (E12 #107, #112).\npayload keeps the raw request body; the layout projection is done by #110."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SiteMeasurementDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID measurementUuid;

    @NotNull
    private ProjectType projectType;

    @NotNull
    private Integer revision;

    @NotNull
    private SiteMeasurementStatus status;

    @NotNull
    private Integer schemaVersion;

    @NotNull
    @Size(max = 20)
    private String catalogVersion;

    @Lob
    private String payload;

    @NotNull
    @Size(max = 64)
    private String payloadSha256;

    private Integer floorOutOfLevelMm;

    @Size(max = 200)
    private String floorOutOfLevelNote;

    @Size(max = 64)
    private String deviceId;

    @Size(max = 20)
    private String appVersion;

    @Size(max = 40)
    private String laserModel;

    private Instant capturedAt;

    @NotNull
    private Instant receivedAt;

    private Instant confirmedAt;

    @NotNull
    private DesignSessionDTO session;

    private UserDTO measuredBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getMeasurementUuid() {
        return measurementUuid;
    }

    public void setMeasurementUuid(UUID measurementUuid) {
        this.measurementUuid = measurementUuid;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public SiteMeasurementStatus getStatus() {
        return status;
    }

    public void setStatus(SiteMeasurementStatus status) {
        this.status = status;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getCatalogVersion() {
        return catalogVersion;
    }

    public void setCatalogVersion(String catalogVersion) {
        this.catalogVersion = catalogVersion;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayloadSha256() {
        return payloadSha256;
    }

    public void setPayloadSha256(String payloadSha256) {
        this.payloadSha256 = payloadSha256;
    }

    public Integer getFloorOutOfLevelMm() {
        return floorOutOfLevelMm;
    }

    public void setFloorOutOfLevelMm(Integer floorOutOfLevelMm) {
        this.floorOutOfLevelMm = floorOutOfLevelMm;
    }

    public String getFloorOutOfLevelNote() {
        return floorOutOfLevelNote;
    }

    public void setFloorOutOfLevelNote(String floorOutOfLevelNote) {
        this.floorOutOfLevelNote = floorOutOfLevelNote;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getLaserModel() {
        return laserModel;
    }

    public void setLaserModel(String laserModel) {
        this.laserModel = laserModel;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public DesignSessionDTO getSession() {
        return session;
    }

    public void setSession(DesignSessionDTO session) {
        this.session = session;
    }

    public UserDTO getMeasuredBy() {
        return measuredBy;
    }

    public void setMeasuredBy(UserDTO measuredBy) {
        this.measuredBy = measuredBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SiteMeasurementDTO)) {
            return false;
        }

        SiteMeasurementDTO siteMeasurementDTO = (SiteMeasurementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, siteMeasurementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SiteMeasurementDTO{" +
            "id=" + getId() +
            ", measurementUuid='" + getMeasurementUuid() + "'" +
            ", projectType='" + getProjectType() + "'" +
            ", revision=" + getRevision() +
            ", status='" + getStatus() + "'" +
            ", schemaVersion=" + getSchemaVersion() +
            ", catalogVersion='" + getCatalogVersion() + "'" +
            ", payload='" + getPayload() + "'" +
            ", payloadSha256='" + getPayloadSha256() + "'" +
            ", floorOutOfLevelMm=" + getFloorOutOfLevelMm() +
            ", floorOutOfLevelNote='" + getFloorOutOfLevelNote() + "'" +
            ", deviceId='" + getDeviceId() + "'" +
            ", appVersion='" + getAppVersion() + "'" +
            ", laserModel='" + getLaserModel() + "'" +
            ", capturedAt='" + getCapturedAt() + "'" +
            ", receivedAt='" + getReceivedAt() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", session=" + getSession() +
            ", measuredBy=" + getMeasuredBy() +
            "}";
    }
}
