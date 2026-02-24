package com.dekk.activelog.application.service;

import com.dekk.activelog.presentation.response.UnseenCardResponse;
// import com.dekk.card.application.service.CardQueryService;
// import com.dekk.card.domain.model.Card;
// import com.dekk.card.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    // private final CardQueryService cardQueryService;

    public List<UnseenCardResponse> getUnseenCards(Long userId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);

        // TODO: 효진님의 CardQueryService.getUnseenCardsByUserId() 구현이 완료/머지되면 아래 로직 주석 해제 예정! (by SungRyulCho)
        /*
        List<Card> unseenCards = cardQueryService.getUnseenCardsByUserId(userId, pageRequest);

        return unseenCards.stream()
                .map(card -> {
                    String productName = "";
                    Integer price = 0;
                    if (!card.getCardProducts().isEmpty()) {
                        Product mainProduct = card.getCardProducts().get(0).getProduct();
                        productName = mainProduct.getName();
                        price = mainProduct.getPrice();
                    }

                    return UnseenCardResponse.from(
                            card.getId(),
                            card.getCardImage().getImageUrl(),
                            card.getPlatform().getDescription(),
                            productName,
                            price
                    );
                })
                .toList();
        */

        // 컴파일 에러를 막고 API 테스트를 위해 임시로 빈 리스트 반환
        return Collections.emptyList();
    }
}
