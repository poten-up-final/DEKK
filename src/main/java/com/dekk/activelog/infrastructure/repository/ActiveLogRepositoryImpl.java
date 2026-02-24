package com.dekk.activelog.infrastructure.repository;

import com.dekk.activelog.domain.model.ActiveLog;
import com.dekk.activelog.domain.repository.ActiveLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ActiveLogRepositoryImpl implements ActiveLogRepository {

    private final ActiveLogJpaRepository jpaRepository;

    @Override
    public ActiveLog save(ActiveLog activeLog) {
        return jpaRepository.save(activeLog);
    }

    @Override
    public boolean existsByUserIdAndCardId(Long userId, Long cardId) {
        return jpaRepository.existsByUserIdAndCardId(userId, cardId);
    }

    @Override
    public Optional<ActiveLog> findByUserIdAndCardId(Long userId, Long cardId) {
        return jpaRepository.findByUserIdAndCardId(userId, cardId);
    }

    @Override
    public void delete(ActiveLog activeLog) {
        jpaRepository.delete(activeLog);
    }
}
