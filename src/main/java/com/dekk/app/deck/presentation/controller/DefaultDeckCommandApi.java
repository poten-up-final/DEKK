package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.domain.exception.DeckErrorCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "기본덱 관리 API", description = "기본덱 및 기본덱 내 카드 관리 API")
public interface DefaultDeckCommandApi {

    @Operation(summary = "기본덱에서 특정 카드 저장 취소 (Soft Delete)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SD20004: 덱 내 카드 저장 취소 성공")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<Void>> removeCardFromDefaultDeck(
            @LoginUser Long userId, @Parameter(description = "삭제할 카드 ID", in = ParameterIn.PATH) Long cardId);
}
