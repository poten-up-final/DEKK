package com.dekk.user.domain.model.enums;

import com.dekk.security.oauth2.exception.AuthErrorCode;
import com.dekk.security.oauth2.exception.CustomOAuth2Exception;

import java.util.Arrays;

public enum Provider {
    GOOGLE,
    KAKAO;

    public static Provider from(String registrationId) {
        return Arrays.stream(Provider.values())
                .filter(p -> p.name().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new CustomOAuth2Exception(AuthErrorCode.UNSUPPORTED_PROVIDER));
    }
}
