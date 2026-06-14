package com.dekk.app.activelog.infrastructure.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.dekk.app.activelog.domain.model.ActiveLog;
import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.activelog.domain.repository.CardSwipeProjection;
import com.dekk.app.activelog.infrastructure.jpa.ActiveLogJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ActiveLogJpaRepositoryTest {

    @Autowired
    private ActiveLogJpaRepository activeLogJpaRepository;

    @Test
    @DisplayName("LIKE/DISLIKE 조회 시 cardId와 swipeType 쌍이 함께 반환된다")
    void findCardIdAndSwipeTypeByUserId_returnsAllTypes() {
        Long userId = 1L;
        activeLogJpaRepository.save(ActiveLog.create(userId, 101L, SwipeType.LIKE));
        activeLogJpaRepository.save(ActiveLog.create(userId, 102L, SwipeType.DISLIKE));
        activeLogJpaRepository.save(ActiveLog.create(userId, 103L, SwipeType.LIKE));
        activeLogJpaRepository.save(ActiveLog.create(2L, 104L, SwipeType.LIKE));

        List<CardSwipeProjection> rows = activeLogJpaRepository.findCardIdAndSwipeTypeByUserId(
                userId, List.of(SwipeType.LIKE, SwipeType.DISLIKE));

        assertThat(rows).hasSize(3);
        Map<SwipeType, List<Long>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        CardSwipeProjection::getSwipeType,
                        Collectors.mapping(CardSwipeProjection::getCardId, Collectors.toList())));
        assertThat(grouped.get(SwipeType.LIKE)).containsExactlyInAnyOrder(101L, 103L);
        assertThat(grouped.get(SwipeType.DISLIKE)).containsExactlyInAnyOrder(102L);
        assertThat(rows.stream().map(CardSwipeProjection::getCardId).toList()).doesNotContain(104L);
    }

    @Test
    @DisplayName("LIKE 단일 타입만 요청하면 LIKE 카드 ID만 반환된다")
    void findCardIdAndSwipeTypeByUserId_filtersBySingleType() {
        Long userId = 1L;
        activeLogJpaRepository.save(ActiveLog.create(userId, 101L, SwipeType.LIKE));
        activeLogJpaRepository.save(ActiveLog.create(userId, 102L, SwipeType.DISLIKE));

        List<CardSwipeProjection> rows = activeLogJpaRepository.findCardIdAndSwipeTypeByUserId(
                userId, List.of(SwipeType.LIKE));

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getCardId()).isEqualTo(101L);
        assertThat(rows.getFirst().getSwipeType()).isEqualTo(SwipeType.LIKE);
    }
}
