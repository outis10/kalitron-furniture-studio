package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SiteMeasurementStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * On-site laser measurement snapshot synced from KFS-APP (E12 #107, #112).
 * payload keeps the raw request body; the layout projection is done by #110.
 */
@Entity
@Table(name = "site_measurement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SiteMeasurement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "measurement_uuid", nullable = false, unique = true)
    private UUID measurementUuid;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", nullable = false)
    private ProjectType projectType;

    @NotNull
    @Column(name = "revision", nullable = false)
    private Integer revision;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SiteMeasurementStatus status;

    @NotNull
    @Column(name = "schema_version", nullable = false)
    private Integer schemaVersion;

    @NotNull
    @Size(max = 20)
    @Column(name = "catalog_version", length = 20, nullable = false)
    private String catalogVersion;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @NotNull
    @Size(max = 64)
    @Column(name = "payload_sha_256", length = 64, nullable = false)
    private String payloadSha256;

    @Column(name = "floor_out_of_level_mm")
    private Integer floorOutOfLevelMm;

    @Size(max = 200)
    @Column(name = "floor_out_of_level_note", length = 200)
    private String floorOutOfLevelNote;

    @Size(max = 64)
    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Size(max = 20)
    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Size(max = 40)
    @Column(name = "laser_model", length = 40)
    private String laserModel;

    @Column(name = "captured_at")
    private Instant capturedAt;

    @NotNull
    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "spec",
            "messageses",
            "imageses",
            "artifactses",
            "jobses",
            "quoteses",
            "wallses",
            "obstacleses",
            "catalogStyle",
            "assignedMeasurer",
        },
        allowSetters = true
    )
    private DesignSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    private User measuredBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SiteMeasurement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getMeasurementUuid() {
        return this.measurementUuid;
    }

    public SiteMeasurement measurementUuid(UUID measurementUuid) {
        this.setMeasurementUuid(measurementUuid);
        return this;
    }

    public void setMeasurementUuid(UUID measurementUuid) {
        this.measurementUuid = measurementUuid;
    }

    public ProjectType getProjectType() {
        return this.projectType;
    }

    public SiteMeasurement projectType(ProjectType projectType) {
        this.setProjectType(projectType);
        return this;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public Integer getRevision() {
        return this.revision;
    }

    public SiteMeasurement revision(Integer revision) {
        this.setRevision(revision);
        return this;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public SiteMeasurementStatus getStatus() {
        return this.status;
    }

    public SiteMeasurement status(SiteMeasurementStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SiteMeasurementStatus status) {
        this.status = status;
    }

    public Integer getSchemaVersion() {
        return this.schemaVersion;
    }

    public SiteMeasurement schemaVersion(Integer schemaVersion) {
        this.setSchemaVersion(schemaVersion);
        return this;
    }

    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getCatalogVersion() {
        return this.catalogVersion;
    }

    public SiteMeasurement catalogVersion(String catalogVersion) {
        this.setCatalogVersion(catalogVersion);
        return this;
    }

    public void setCatalogVersion(String catalogVersion) {
        this.catalogVersion = catalogVersion;
    }

    public String getPayload() {
        return this.payload;
    }

    public SiteMeasurement payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayloadSha256() {
        return this.payloadSha256;
    }

    public SiteMeasurement payloadSha256(String payloadSha256) {
        this.setPayloadSha256(payloadSha256);
        return this;
    }

    public void setPayloadSha256(String payloadSha256) {
        this.payloadSha256 = payloadSha256;
    }

    public Integer getFloorOutOfLevelMm() {
        return this.floorOutOfLevelMm;
    }

    public SiteMeasurement floorOutOfLevelMm(Integer floorOutOfLevelMm) {
        this.setFloorOutOfLevelMm(floorOutOfLevelMm);
        return this;
    }

    public void setFloorOutOfLevelMm(Integer floorOutOfLevelMm) {
        this.floorOutOfLevelMm = floorOutOfLevelMm;
    }

    public String getFloorOutOfLevelNote() {
        return this.floorOutOfLevelNote;
    }

    public SiteMeasurement floorOutOfLevelNote(String floorOutOfLevelNote) {
        this.setFloorOutOfLevelNote(floorOutOfLevelNote);
        return this;
    }

    public void setFloorOutOfLevelNote(String floorOutOfLevelNote) {
        this.floorOutOfLevelNote = floorOutOfLevelNote;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public SiteMeasurement deviceId(String deviceId) {
        this.setDeviceId(deviceId);
        return this;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public SiteMeasurement appVersion(String appVersion) {
        this.setAppVersion(appVersion);
        return this;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getLaserModel() {
        return this.laserModel;
    }

    public SiteMeasurement laserModel(String laserModel) {
        this.setLaserModel(laserModel);
        return this;
    }

    public void setLaserModel(String laserModel) {
        this.laserModel = laserModel;
    }

    public Instant getCapturedAt() {
        return this.capturedAt;
    }

    public SiteMeasurement capturedAt(Instant capturedAt) {
        this.setCapturedAt(capturedAt);
        return this;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Instant getReceivedAt() {
        return this.receivedAt;
    }

    public SiteMeasurement receivedAt(Instant receivedAt) {
        this.setReceivedAt(receivedAt);
        return this;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public SiteMeasurement confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public SiteMeasurement session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    public User getMeasuredBy() {
        return this.measuredBy;
    }

    public void setMeasuredBy(User user) {
        this.measuredBy = user;
    }

    public SiteMeasurement measuredBy(User user) {
        this.setMeasuredBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SiteMeasurement)) {
            return false;
        }
        return getId() != null && getId().equals(((SiteMeasurement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SiteMeasurement{" +
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
            "}";
    }
}
