package com.dekk.app.activelog.application;

import com.dekk.app.activelog.domain.model.SwipeType;
import com.dekk.app.activelog.domain.model.SwipedCards;
import com.dekk.app.activelog.domain.repository.ActiveLogRepository;
import com.dekk.app.activelog.domain.repository.CardSwipeProjection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActiveLogQueryService {

    private final ActiveLogRepository activeLogRepository;

    public SwipedCards getSwipedCards(Long userId) {
        Map<SwipeType, Set<Long>> grouped =
                activeLogRepository
                        .findCardIdAndSwipeTypeByUserId(userId, List.of(SwipeType.LIKE, SwipeType.DISLIKE))
                        .stream()
                        .collect(Collectors.groupingBy(
                                CardSwipeProjection::getSwipeType,
                                Collectors.mapping(CardSwipeProjection::getCardId, Collectors.toSet())));

        return new SwipedCards(
                grouped.getOrDefault(SwipeType.LIKE, Set.of()), grouped.getOrDefault(SwipeType.DISLIKE, Set.of()));
    }
}
