package com.dekk.global.security.jwt;

public interface TokenBlacklistManager {

    boolean isBlacklisted(String token);
}
