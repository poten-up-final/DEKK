package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.domain.exception.DeckErrorCode;
import com.dekk.app.deck.presentation.response.MyDeckCardResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.response.PageResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "기본덱 조회 API", description = "기본덱 카드 목록 조회 API")
public interface DefaultDeckQueryApi {

    @Operation(summary = "기본덱 카드 목록 페이징 조회", description = "기본덱의 카드 목록을 페이징하여 조회합니다. (기본 정렬: 최신순)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SDK20002)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<PageResponse<MyDeckCardResponse>>> getMyDefaultDeckCards(
            @Parameter(hidden = true) CustomUserDetails userDetails, @ParameterObject Pageable pageable);
}
