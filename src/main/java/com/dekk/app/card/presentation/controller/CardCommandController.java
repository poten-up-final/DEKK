package com.dekk.app.card.presentation.controller;

import com.dekk.app.card.application.CardCommandService;
import com.dekk.app.card.presentation.request.AssignCategoriesRequest;
import com.dekk.app.card.presentation.request.RequestDeleteCardRequest;
import com.dekk.app.card.presentation.response.CardResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adm/v1/cards")
@RequiredArgsConstructor
public class CardCommandController implements CardCommandApi {

    private final CardCommandService cardCommandService;

    @Override
    @PatchMapping("/{cardId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveCard(@PathVariable("cardId") Long cardId) {
        cardCommandService.approveCard(cardId);
        return ResponseEntity.ok(ApiResponse.from(CardResultCode.CARD_APPROVE_SUCCESS));
    }

    @Override
    @PatchMapping("/{cardId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectCard(@PathVariable("cardId") Long cardId) {
        cardCommandService.rejectCard(cardId);
        return ResponseEntity.ok(ApiResponse.from(CardResultCode.CARD_REJECT_SUCCESS));
    }

    @Override
    @PutMapping("/{cardId}/categories")
    public ResponseEntity<ApiResponse<Void>> assignCategories(
            @PathVariable("cardId") Long cardId, @Valid @RequestBody AssignCategoriesRequest request) {
        cardCommandService.assignCategories(cardId, request.toCommand());
        return ResponseEntity.ok(ApiResponse.from(CardResultCode.CARD_CATEGORIES_ASSIGNED));
    }

    @Override
    @PatchMapping("/{cardId}/delete-request")
    public ResponseEntity<ApiResponse<Void>> requestDeleteCard(
            @PathVariable("cardId") Long cardId,
            @Valid @RequestBody RequestDeleteCardRequest request,
            @LoginAdmin Long adminId) {
        cardCommandService.requestDeleteCard(request.toCommand(cardId, adminId));
        return ResponseEntity.ok(ApiResponse.from(CardResultCode.CARD_DELETE_REQUEST_SUCCESS));
    }
}
