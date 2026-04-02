package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.application.CustomDeckCommandService;
import com.dekk.app.deck.application.DeckCardCommandService;
import com.dekk.app.deck.presentation.request.CustomDeckCreateRequest;
import com.dekk.app.deck.presentation.request.CustomDeckUpdateRequest;
import com.dekk.app.deck.presentation.response.DeckResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks/custom")
@RequiredArgsConstructor
public class CustomDeckCommandController implements CustomDeckCommandApi {

    private final CustomDeckCommandService customDeckCommandService;
    private final DeckCardCommandService deckCardCommandService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCustomDeck(
            @LoginUser Long userId, @Valid @RequestBody CustomDeckCreateRequest request) {
        customDeckCommandService.createCustomDeck(userId, request.toCommand());

        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.CUSTOM_DECK_CREATE_SUCCESS));
    }

    @Override
    @PatchMapping("/{customDeckId}")
    public ResponseEntity<ApiResponse<Void>> updateCustomDeckName(
            @LoginUser Long userId,
            @PathVariable("customDeckId") Long customDeckId,
            @Valid @RequestBody CustomDeckUpdateRequest request) {
        customDeckCommandService.updateCustomDeckName(userId, customDeckId, request.toCommand());

        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.CUSTOM_DECK_UPDATE_SUCCESS));
    }

    @Override
    @DeleteMapping("/{customDeckId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomDeck(
            @LoginUser Long userId, @PathVariable("customDeckId") Long customDeckId) {
        customDeckCommandService.deleteCustomDeck(userId, customDeckId);

        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.CUSTOM_DECK_DELETE_SUCCESS));
    }

    @Override
    @PostMapping("/{customDeckId}/cards/{cardId}")
    public ResponseEntity<ApiResponse<Void>> saveCardToCustomDeck(
            @LoginUser Long userId,
            @PathVariable("customDeckId") Long customDeckId,
            @PathVariable("cardId") Long cardId) {
        deckCardCommandService.saveToCustomDeck(userId, customDeckId, cardId);
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.CUSTOM_DECK_CARD_SAVE_SUCCESS));
    }

    @Override
    @DeleteMapping("/{customDeckId}/cards/{cardId}")
    public ResponseEntity<ApiResponse<Void>> removeCardFromCustomDeck(
            @LoginUser Long userId,
            @PathVariable("customDeckId") Long customDeckId,
            @PathVariable("cardId") Long cardId) {
        deckCardCommandService.removeFromCustomDeck(userId, customDeckId, cardId);
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.CUSTOM_DECK_CARD_DELETE_SUCCESS));
    }
}
