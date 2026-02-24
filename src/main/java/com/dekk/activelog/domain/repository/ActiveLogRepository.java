package com.dekk.activelog.domain.repository;

import com.dekk.activelog.domain.model.ActiveLog;
import java.util.Optional;

public interface ActiveLogRepository {
    ActiveLog save(ActiveLog activeLog);
    boolean existsByUserIdAndCardId(Long userId, Long cardId);
    Optional<ActiveLog> findByUserIdAndCardId(Long userId, Long cardId);
    void delete(ActiveLog activeLog);
}
