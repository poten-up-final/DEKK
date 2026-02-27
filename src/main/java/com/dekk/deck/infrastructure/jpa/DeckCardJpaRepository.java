package com.dekk.deck.infrastructure.jpa;

import com.dekk.deck.domain.model.DeckCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckCardJpaRepository extends JpaRepository<DeckCard, Long> {
    boolean existsByDeckIdAndCardId(Long deckId, Long cardId);
}
