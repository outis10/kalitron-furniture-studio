package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.ProjectType;
import com.kalitron.studio.domain.enumeration.SessionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DesignSession.
 */
@Entity
@Table(name = "design_session")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DesignSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "session_code", length = 20, nullable = false, unique = true)
    private String sessionCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", nullable = false)
    private ProjectType projectType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status;

    @NotNull
    @Size(max = 120)
    @Column(name = "client_name", length = 120, nullable = false)
    private String clientName;

    @Size(max = 120)
    @Column(name = "client_email", length = 120)
    private String clientEmail;

    @Size(max = 20)
    @Column(name = "client_phone", length = 20)
    private String clientPhone;

    @Size(max = 80)
    @Column(name = "selected_style", length = 80)
    private String selectedStyle;

    @Size(max = 2000)
    @Column(name = "notes", length = 2000)
    private String notes;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @JsonIgnoreProperties(value = { "cabinetses", "primaryMaterial", "session" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private KitchenSpec spec;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private Set<ChatMessage> messageses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private Set<DesignImage> imageses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private Set<DesignArtifact> artifactses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "artifact", "session" }, allowSetters = true)
    private Set<GenerationJob> jobses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "itemses", "renderImage", "pdfArtifact", "session" }, allowSetters = true)
    private Set<Quote> quoteses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private Set<RoomWall> wallses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private Set<RoomObstacle> obstacleses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private CatalogStyle catalogStyle;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DesignSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionCode() {
        return this.sessionCode;
    }

    public DesignSession sessionCode(String sessionCode) {
        this.setSessionCode(sessionCode);
        return this;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public ProjectType getProjectType() {
        return this.projectType;
    }

    public DesignSession projectType(ProjectType projectType) {
        this.setProjectType(projectType);
        return this;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public SessionStatus getStatus() {
        return this.status;
    }

    public DesignSession status(SessionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getClientName() {
        return this.clientName;
    }

    public DesignSession clientName(String clientName) {
        this.setClientName(clientName);
        return this;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return this.clientEmail;
    }

    public DesignSession clientEmail(String clientEmail) {
        this.setClientEmail(clientEmail);
        return this;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return this.clientPhone;
    }

    public DesignSession clientPhone(String clientPhone) {
        this.setClientPhone(clientPhone);
        return this;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getSelectedStyle() {
        return this.selectedStyle;
    }

    public DesignSession selectedStyle(String selectedStyle) {
        this.setSelectedStyle(selectedStyle);
        return this;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    public String getNotes() {
        return this.notes;
    }

    public DesignSession notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public DesignSession createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public DesignSession updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public KitchenSpec getSpec() {
        return this.spec;
    }

    public void setSpec(KitchenSpec kitchenSpec) {
        this.spec = kitchenSpec;
    }

    public DesignSession spec(KitchenSpec kitchenSpec) {
        this.setSpec(kitchenSpec);
        return this;
    }

    public Set<ChatMessage> getMessageses() {
        return this.messageses;
    }

    public void setMessageses(Set<ChatMessage> chatMessages) {
        if (this.messageses != null) {
            this.messageses.forEach(i -> i.setSession(null));
        }
        if (chatMessages != null) {
            chatMessages.forEach(i -> i.setSession(this));
        }
        this.messageses = chatMessages;
    }

    public DesignSession messageses(Set<ChatMessage> chatMessages) {
        this.setMessageses(chatMessages);
        return this;
    }

    public DesignSession addMessages(ChatMessage chatMessage) {
        this.messageses.add(chatMessage);
        chatMessage.setSession(this);
        return this;
    }

    public DesignSession removeMessages(ChatMessage chatMessage) {
        this.messageses.remove(chatMessage);
        chatMessage.setSession(null);
        return this;
    }

    public Set<DesignImage> getImageses() {
        return this.imageses;
    }

    public void setImageses(Set<DesignImage> designImages) {
        if (this.imageses != null) {
            this.imageses.forEach(i -> i.setSession(null));
        }
        if (designImages != null) {
            designImages.forEach(i -> i.setSession(this));
        }
        this.imageses = designImages;
    }

    public DesignSession imageses(Set<DesignImage> designImages) {
        this.setImageses(designImages);
        return this;
    }

    public DesignSession addImages(DesignImage designImage) {
        this.imageses.add(designImage);
        designImage.setSession(this);
        return this;
    }

    public DesignSession removeImages(DesignImage designImage) {
        this.imageses.remove(designImage);
        designImage.setSession(null);
        return this;
    }

    public Set<DesignArtifact> getArtifactses() {
        return this.artifactses;
    }

    public void setArtifactses(Set<DesignArtifact> designArtifacts) {
        if (this.artifactses != null) {
            this.artifactses.forEach(i -> i.setSession(null));
        }
        if (designArtifacts != null) {
            designArtifacts.forEach(i -> i.setSession(this));
        }
        this.artifactses = designArtifacts;
    }

    public DesignSession artifactses(Set<DesignArtifact> designArtifacts) {
        this.setArtifactses(designArtifacts);
        return this;
    }

    public DesignSession addArtifacts(DesignArtifact designArtifact) {
        this.artifactses.add(designArtifact);
        designArtifact.setSession(this);
        return this;
    }

    public DesignSession removeArtifacts(DesignArtifact designArtifact) {
        this.artifactses.remove(designArtifact);
        designArtifact.setSession(null);
        return this;
    }

    public Set<GenerationJob> getJobses() {
        return this.jobses;
    }

    public void setJobses(Set<GenerationJob> generationJobs) {
        if (this.jobses != null) {
            this.jobses.forEach(i -> i.setSession(null));
        }
        if (generationJobs != null) {
            generationJobs.forEach(i -> i.setSession(this));
        }
        this.jobses = generationJobs;
    }

    public DesignSession jobses(Set<GenerationJob> generationJobs) {
        this.setJobses(generationJobs);
        return this;
    }

    public DesignSession addJobs(GenerationJob generationJob) {
        this.jobses.add(generationJob);
        generationJob.setSession(this);
        return this;
    }

    public DesignSession removeJobs(GenerationJob generationJob) {
        this.jobses.remove(generationJob);
        generationJob.setSession(null);
        return this;
    }

    public Set<Quote> getQuoteses() {
        return this.quoteses;
    }

    public void setQuoteses(Set<Quote> quotes) {
        if (this.quoteses != null) {
            this.quoteses.forEach(i -> i.setSession(null));
        }
        if (quotes != null) {
            quotes.forEach(i -> i.setSession(this));
        }
        this.quoteses = quotes;
    }

    public DesignSession quoteses(Set<Quote> quotes) {
        this.setQuoteses(quotes);
        return this;
    }

    public DesignSession addQuotes(Quote quote) {
        this.quoteses.add(quote);
        quote.setSession(this);
        return this;
    }

    public DesignSession removeQuotes(Quote quote) {
        this.quoteses.remove(quote);
        quote.setSession(null);
        return this;
    }

    public Set<RoomWall> getWallses() {
        return this.wallses;
    }

    public void setWallses(Set<RoomWall> roomWalls) {
        if (this.wallses != null) {
            this.wallses.forEach(i -> i.setSession(null));
        }
        if (roomWalls != null) {
            roomWalls.forEach(i -> i.setSession(this));
        }
        this.wallses = roomWalls;
    }

    public DesignSession wallses(Set<RoomWall> roomWalls) {
        this.setWallses(roomWalls);
        return this;
    }

    public DesignSession addWalls(RoomWall roomWall) {
        this.wallses.add(roomWall);
        roomWall.setSession(this);
        return this;
    }

    public DesignSession removeWalls(RoomWall roomWall) {
        this.wallses.remove(roomWall);
        roomWall.setSession(null);
        return this;
    }

    public Set<RoomObstacle> getObstacleses() {
        return this.obstacleses;
    }

    public void setObstacleses(Set<RoomObstacle> roomObstacles) {
        if (this.obstacleses != null) {
            this.obstacleses.forEach(i -> i.setSession(null));
        }
        if (roomObstacles != null) {
            roomObstacles.forEach(i -> i.setSession(this));
        }
        this.obstacleses = roomObstacles;
    }

    public DesignSession obstacleses(Set<RoomObstacle> roomObstacles) {
        this.setObstacleses(roomObstacles);
        return this;
    }

    public DesignSession addObstacles(RoomObstacle roomObstacle) {
        this.obstacleses.add(roomObstacle);
        roomObstacle.setSession(this);
        return this;
    }

    public DesignSession removeObstacles(RoomObstacle roomObstacle) {
        this.obstacleses.remove(roomObstacle);
        roomObstacle.setSession(null);
        return this;
    }

    public CatalogStyle getCatalogStyle() {
        return this.catalogStyle;
    }

    public void setCatalogStyle(CatalogStyle catalogStyle) {
        this.catalogStyle = catalogStyle;
    }

    public DesignSession catalogStyle(CatalogStyle catalogStyle) {
        this.setCatalogStyle(catalogStyle);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DesignSession)) {
            return false;
        }
        return getId() != null && getId().equals(((DesignSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DesignSession{" +
            "id=" + getId() +
            ", sessionCode='" + getSessionCode() + "'" +
            ", projectType='" + getProjectType() + "'" +
            ", status='" + getStatus() + "'" +
            ", clientName='" + getClientName() + "'" +
            ", clientEmail='" + getClientEmail() + "'" +
            ", clientPhone='" + getClientPhone() + "'" +
            ", selectedStyle='" + getSelectedStyle() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
