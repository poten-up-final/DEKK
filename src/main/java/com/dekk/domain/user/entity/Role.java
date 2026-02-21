package com.dekk.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MEMBER("ROLE_MEMBER", "일반 사용자"),
    SELLER("ROLE_SELLER", "사업자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
