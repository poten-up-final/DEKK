package com.dekk.auth.domain.repository;

import com.dekk.auth.domain.model.BlacklistAccessToken;

public interface BlacklistRepository {
    void save(BlacklistAccessToken blacklistAccessToken);
    boolean existsByAccessToken(String accessToken);
}
