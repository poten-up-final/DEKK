package com.dekk.activelog.application.dto.result;

public record UnseenCardResult(
    Long cardId,
    String imageUrl,
    String brandName,
    String productName,
    Integer price
) {
    public static UnseenCardResult of(Long cardId, String imageUrl, String brandName, String productName, Integer price) {
        return new UnseenCardResult(cardId, imageUrl, brandName, productName, price);
    }
}
