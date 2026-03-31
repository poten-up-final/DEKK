package com.dekk.app.user.domain.repository;

import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.model.enums.Provider;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<User> findById(Long id);

    Optional<User> findWithProfileById(Long id);
}
