package com.dekk.activelog.presentation.controller;

import com.dekk.activelog.presentation.request.SwipeRequest;
import com.dekk.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "스와이프 액션 API", description = "카드에 대한 좋아요/싫어요 평가 기록 API")
public interface ActiveLogApi {

    @Operation(summary = "카드 스와이프 평가 저장", description = "특정 카드에 대한 사용자의 액션을 기록합니다. (비회원은 수집 안함)")
    ApiResponse<Void> swipeCard(
        @Parameter(description = "대상 카드 ID") Long cardId,
        @Parameter(description = "스와이프 요청 정보(LIKE/DISLIKE)") SwipeRequest request
    );
}
