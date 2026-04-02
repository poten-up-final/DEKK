package com.dekk.global.security.oauth2;

import com.dekk.app.user.application.UserAuthCommandService;
import com.dekk.app.user.application.dto.result.UserAuthResult;
import com.dekk.app.user.domain.model.enums.Provider;
import com.dekk.global.error.BusinessException;
import com.dekk.global.security.oauth2.dto.OAuth2UserInfo;
import com.dekk.global.security.oauth2.exception.CustomOAuth2Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserAuthCommandService userAuthCommandService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Provider provider = Provider.from(registrationId);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(provider, oAuth2User.getAttributes());

        try {
            UserAuthResult authResult =
                    userAuthCommandService.getOrRegisterUser(userInfo.getEmail(), provider, userInfo.getProviderId());

            return new CustomUserDetails(
                    authResult.id(),
                    authResult.email(),
                    authResult.role(),
                    authResult.status(),
                    oAuth2User.getAttributes());
        } catch (BusinessException e) {
            throw new CustomOAuth2Exception(e.errorCode());
        }
    }
}
