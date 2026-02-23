package com.dekk.card.domain.model;

import com.dekk.card.application.command.CardRawDataCommand;
import com.dekk.card.domain.exception.CardBusinessException;
import com.dekk.card.domain.exception.CardErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Table(name = "card_raw_data")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardRawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", nullable = false, columnDefinition = "json")
    private String rawData;

    private CardRawData(
            String rawData
    ) {
        this.rawData = rawData;
    }

    protected static CardRawData create(CardRawDataCommand command) {
        if (command.rawData() == null) {
            throw new CardBusinessException(CardErrorCode.CARD_RAW_DATA_IS_REQUIRED_TO_CREATE);
        }
        return new CardRawData(command.rawData());
    }

    protected void setCard(Card card) {
        this.card = card;
    }
}
