package com.dekk.deck.domain.model;

import com.dekk.common.entity.BaseTimeEntity;
import com.dekk.deck.domain.exception.DeckBusinessException;
import com.dekk.deck.domain.exception.DeckErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "deck_cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE deck_cards SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
public class DeckCard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long deckId;

    @Column(nullable = false)
    private Long cardId;

    private DeckCard(Long deckId, Long cardId) {
        this.deckId = deckId;
        this.cardId = cardId;
    }

    public static DeckCard create(Long deckId, Long cardId) {
        if (deckId == null) {
            throw new DeckBusinessException(DeckErrorCode.DECK_ID_IS_REQUIRED);
        }
        if (cardId == null) {
            throw new DeckBusinessException(DeckErrorCode.CARD_ID_IS_REQUIRED);
        }
        return new DeckCard(deckId, cardId);
    }
}
