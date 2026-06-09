package com.dekk.app.activelog.infrastructure.jpa;

import com.dekk.app.activelog.domain.model.ActiveLog;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.activelog.domain.repository.CardSwipeProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActiveLogJpaRepository extends JpaRepository<ActiveLog, Long> {
    boolean existsByUserIdAndCardId(Long userId, Long cardId);

    @Query("""
            SELECT a.cardId AS cardId, a.swipeType AS swipeType
            FROM ActiveLog a
            WHERE a.userId = :userId AND a.swipeType
                        IN :swipeTypes
            """)
    List<CardSwipeProjection> findCardIdAndSwipeTypeByUserId(
            @Param("userId") Long userId, @Param("swipeTypes") List<SwipeType> swipeTypes);
}
