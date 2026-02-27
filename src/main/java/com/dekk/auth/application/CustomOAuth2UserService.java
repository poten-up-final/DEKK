package com.dekk.auth.application;

import com.dekk.user.application.command.UserCreateCommand;
import com.dekk.user.domain.model.User;
import com.dekk.user.domain.model.enums.Provider;
import com.dekk.user.domain.repository.UserRepository;
import com.dekk.security.oauth2.CustomUserDetails;
import com.dekk.security.oauth2.OAuth2UserInfoFactory;
import com.dekk.security.oauth2.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.from(registrationId);

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(provider, attributes);

        User user = saveOrUpdate(userInfo, provider);

        return new CustomUserDetails(user, attributes);
    }

    private User saveOrUpdate(OAuth2UserInfo userInfo, Provider provider) {
        return userRepository.findByProviderAndProviderId(provider, userInfo.getProviderId())
                .orElseGet(() -> userRepository.save(
                        User.create(new UserCreateCommand(userInfo.getEmail(), provider, userInfo.getProviderId()))
                ));

    }
}
