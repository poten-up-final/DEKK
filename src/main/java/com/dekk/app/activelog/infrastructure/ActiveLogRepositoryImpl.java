package com.dekk.app.activelog.infrastructure;

import com.dekk.app.activelog.domain.model.ActiveLog;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.activelog.domain.repository.ActiveLogRepository;
import com.dekk.app.activelog.domain.repository.CardSwipeProjection;
import com.dekk.app.activelog.infrastructure.jpa.ActiveLogJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public List<CardSwipeProjection> findCardIdAndSwipeTypeByUserId(Long userId, List<SwipeType> swipeTypes) {
        return jpaRepository.findCardIdAndSwipeTypeByUserId(userId, swipeTypes);
    }
}
