package com.dekk.user.infrastructure.jpa;

import com.dekk.user.domain.model.User;
import com.dekk.user.domain.model.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
}
