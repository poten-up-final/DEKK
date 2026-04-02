package com.dekk.global.security.jwt;

public interface JwtPrincipal {
    Long getJwtId();

    String getJwtEmail();

    String getJwtRole();

    String getJwtStatus();
}
