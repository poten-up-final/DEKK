package com.dekk.activelog.infrastructure.jpa;

import com.dekk.activelog.domain.model.ActiveLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ActiveLogJpaRepository extends JpaRepository<ActiveLog, Long> {
    boolean existsByUserIdAndCardId(Long userId, Long cardId);
    Optional<ActiveLog> findByUserIdAndCardId(Long userId, Long cardId);
}
