package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.application.DeckCardCommandService;
import com.dekk.app.deck.application.DefaultDeckCommandService;
import com.dekk.app.deck.presentation.response.DeckResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks/default")
@RequiredArgsConstructor
public class DefaultDeckCommandController implements DefaultDeckCommandApi {

    private final DefaultDeckCommandService deckCommandService;
    private final DeckCardCommandService deckCardCommandService;

    @Override
    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<ApiResponse<Void>> removeCardFromDefaultDeck(
            @LoginUser Long userId, @PathVariable("cardId") Long cardId) {
        deckCardCommandService.removeFromDefaultDeck(userId, cardId);
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.DECK_CARD_DELETED_SUCCESS));
    }
}
