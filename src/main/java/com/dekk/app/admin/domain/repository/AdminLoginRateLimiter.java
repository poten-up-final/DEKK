package com.dekk.app.admin.domain.repository;

public interface AdminLoginRateLimiter {
    boolean isLocked(String ip, String email);

    void incrementFailCount(String ip, String email);

    void resetFailCount(String ip, String email);
}
