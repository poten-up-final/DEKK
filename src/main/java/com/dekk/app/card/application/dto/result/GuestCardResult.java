package com.dekk.app.card.application.dto.result;

import com.dekk.app.card.domain.model.Card;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record GuestCardResult(Long cardId, UUID publicId, String cardImageUrl, Integer height, Integer weight, List<String> tags) {
    public static GuestCardResult from(Card card) {
        return new GuestCardResult(
                card.getId(),
                card.getPublicId(),
                card.getCardImage().getImageUrl(),
                card.getHeight(),
                card.getWeight(),
                parseTags(card.getTags()));
    }

    private static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
