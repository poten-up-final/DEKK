package com.dekk.app.deck.presentation.response;

import com.dekk.app.deck.application.dto.result.DeckResult;
import com.dekk.app.deck.domain.model.enums.DeckType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "통합 덱 목록 응답")
public record DeckResponse(
        @Schema(description = "덱 ID", example = "1") Long deckId,
        @Schema(description = "덱 이름", example = "나의 기본 덱") String name,

        @Schema(description = "덱 타입 (DEFAULT/CUSTOM/SHARED)", example = "DEFAULT")
        DeckType type,

        @Schema(description = "덱 내 카드 수", example = "15") Long cardCount,
        @Schema(description = "프리뷰 이미지 URL 목록 (최대 3개)") List<String> previewImageUrls) {
    public static DeckResponse from(DeckResult result) {
        return new DeckResponse(
                result.deckId(), result.name(), result.type(), result.cardCount(), result.previewImageUrls());
    }
}
