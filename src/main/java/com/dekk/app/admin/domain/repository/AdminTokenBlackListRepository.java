package com.dekk.app.admin.domain.repository;

public interface AdminTokenBlackListRepository {
    void save(String accessToken, long ttlSeconds);

    boolean isBlacklisted(String accessToken);

    void saveKickOut(Long adminId, long ttlSeconds);

    boolean isKickedOut(Long adminId);
}
