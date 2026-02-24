package com.dekk.activelog.presentation.controller;

import com.dekk.activelog.application.dto.result.UnseenCardResult;
import com.dekk.activelog.application.service.FeedQueryService;
import com.dekk.activelog.presentation.response.UnseenCardResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController implements FeedApi {

    private final FeedQueryService feedQueryService;

    @Override
    @GetMapping("/unseen")
    public ResponseEntity<List<UnseenCardResponse>> getUnseenCards(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "10") @Min(1) @Max(30) int size
    ) {
        List<UnseenCardResult> results = feedQueryService.getUnseenCards(userId, size);

        List<UnseenCardResponse> responses = results.stream()
            .map(result -> UnseenCardResponse.from(
                result.cardId(),
                result.imageUrl(),
                result.brandName(),
                result.productName(),
                result.price()
            ))
            .toList();

        return ResponseEntity.ok(responses);
    }
}
