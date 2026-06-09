package com.dekk.app.activelog.domain.repository;

import com.dekk.app.activelog.domain.model.ActiveLog;
import com.dekk.app.activelog.domain.model.SwipeType;
import java.util.List;

public interface ActiveLogRepository {
    ActiveLog save(ActiveLog activeLog);

    boolean existsByUserIdAndCardId(Long userId, Long cardId);

    List<CardSwipeProjection> findCardIdAndSwipeTypeByUserId(Long userId, List<SwipeType> swipeTypes);
}
