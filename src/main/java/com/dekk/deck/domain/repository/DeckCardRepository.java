package com.dekk.deck.domain.repository;

import com.dekk.deck.domain.model.DeckCard;

public interface DeckCardRepository {
    DeckCard save(DeckCard deckCard);
    boolean existsByDeckIdAndCardId(Long deckId, Long cardId);
}
