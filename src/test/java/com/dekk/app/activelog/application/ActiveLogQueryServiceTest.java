package com.dekk.app.activelog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.activelog.domain.model.SwipedCards;
import com.dekk.app.activelog.domain.repository.ActiveLogRepository;
import com.dekk.app.activelog.domain.repository.CardSwipeProjection;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActiveLogQueryServiceTest {

    @Mock
    private ActiveLogRepository activeLogRepository;

    @InjectMocks
    private ActiveLogQueryService activeLogQueryService;

    @Test
    @DisplayName("1회 조회 후 SwipeType별로 분리된 SwipedCards를 반환한다")
    void getSwipedCards_separatesBySwipeType() {
        Long userId = 1L;
        CardSwipeProjection p1 = projection(10L, SwipeType.LIKE);
        CardSwipeProjection p2 = projection(20L, SwipeType.LIKE);
        CardSwipeProjection p3 = projection(30L, SwipeType.DISLIKE);
        given(activeLogRepository.findCardIdAndSwipeTypeByUserId(eq(userId), anyList()))
            .willReturn(List.of(p1, p2, p3));

        SwipedCards result = activeLogQueryService.getSwipedCards(userId);

        assertThat(result.likedIds()).containsExactlyInAnyOrder(10L, 20L);
        assertThat(result.dislikedIds()).containsExactlyInAnyOrder(30L);
        assertThat(result.allSwipedIds()).containsExactlyInAnyOrder(10L, 20L, 30L);
        verify(activeLogRepository).findCardIdAndSwipeTypeByUserId(eq(userId), anyList());
    }

    @Test
    @DisplayName("스와이프 이력이 없으면 빈 SwipedCards를 반환한다")
    void getSwipedCards_returnsEmpty_whenNoHistory() {
        Long userId = 1L;
        given(activeLogRepository.findCardIdAndSwipeTypeByUserId(eq(userId), anyList()))
            .willReturn(List.of());

        SwipedCards result = activeLogQueryService.getSwipedCards(userId);

        assertThat(result.likedIds()).isEmpty();
        assertThat(result.dislikedIds()).isEmpty();
        assertThat(result.allSwipedIds()).isEmpty();
    }

    private CardSwipeProjection projection(Long cardId, SwipeType swipeType) {
        CardSwipeProjection p = mock(CardSwipeProjection.class);
        given(p.getCardId()).willReturn(cardId);
        given(p.getSwipeType()).willReturn(swipeType);
        return p;
    }
}
