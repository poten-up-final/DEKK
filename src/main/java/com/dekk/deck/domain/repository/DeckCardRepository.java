package com.dekk.deck.domain.repository;

import com.dekk.deck.domain.model.DeckCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeckCardRepository {
    DeckCard save(DeckCard deckCard);
    boolean existsByDeckIdAndCardId(Long deckId, Long cardId);

    Page<DeckCard> findAllByDeckId(Long deckId, Pageable pageable);
}
