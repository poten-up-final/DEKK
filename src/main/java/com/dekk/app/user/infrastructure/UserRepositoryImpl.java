package com.dekk.app.user.infrastructure;

import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.model.enums.Provider;
import com.dekk.app.user.domain.repository.UserRepository;
import com.dekk.app.user.infrastructure.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
        return jpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findWithProfileById(Long id) {
        return jpaRepository.findWithProfileById(id);
    }
}
