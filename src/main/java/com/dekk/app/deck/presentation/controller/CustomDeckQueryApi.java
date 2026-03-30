package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.domain.exception.DeckErrorCode;
import com.dekk.app.deck.presentation.response.CustomDeckCardsResponse;
import com.dekk.app.deck.presentation.response.CustomDeckResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "커스텀덱 조회 API", description = "커스텀덱 목록 및 상태 조회 API")
public interface CustomDeckQueryApi {

    @Operation(summary = "내 커스텀덱(쉐어덱) 목록 조회", description = "사용자가 생성한 커스텀덱 목록과 최신 카드 썸네일(imageUrl) 1장을 최신순으로 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SD20008)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<List<CustomDeckResponse>>> getMyCustomDecks(
            @Parameter(hidden = true) CustomUserDetails userDetails);

    @Operation(
            summary = "커스텀덱(쉐어덱) 내부 카드 목록 조회",
            description =
                    "특정 커스텀덱(쉐어덱)에 담긴 카드 목록을 덱의 메타데이터(타입, 공유 토큰 등) 및 현재 유저의 권한(HOST/GUEST)과 함께 최신순으로 전체 조회합니다. (최대 50장)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SD20011)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<CustomDeckCardsResponse>> getCustomDeckCards(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "조회할 커스텀덱 ID", in = ParameterIn.PATH) Long customDeckId);
}
