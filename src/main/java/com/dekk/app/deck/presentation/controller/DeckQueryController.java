package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.application.DeckQueryService;
import com.dekk.app.deck.presentation.response.DeckResponse;
import com.dekk.app.deck.presentation.response.DeckResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks")
@RequiredArgsConstructor
public class DeckQueryController implements DeckQueryApi {

    private final DeckQueryService deckQueryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeckResponse>>> getDecks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<DeckResponse> response = deckQueryService.getDecks(userDetails.getId()).stream()
                .map(DeckResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.of(DeckResultCode.DECK_LIST_SUCCESS, response));
    }
}
