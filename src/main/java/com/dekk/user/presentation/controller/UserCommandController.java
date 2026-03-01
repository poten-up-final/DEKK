package com.dekk.user.presentation.controller;

import com.dekk.common.response.ApiResponse;
import com.dekk.security.oauth2.CustomUserDetails;
import com.dekk.user.application.UserCommandService;
import com.dekk.user.presentation.request.UserOnboardingRequest;
import com.dekk.user.presentation.response.UserResultCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/users")
@RequiredArgsConstructor
public class UserCommandController implements UserCommandApi {

    private final UserCommandService userCommandService;

    @Override
    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> onboardUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserOnboardingRequest request
    ) {
        userCommandService.onboardUser(userDetails.getId(), request.toCommand());

        return ResponseEntity.ok(ApiResponse.from(UserResultCode.ONBOARDING_SUCCESS));
    }
}
