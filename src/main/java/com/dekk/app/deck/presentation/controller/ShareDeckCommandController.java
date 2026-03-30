package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.application.ShareDeckCommandService;
import com.dekk.app.deck.application.dto.result.ShareTokenResult;
import com.dekk.app.deck.presentation.request.SharedDeckJoinRequest;
import com.dekk.app.deck.presentation.response.DeckResultCode;
import com.dekk.app.deck.presentation.response.ShareTokenResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks")
@RequiredArgsConstructor
public class ShareDeckCommandController implements ShareDeckCommandApi {

    private final ShareDeckCommandService shareDeckCommandService;

    @Override
    @PostMapping("/custom/{customDeckId}/share")
    public ResponseEntity<ApiResponse<ShareTokenResponse>> turnOnShare(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("customDeckId") Long customDeckId) {

        ShareTokenResult result = shareDeckCommandService.turnOnShareAndGetToken(userDetails.getId(), customDeckId);

        return ResponseEntity.ok(ApiResponse.of(DeckResultCode.SHARE_DECK_ON_SUCCESS, ShareTokenResponse.from(result)));
    }

    @Override
    @DeleteMapping("/custom/{customDeckId}/share")
    public ResponseEntity<ApiResponse<Void>> turnOffShare(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("customDeckId") Long customDeckId) {
        shareDeckCommandService.turnOffShare(userDetails.getId(), customDeckId);
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.SHARE_DECK_OFF_SUCCESS));
    }

    @Override
    @PostMapping("/shared/join")
    public ResponseEntity<ApiResponse<Void>> joinSharedDeck(
            @AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody SharedDeckJoinRequest request) {
        shareDeckCommandService.joinSharedDeck(userDetails.getId(), request.token());
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.SHARE_DECK_JOIN_SUCCESS));
    }

    @Override
    @DeleteMapping("/shared/{sharedDeckId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveSharedDeck(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("sharedDeckId") Long sharedDeckId) {
        shareDeckCommandService.leaveSharedDeck(userDetails.getId(), sharedDeckId);
        return ResponseEntity.ok(ApiResponse.from(DeckResultCode.SHARE_DECK_LEAVE_SUCCESS));
    }
}
