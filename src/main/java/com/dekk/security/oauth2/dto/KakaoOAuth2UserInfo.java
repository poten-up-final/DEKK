package com.dekk.security.oauth2.dto;

import com.dekk.security.oauth2.exception.AuthErrorCode;
import com.dekk.user.domain.model.enums.Provider;
import com.dekk.security.oauth2.exception.CustomOAuth2Exception;

import java.util.Map;
import java.util.Optional;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if(this.kakaoAccount == null){
            throw new CustomOAuth2Exception(AuthErrorCode.MISSING_USER_INFO);
        }


        this.profile = Optional.ofNullable(
                (Map<String, Object>) kakaoAccount.get("profile")
        ).orElseThrow(() -> new  CustomOAuth2Exception(AuthErrorCode.MISSING_USER_INFO));
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.getOrDefault("email", null);
    }

    @Override
    public String getName() {
        return (String) profile.get("nickname");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}