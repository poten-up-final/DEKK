package com.dekk.app.card.recommend.presentation.controller;

import com.dekk.app.card.recommend.presentation.dto.response.RecommendCardResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.response.SliceResponse;
import com.dekk.global.security.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "추천 카드 조회 API", description = "사용자 맞춤형 추천 카드 조회 API")
public interface RecommendQueryApi {

    @Operation(summary = "추천 카드 조회", description = "유저 체형 & 카테고리 선호도 기반 추천 카드(70%) + 최신순 카드(30%)를 반환한다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "추천 카드 조회 성공",
                        content = @Content(schema = @Schema(implementation = RecommendCardResponse.class)))
            })
    ResponseEntity<ApiResponse<SliceResponse<RecommendCardResponse>>> getRecommendCards(
            @LoginUser Long userId,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 카드 수 (최대 50)", example = "10") @RequestParam(defaultValue = "10") @Max(50)
                    int size,
            @Parameter(
                            description = "SEO 시작 카드 공개 ID (선택). 해당 카드가 첫 번째로 노출됨",
                            example = "550e8400-e29b-41d4-a716-446655440000")
                    @RequestParam(required = false)
                    UUID startCardId);
}
