package com.dekk.app.auth.application;

import com.dekk.app.user.application.command.UserCreateCommand;
import com.dekk.app.user.domain.model.User;
import com.dekk.app.user.domain.model.enums.Provider;
import com.dekk.app.user.domain.repository.UserRepository;
import com.dekk.global.security.oauth2.CustomUserDetails;
import com.dekk.global.security.oauth2.OAuth2UserInfoFactory;
import com.dekk.global.security.oauth2.dto.OAuth2UserInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.from(registrationId);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(provider, oAuth2User.getAttributes());

        User user = getOrRegisterUser(userInfo, provider);

        return new CustomUserDetails(
                user.getId(), user.getEmail(), user.getRole().getKey(), user.getStatus(), oAuth2User.getAttributes());
    }

    private User getOrRegisterUser(OAuth2UserInfo userInfo, Provider provider) {
        Optional<User> user = userRepository.findByProviderAndProviderId(provider, userInfo.getProviderId());

        if (user.isPresent()) {
            return user.get();
        }

        return userRepository.save(
                User.create(new UserCreateCommand(userInfo.getEmail(), provider, userInfo.getProviderId())));
    }
}
