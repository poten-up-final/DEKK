package com.dekk.deck.application;

import com.dekk.deck.domain.exception.DeckBusinessException;
import com.dekk.deck.domain.exception.DeckErrorCode;
import com.dekk.deck.domain.model.Deck;
import com.dekk.deck.domain.model.DeckCard;
import com.dekk.deck.domain.repository.DeckCardRepository;
import com.dekk.deck.domain.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeckCardCommandService {

    private final DeckRepository deckRepository;
    private final DeckCardRepository deckCardRepository;

    @Transactional
    public void saveToDefaultDeck(Long userId, Long cardId) {
        Deck defaultDeck = deckRepository.findByUserIdAndIsDefaultTrue(userId)
            .orElseThrow(() -> new DeckBusinessException(DeckErrorCode.DEFAULT_DECK_NOT_FOUND));

        if (deckCardRepository.existsByDeckIdAndCardId(defaultDeck.getId(), cardId)) {
            return;
        }

        deckCardRepository.save(DeckCard.create(defaultDeck.getId(), cardId));
    }
}
