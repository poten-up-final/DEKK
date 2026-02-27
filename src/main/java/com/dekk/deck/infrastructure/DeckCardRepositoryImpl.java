package com.dekk.deck.infrastructure;

import com.dekk.deck.domain.model.DeckCard;
import com.dekk.deck.domain.repository.DeckCardRepository;
import com.dekk.deck.infrastructure.jpa.DeckCardJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeckCardRepositoryImpl implements DeckCardRepository {

    private final DeckCardJpaRepository deckCardJpaRepository;

    @Override
    public DeckCard save(DeckCard deckCard) {
        return deckCardJpaRepository.save(deckCard);
    }

    @Override
    public boolean existsByDeckIdAndCardId(Long deckId, Long cardId) {
        return deckCardJpaRepository.existsByDeckIdAndCardId(deckId, cardId);
    }
}
