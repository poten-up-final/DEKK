package com.dekk.domain.auth.dto;

import com.dekk.domain.user.entity.Provider;

import java.util.Map;

public interface OAuth2UserInfo {

    Map<String, Object> getAttributes();

    Provider getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
