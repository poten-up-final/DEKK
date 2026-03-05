package com.dekk.category.domain.model;

import com.dekk.category.domain.exception.CategoryBusinessException;
import com.dekk.category.domain.exception.CategoryErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"card_id", "category_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    private CardCategory(Long cardId, Long categoryId) {
        this.cardId = cardId;
        this.categoryId = categoryId;
    }

    public static CardCategory create(Long cardId, Long categoryId) {
        if (cardId == null) {
            throw new CategoryBusinessException(CategoryErrorCode.CARD_ID_IS_REQUIRED);
        }
        if (categoryId == null) {
            throw new CategoryBusinessException(CategoryErrorCode.CATEGORY_ID_IS_REQUIRED);
        }
        return new CardCategory(cardId, categoryId);
    }
}
