package com.dekk.app.card.domain.model;

import com.dekk.app.card.application.dto.command.CardCreateByUserCommand;
import com.dekk.app.card.application.dto.command.CardCreateCommand;
import com.dekk.app.card.domain.exception.CardBusinessException;
import com.dekk.app.card.domain.exception.CardErrorCode;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.model.enums.Platform;
import com.dekk.app.card.domain.model.enums.TargetGender;
import com.dekk.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "cards")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CardImage cardImage;

    @Column(name = "resource_id")
    private Long resourceId;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardProduct> cardProducts = new ArrayList<>();

    @Column(name = "tags")
    private String tags;

    @Column(name = "origin_id", updatable = false)
    private String originId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_gender")
    private TargetGender targetGender;

    private Integer height;

    private Integer weight;

    private Card(
            CardImage cardImage,
            String tags,
            String originId,
            Platform platform,
            TargetGender targetGender,
            Integer height,
            Integer weight,
            Long resourceId) {
        this.publicId = UUID.randomUUID();
        this.cardImage = cardImage;
        this.tags = tags;
        this.originId = originId;
        this.status = CardStatus.PENDING;
        this.platform = platform;
        this.targetGender = targetGender;
        this.height = height;
        this.weight = weight;
        this.resourceId = resourceId;
    }

    public static Card createByCrawl(CardCreateCommand command) {
        if (command.originId() == null) {
            throw new CardBusinessException(CardErrorCode.CARD_ORIGIN_ID_IS_REQUIRED_TO_CREATE);
        }

        CardImage cardImage = CardImage.create(command.cardImage());

        Card card = new Card(
                cardImage,
                command.tags(),
                command.originId(),
                command.platform(),
                command.targetGender(),
                command.height(),
                command.weight(),
                null);

        cardImage.setCard(card);
        command.productCreateCommands().stream()
                .map(Product::createByCrawl)
                .map(product -> CardProduct.create(card, product))
                .forEach(card.cardProducts::add);
        return card;
    }

    public static Card createByUser(CardCreateByUserCommand command) {
        validateUserCardParameters(command);

        Card card = new Card(
                null,
                command.tags(),
                null,
                null,
                command.targetGender(),
                command.height(),
                command.weight(),
                command.resourceId());

        if (command.products() != null) {
            command.products().stream()
                    .map(cmd -> Product.createByUser(
                            cmd.resourceId(), cmd.brand(), cmd.name(), cmd.price(), cmd.productUrl(), cmd.option()))
                    .map(product -> CardProduct.create(card, product))
                    .forEach(card.cardProducts::add);
        }

        return card;
    }

    private static void validateUserCardParameters(CardCreateByUserCommand command) {
        if (command.resourceId() == null) {
            throw new CardBusinessException(CardErrorCode.RESOURCE_ID_IS_REQUIRED_FOR_USER_CARD);
        }

        if (command.targetGender() == null) {
            throw new CardBusinessException(CardErrorCode.TARGET_GENDER_IS_REQUIRED);
        }

        if (command.height() == null) {
            throw new CardBusinessException(CardErrorCode.HEIGHT_IS_REQUIRED);
        }

        if (command.weight() == null) {
            throw new CardBusinessException(CardErrorCode.WEIGHT_IS_REQUIRED);
        }
    }

    public void approve() {
        validateStatusChangeable();
        this.status = CardStatus.APPROVED;
    }

    public void reject() {
        validateStatusChangeable();
        this.status = CardStatus.REJECTED;
    }

    public void requestDelete() {
        validateStatusChangeable();
        this.status = CardStatus.DELETE_REQUESTED;
    }

    private void validateStatusChangeable() {
        if (!this.status.canChangeStatus()) {
            throw new CardBusinessException(CardErrorCode.CANNOT_CHANGE_STATUS_OF_DELETE_REQUESTED);
        }
    }
}
