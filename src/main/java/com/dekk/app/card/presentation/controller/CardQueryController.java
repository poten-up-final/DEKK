package com.dekk.app.card.presentation.controller;

import com.dekk.app.card.application.CardQueryService;
import com.dekk.app.card.presentation.response.CardResultCode;
import com.dekk.app.card.presentation.response.GuestCardResponse;
import com.dekk.app.card.presentation.response.MemberCardResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.response.PageResponse;
import com.dekk.global.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/cards")
@RequiredArgsConstructor
public class CardQueryController implements CardQueryApi {

    private final CardQueryService cardQueryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<?>>> getCards(
            @LoginUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (userId != null) {
            return ResponseEntity.ok(ApiResponse.of(CardResultCode.MEMBER_CARD_LIST_SUCCESS, getMemberCards(pageable)));
        }
        return ResponseEntity.ok(ApiResponse.of(CardResultCode.GUEST_CARD_LIST_SUCCESS, getGuestCards(pageable)));
    }

    private PageResponse<MemberCardResponse> getMemberCards(Pageable pageable) {
        return PageResponse.from(cardQueryService.getCardsForMember(pageable).map(MemberCardResponse::from));
    }

    private PageResponse<GuestCardResponse> getGuestCards(Pageable pageable) {
        return PageResponse.from(cardQueryService.getCardsForGuest(pageable).map(GuestCardResponse::from));
    }
}
