package com.dekk.user.infrastructure.repository;

import com.dekk.user.domain.model.User;
import com.dekk.user.domain.model.enums.Provider;
import com.dekk.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
        return jpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }
}
