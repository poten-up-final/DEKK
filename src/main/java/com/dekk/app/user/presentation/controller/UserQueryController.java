package com.dekk.app.user.presentation.controller;

import com.dekk.app.user.application.UserQueryService;
import com.dekk.app.user.application.dto.result.UserInfoResult;
import com.dekk.app.user.presentation.response.UserInfoResponse;
import com.dekk.app.user.presentation.response.UserResultCode;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/w/v1/users")
@RequiredArgsConstructor
public class UserQueryController implements UserQueryApi {

    private final UserQueryService userQueryService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(@LoginUser Long userId) {
        UserInfoResult result = userQueryService.getMyInfo(userId);
        UserInfoResponse response = UserInfoResponse.from(result);

        return ResponseEntity.ok(ApiResponse.of(UserResultCode.GET_MY_INFO_SUCCESS, response));
    }
}
