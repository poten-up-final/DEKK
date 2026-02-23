package com.dekk.card.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {
    MUSINSA("무신사"),
    ZIGZAG("지그재그"),
    ABLY("에이블리");

    private final String description;
}
