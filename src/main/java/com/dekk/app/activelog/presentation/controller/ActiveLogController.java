package com.dekk.app.activelog.presentation.controller;

import com.dekk.app.activelog.application.ActiveLogCommandService;
import com.dekk.app.activelog.application.dto.command.SwipeCommand;
import com.dekk.app.activelog.presentation.request.SwipeRequest;
import com.dekk.app.activelog.presentation.response.ActiveLogResultCode;
import com.dekk.app.card.application.CardQueryService;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActiveLogController implements ActiveLogApi {

    private final ActiveLogCommandService activeLogCommandService;
    private final CardQueryService cardQueryService;

    @Override
    @PostMapping("/w/v1/cards/{publicId}/swipe")
    public ResponseEntity<ApiResponse<Void>> swipeCard(
            @PathVariable UUID publicId, @Valid @RequestBody SwipeRequest request, @LoginUser Long userId) {
        Long cardId = cardQueryService.getCardIdByPublicId(publicId);
        SwipeCommand command = new SwipeCommand(userId, cardId, request.swipeType());
        activeLogCommandService.saveSwipeAction(command);

        return ResponseEntity.ok(ApiResponse.from(ActiveLogResultCode.SWIPE_SUCCESS));
    }
}
