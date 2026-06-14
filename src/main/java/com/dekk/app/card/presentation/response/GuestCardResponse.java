package com.dekk.app.card.presentation.response;

import com.dekk.app.card.application.dto.result.GuestCardResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "비회원 카드 응답")
public record GuestCardResponse(
        @Schema(description = "카드 ID", example = "1")
        Long cardId,

        @Schema(description = "카드 공개 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID publicId,

        @Schema(description = "카드 이미지 URL", example = "https://example.com/card.jpg")
        String cardImageUrl,

        @Schema(description = "키(cm)", example = "175") Integer height,

        @Schema(description = "몸무게(kg)", example = "65") Integer weight,

        @Schema(description = "태그 목록", example = "[\"캐주얼\", \"스트릿\"]")
        List<String> tags) {
    public static GuestCardResponse from(GuestCardResult result) {
        return new GuestCardResponse(
                result.cardId(), result.publicId(), result.cardImageUrl(), result.height(), result.weight(), result.tags());
    }
}
