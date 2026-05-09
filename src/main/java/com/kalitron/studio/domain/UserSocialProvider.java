package com.kalitron.studio.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A social login provider linked to a User.
 */
@Entity
@Table(name = "user_social_provider")
public class UserSocialProvider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSocialProviderSeqGen")
    @SequenceGenerator(name = "userSocialProviderSeqGen", sequenceName = "user_social_provider_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Size(max = 20)
    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    @NotNull
    @Size(max = 255)
    @Column(name = "provider_id", length = 255, nullable = false)
    private String providerId;

    @Size(max = 255)
    @Column(name = "email", length = 255)
    private String email;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSocialProvider)) return false;
        return id != null && id.equals(((UserSocialProvider) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserSocialProvider{id=" + id + ", provider='" + provider + "', email='" + email + "'}";
    }
}
