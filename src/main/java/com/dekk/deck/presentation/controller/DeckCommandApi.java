package com.dekk.deck.presentation.controller;

import com.dekk.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Deck Command", description = "보관함 조작 API (Command)")
public interface DeckCommandApi {

    @Operation(summary = "기본 보관함 카드 저장", description = "유저의 기본 보관함에 특정 카드를 저장합니다.")
    ResponseEntity<ApiResponse<Void>> addCardToDefaultDeck(
        // TODO: 향후 SecurityContextHolder에서 추출하도록 변경 예정
        @Parameter(description = "사용자 ID", required = true) Long userId,
        @Parameter(description = "저장할 카드 ID", in = ParameterIn.PATH) @PathVariable Long cardId
    );
}
