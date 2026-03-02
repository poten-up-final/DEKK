package com.dekk.activelog.presentation.controller;

import com.dekk.activelog.application.ActiveLogCommandService;
import com.dekk.activelog.application.dto.command.SwipeCommand;
import com.dekk.activelog.presentation.request.SwipeRequest;
import com.dekk.activelog.presentation.response.ActiveLogResultCode;
import com.dekk.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/activelogs")
@RequiredArgsConstructor
public class ActiveLogController implements ActiveLogApi {

    private final ActiveLogCommandService activeLogCommandService;

    @Override
    @PostMapping("/{cardId}/swipe")
    public ResponseEntity<ApiResponse<Void>> swipeCard(
        @PathVariable("cardId") Long cardId,
        @Valid @RequestBody SwipeRequest request
    ) {
        // TODO: Spring Security 적용 후 SecurityContext에서 실제 userId 추출
        Long userId = null;

        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.from(ActiveLogResultCode.GUEST_SWIPE_IGNORED));
        }

        SwipeCommand command = new SwipeCommand(userId, cardId, request.swipeType());
        activeLogCommandService.saveSwipeAction(command);

        return ResponseEntity.ok(ApiResponse.from(ActiveLogResultCode.SWIPE_SUCCESS));
    }
}
