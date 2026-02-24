package com.dekk.user.infrastructure.repository;

import com.dekk.user.domain.model.Profile;
import com.dekk.user.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfileJpaRepository jpaRepository;

    @Override
    public Profile save(Profile profile) {
        return jpaRepository.save(profile);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return jpaRepository.existsByNickname(nickname);
    }
}
