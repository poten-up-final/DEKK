package com.dekk.app.deck.presentation.response;

import com.dekk.app.deck.application.dto.result.CustomDeckResult;
import com.dekk.app.deck.domain.model.enums.DeckType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커스텀덱 목록 응답")
public record CustomDeckResponse(
        @Schema(description = "덱 ID", example = "2") Long deckId,
        @Schema(description = "덱 이름", example = "여름 코디") String name,

        @Schema(description = "덱 타입 (CUSTOM/SHARED)", example = "CUSTOM")
        DeckType deckType,

        @Schema(description = "덱 내 카드 수", example = "5") Long cardCount,
        @Schema(description = "최신 썸네일 이미지 URL") String imageUrl) {
    public static CustomDeckResponse from(CustomDeckResult result) {
        return new CustomDeckResponse(
                result.deckId(), result.name(), result.deckType(), result.cardCount(), result.imageUrl());
    }
}
