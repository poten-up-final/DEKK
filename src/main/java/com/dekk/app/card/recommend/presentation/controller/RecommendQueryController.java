package com.dekk.app.card.recommend.presentation.controller;

import com.dekk.app.card.recommend.application.RecommendQueryService;
import com.dekk.app.card.recommend.presentation.dto.response.RecommendCardResponse;
import com.dekk.app.card.recommend.presentation.response.RecommendResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.response.SliceResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v2/cards")
@RequiredArgsConstructor
public class RecommendQueryController implements RecommendQueryApi {

    private final RecommendQueryService recommendQueryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponse<RecommendCardResponse>>> getRecommendCards(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<RecommendCardResponse> result = recommendQueryService
                .getRecommendCards(userDetails.getId(), pageable)
                .map(RecommendCardResponse::from);

        return ResponseEntity.ok(
                ApiResponse.of(RecommendResultCode.RECOMMEND_CARD_SUCCESS, SliceResponse.from(result)));
    }
}
