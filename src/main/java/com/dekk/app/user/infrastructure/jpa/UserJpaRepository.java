package com.dekk.app.user.infrastructure.jpa;

import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.model.enums.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findWithProfileById(Long id);
}
