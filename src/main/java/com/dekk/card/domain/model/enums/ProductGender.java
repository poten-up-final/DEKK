package com.dekk.card.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductGender {
    WOMEN("여성"),
    MEN("남성"),
    UNISEX("남녀공용"),
    UNDEFINED("미정");

    private final String description;

    public static ProductGender musinsaParse(String value) {
        if (value == null || value.isBlank()) {
            return UNDEFINED;
        }

        return switch (value.trim().toUpperCase()) {
            case "WOMEN" -> ProductGender.WOMEN;
            case "MEN" -> ProductGender.MEN;
            case "ALL" -> ProductGender.UNISEX;
            default -> ProductGender.UNDEFINED;
        };
    }
}
