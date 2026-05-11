package com.kalitron.studio.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kalitron.studio.domain.enumeration.MessageRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ChatMessage.
 */
@Entity
@Table(name = "chat_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MessageRole role;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "token_count")
    private Integer tokenCount;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "spec", "messageses", "imageses", "artifactses", "jobses", "quoteses", "wallses", "obstacleses", "catalogStyle" },
        allowSetters = true
    )
    private DesignSession session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChatMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageRole getRole() {
        return this.role;
    }

    public ChatMessage role(MessageRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(MessageRole role) {
        this.role = role;
    }

    public String getContent() {
        return this.content;
    }

    public ChatMessage content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTokenCount() {
        return this.tokenCount;
    }

    public ChatMessage tokenCount(Integer tokenCount) {
        this.setTokenCount(tokenCount);
        return this;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ChatMessage createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public DesignSession getSession() {
        return this.session;
    }

    public void setSession(DesignSession designSession) {
        this.session = designSession;
    }

    public ChatMessage session(DesignSession designSession) {
        this.setSession(designSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((ChatMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatMessage{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", content='" + getContent() + "'" +
            ", tokenCount=" + getTokenCount() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
