package com.dekk.card.domain.model;

import com.dekk.card.application.command.ProductRawDataCommand;
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

@Table(name = "product_raw_data")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", nullable = false, columnDefinition = "json")
    private String rawData;

    private ProductRawData(String rawData) {
        this.rawData = rawData;
    }

    protected static ProductRawData create(ProductRawDataCommand command) {
        if (command.rawData() == null) {
            throw new CardBusinessException(CardErrorCode.PRODUCT_RAW_DATA_IS_REQUIRED_TO_CREATE);
        }

        return new ProductRawData(command.rawData());
    }

    protected void setProduct(Product product) {
        this.product = product;
    }
}
