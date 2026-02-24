package com.dekk.card.domain.model;

import com.dekk.card.application.command.CardCreateCommand;
import com.dekk.card.domain.exception.CardBusinessException;
import com.dekk.card.domain.exception.CardErrorCode;
import com.dekk.card.domain.model.enums.Platform;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "cards")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CardImage cardImage;

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CardRawData cardRawData;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "card_tags",
        joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "tag_id")
    private List<Long> tagIds = new ArrayList<>();

    @Column(name = "origin_id", nullable = false, updatable = false)
    private String originId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    private Integer height;

    private Integer weight;

    private Card(
            CardImage cardImage,
            CardRawData cardRawData,
            List<Product> products,
            List<Long> tagIds,
            String originId,
            Boolean isActive,
            Platform platform,
            Integer height,
            Integer weight
    ) {
        this.cardImage = cardImage;
        this.cardRawData = cardRawData;
        this.products = products;
        this.tagIds = tagIds;
        this.originId = originId;
        this.isActive = isActive;
        this.platform = platform;
        this.height = height;
        this.weight = weight;
    }

    public static Card create(CardCreateCommand command) {
        if (command.originId() == null) {
            throw new CardBusinessException(CardErrorCode.CARD_ORIGIN_ID_IS_REQUIRED_TO_CREATE);
        }

        CardImage cardImage = CardImage.create(command.cardImage());
        CardRawData cardRawData = CardRawData.create(command.cardRawData());
        Card card = new Card(
                cardImage,
                cardRawData,
                command.productCreateCommands().stream().map(Product::create).toList(),
                command.tagIds(),
                command.originId(),
                true,
                command.platform(),
                command.height(),
                command.weight()
        );

        cardImage.setCard(card);
        cardRawData.setCard(card);
        card.products.forEach(product -> product.setCard(card));
        return card;
    }
}
