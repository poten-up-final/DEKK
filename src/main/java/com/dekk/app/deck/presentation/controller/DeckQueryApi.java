package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.domain.exception.DeckErrorCode;
import com.dekk.app.deck.presentation.response.DeckResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "통합 덱 조회 API", description = "메인 홈 화면용 덱 목록 통합 조회 API")
public interface DeckQueryApi {

    @Operation(
            summary = "내 덱 통합 목록 조회",
            description = "사용자가 참여 중인 모든 덱(기본덱, 커스텀덱, 쉐어덱)을 통합 조회합니다. 기본덱이 최상단에 위치하며, 각 덱의 최신 카드 썸네일(최대 3장)을 포함합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SD20012: 보관함 통합 목록 조회 성공")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<List<DeckResponse>>> getDecks(@Parameter(hidden = true) CustomUserDetails userDetails);
}
