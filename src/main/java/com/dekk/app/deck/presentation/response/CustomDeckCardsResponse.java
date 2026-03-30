package com.dekk.app.deck.presentation.response;

import com.dekk.app.deck.application.dto.result.CustomDeckCardsResult;
import com.dekk.app.deck.domain.model.enums.DeckRole;
import com.dekk.app.deck.domain.model.enums.DeckType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "커스텀덱(쉐어덱) 내부 카드 목록 응답")
public record CustomDeckCardsResponse(
        @Schema(description = "보관함 타입 (CUSTOM/SHARED)", example = "SHARED")
        DeckType deckType,

        @Schema(description = "현재 조회 중인 유저의 덱 권한 (HOST/GUEST)", example = "HOST")
        DeckRole role,

        @Schema(description = "초대 토큰 (쉐어덱일 경우만 존재)", example = "a1b2c3d4e5f64789b123c456d789e012")
        String token,

        @Schema(description = "토큰 만료 남은 시간(초)", example = "86400")
        Long expiredInSeconds,

        @Schema(description = "카드 목록") List<MyDeckCardResponse> cards) {

    public static CustomDeckCardsResponse from(CustomDeckCardsResult result) {
        return new CustomDeckCardsResponse(
                result.deckType(),
                result.role(),
                result.token(),
                result.expiredInSeconds(),
                result.cards().stream().map(MyDeckCardResponse::from).toList());
    }
}
