package com.dekk.deck.application;

import com.dekk.deck.application.dto.result.MyDeckCardResult;
import com.dekk.deck.domain.exception.DeckBusinessException;
import com.dekk.deck.domain.exception.DeckErrorCode;
import com.dekk.deck.domain.model.Deck;
import com.dekk.deck.domain.model.DeckCard;
import com.dekk.deck.domain.repository.DeckCardRepository;
import com.dekk.deck.domain.repository.DeckRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckQueryService {

    private final DeckRepository deckRepository;
    private final DeckCardRepository deckCardRepository;
    // private final CardQueryService cardQueryService; // TODO: 향후 Card 도메인 완성 시 주입 예정

    public Page<MyDeckCardResult> getMyDefaultDeckCards(Long userId, Pageable pageable) {
        Deck defaultDeck = deckRepository.findByUserIdAndIsDefaultTrue(userId)
            .orElseThrow(() -> new DeckBusinessException(DeckErrorCode.DEFAULT_DECK_NOT_FOUND));

        Page<DeckCard> deckCards = deckCardRepository.findAllByDeckId(defaultDeck.getId(), pageable);

        // TODO: 향후 CardQueryService 연동 로직 추가 (cardIds 추출 후 맵핑)

        return deckCards.map(dc -> new MyDeckCardResult(
            dc.getCardId(),
            "", // TODO: cardImageUrl
            null, // TODO: height
            null, // TODO: weight
            Collections.emptyList(), // TODO: tag
            Collections.emptyList()  // TODO: product
        ));
    }
}
