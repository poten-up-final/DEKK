package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.application.CustomDeckQueryService;
import com.dekk.app.deck.application.dto.result.CustomDeckCardsResult;
import com.dekk.app.deck.presentation.response.CustomDeckCardsResponse;
import com.dekk.app.deck.presentation.response.CustomDeckResponse;
import com.dekk.app.deck.presentation.response.DeckResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks/custom")
@RequiredArgsConstructor
public class CustomDeckQueryController implements CustomDeckQueryApi {

    private final CustomDeckQueryService customDeckQueryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomDeckResponse>>> getMyCustomDecks(@LoginUser Long userId) {

        List<CustomDeckResponse> response = customDeckQueryService.getMyCustomDecks(userId).stream()
                .map(CustomDeckResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.of(DeckResultCode.CUSTOM_DECK_LIST_SUCCESS, response));
    }

    @Override
    @GetMapping("/{customDeckId}/cards")
    public ResponseEntity<ApiResponse<CustomDeckCardsResponse>> getCustomDeckCards(
            @LoginUser Long userId, @PathVariable("customDeckId") Long customDeckId) {

        CustomDeckCardsResult result = customDeckQueryService.getCustomDeckCards(userId, customDeckId);

        return ResponseEntity.ok(
                ApiResponse.of(DeckResultCode.CUSTOM_DECK_CARD_LIST_SUCCESS, CustomDeckCardsResponse.from(result)));
    }
}
