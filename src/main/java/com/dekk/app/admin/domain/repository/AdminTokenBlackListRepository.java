package com.dekk.app.admin.domain.repository;

public interface AdminTokenBlackListRepository {

    void save(String accessToken, long ttlSeconds);

    boolean isBlackListed(String accessToken);
}
