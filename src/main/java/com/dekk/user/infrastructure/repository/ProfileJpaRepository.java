package com.dekk.user.infrastructure.repository;

import com.dekk.user.domain.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileJpaRepository extends JpaRepository<Profile, Long> {
    boolean existsByNickname(String nickname);
}
