package com.dekk.auth.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BlacklistAccessToken {

    private String accessToken;
    private String status;

    private BlacklistAccessToken(String accessToken, String status) {
        this.accessToken = accessToken;
        this.status = status;
    }

    public static BlacklistAccessToken create(String accessToken) {
        if(accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("AccessToken은 필수입니다.");
        }
        return new BlacklistAccessToken(accessToken, "logout");
    }

}
