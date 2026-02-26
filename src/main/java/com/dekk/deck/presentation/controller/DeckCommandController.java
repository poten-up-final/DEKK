package com.dekk.deck.presentation.controller;

import com.dekk.common.response.ApiResponse;
import com.dekk.deck.application.DeckCardCommandService;
import com.dekk.deck.application.DeckCommandService;
import com.dekk.deck.presentation.response.DeckResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks")
@RequiredArgsConstructor
public class DeckCommandController implements DeckCommandApi {

    private final DeckCommandService deckCommandService;
    private final DeckCardCommandService deckCardCommandService;

    @PostMapping("/default")
    public ResponseEntity<ApiResponse<Void>> createDefaultDeck(@RequestParam Long userId) {
        deckCommandService.createDefaultDeck(userId);
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.DEFAULT_DECK_CREATE_SUCCESS));
    }

    @Override
    @PostMapping("/default/cards/{cardId}")
    public ResponseEntity<ApiResponse<Void>> addCardToDefaultDeck(
        Long userId,
        @PathVariable Long cardId
    ) {
        deckCardCommandService.saveToDefaultDeck(userId, cardId);

        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.CARD_SAVE_SUCCESS));
    }
}
