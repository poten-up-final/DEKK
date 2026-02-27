package com.dekk.deck.application.dto.result;

import java.util.List;

public record MyDeckCardResult(
    Long cardId,
    String cardImageUrl,
    Integer height,
    Integer weight,
    List<String> tag,
    List<ProductDetail> product
) {
    public record ProductDetail(
        String brand,
        String url,
        String name,
        String imageUrl
    ){}
}
