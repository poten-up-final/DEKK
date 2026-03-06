package com.dekk.deck.infrastructure.jpa;

import com.dekk.deck.domain.model.DeckCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeckCardJpaRepository extends JpaRepository<DeckCard, Long> {
    boolean existsByDeckIdAndCardId(Long deckId, Long cardId);

    Page<DeckCard> findAllByDeckId(Long deckId, Pageable pageable);

    Optional<DeckCard> findByDeckIdAndCardId(Long deckId, Long CardId);

    void deleteAllByDeckId(Long deckId);

    @Query("""
         SELECT d.deckId AS deckId, COUNT(d) AS cardCount
         FROM DeckCard d
         WHERE d.deckId IN :deckIds
         GROUP BY d.deckId
        """)
    List<DeckCardCountProjection> countCardsByDeckIds(@Param("deckIds") List<Long> deckIds);

    long countByDeckId(Long deckId);
}
