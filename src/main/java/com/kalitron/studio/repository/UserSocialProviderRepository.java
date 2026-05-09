package com.kalitron.studio.repository;

import com.kalitron.studio.domain.UserSocialProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSocialProviderRepository extends JpaRepository<UserSocialProvider, Long> {
    Optional<UserSocialProvider> findByProviderAndProviderId(String provider, String providerId);
}
