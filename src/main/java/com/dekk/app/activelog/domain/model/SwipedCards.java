package com.dekk.app.activelog.domain.model;

import java.util.HashSet;
import java.util.Set;

public record SwipedCards(Set<Long> likedIds, Set<Long> dislikedIds) {

    public SwipedCards {
        likedIds = Set.copyOf(likedIds);
        dislikedIds = Set.copyOf(dislikedIds);
    }

    public static SwipedCards empty() {
        return new SwipedCards(Set.of(), Set.of());
    }

    public Set<Long> allSwipedIds() {
        if (likedIds.isEmpty()) return dislikedIds;
        if (dislikedIds.isEmpty()) return likedIds;
        Set<Long> all = new HashSet<>(likedIds);
        all.addAll(dislikedIds);
        return all;
    }

    public boolean isAlreadySwiped(Long cardId) {
        return likedIds.contains(cardId) || dislikedIds.contains(cardId);
    }
}
