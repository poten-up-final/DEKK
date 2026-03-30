package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.application.DefaultDeckQueryService;
import com.dekk.app.deck.presentation.response.DeckResultCode;
import com.dekk.app.deck.presentation.response.MyDeckCardResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.response.PageResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/decks/default")
@RequiredArgsConstructor
public class DefaultDeckQueryController implements DefaultDeckQueryApi {

    private final DefaultDeckQueryService deckQueryService;

    @Override
    @GetMapping("/cards")
    public ResponseEntity<ApiResponse<PageResponse<MyDeckCardResponse>>> getMyDefaultDeckCards(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<MyDeckCardResponse> responsePage = deckQueryService
                .getMyDefaultDeckCards(userDetails.getId(), pageable)
                .map(MyDeckCardResponse::from);

        return ResponseEntity.ok(
                ApiResponse.of(DeckResultCode.DECK_CARD_LIST_SUCCESS, PageResponse.from(responsePage)));
    }
}
