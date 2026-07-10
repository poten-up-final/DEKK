package com.dekk.app.admin.domain.repository;

public interface AdminTokenBlackListRepository {
    void save(String accessToken, long ttlSeconds);

    boolean isBlacklisted(String accessToken);

    boolean saveKickOut(Long adminId, long ttlSeconds);

    boolean isKickedOut(Long adminId);
}
