package com.dekk.app.user.presentation.controller;

import com.dekk.app.user.application.UserCommandService;
import com.dekk.app.user.presentation.request.UserOnboardingRequest;
import com.dekk.app.user.presentation.request.UserProfileUpdateRequest;
import com.dekk.app.user.presentation.response.UserResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
            @LoginUser Long userId, @Valid @RequestBody UserOnboardingRequest request) {
        userCommandService.onboardUser(userId, request.toCommand());

        return ResponseEntity.ok(ApiResponse.from(UserResultCode.ONBOARDING_SUCCESS));
    }

    @Override
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @LoginUser Long userId, @Valid @RequestBody UserProfileUpdateRequest request) {
        userCommandService.updateProfileInfo(userId, request.toCommand());

        return ResponseEntity.ok(ApiResponse.from(UserResultCode.PROFILE_UPDATE_SUCCESS));
    }

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@LoginUser Long userId) {
        userCommandService.deleteUser(userId);

        return ResponseEntity.ok(ApiResponse.from(UserResultCode.USER_DELETE_SUCCESS));
    }
}
