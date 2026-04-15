package com.dekk.app.card.domain.model;

import com.dekk.app.card.application.dto.command.ProductCreateCommand;
import com.dekk.app.card.domain.exception.CardBusinessException;
import com.dekk.app.card.domain.exception.CardErrorCode;
import com.dekk.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "products")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProductImage productImage;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String name;

    @Column(name = "origin_id")
    private String originId;

    @Column(name = "product_url", columnDefinition = "text")
    private String productUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    private Product(
            ProductImage productImage,
            String brand,
            String name,
            String originId,
            String productUrl,
            boolean isActive,
            Long resourceId) {
        this.productImage = productImage;
        this.brand = brand;
        this.name = name;
        this.originId = originId;
        this.productUrl = productUrl;
        this.isActive = isActive;
        this.resourceId = resourceId;
    }

    public static Product createByCrawl(ProductCreateCommand command) {
        ProductImage productImage = ProductImage.create(command.productImage());

        if (command.name() == null) {
            throw new CardBusinessException(CardErrorCode.PRODUCT_NAME_IS_REQUIRED_TO_CREATE);
        }

        if (command.originId() == null) {
            throw new CardBusinessException(CardErrorCode.PRODUCT_EXTERNAL_ID_IS_REQUIRED_TO_CREATE);
        }

        Product product = new Product(
                productImage,
                command.brand(),
                command.name(),
                command.originId(),
                command.productUrl(),
                command.isActive(),
                null);

        productImage.setProduct(product);
        return product;
    }

    public static Product createByUser(Long resourceId, String brand, String name, String productUrl) {

        if (resourceId == null) {
            throw new CardBusinessException(CardErrorCode.RESOURCE_ID_IS_REQUIRED_FOR_USER_PRODUCT);
        }

        if (brand == null || brand.isBlank()) {
            throw new CardBusinessException(CardErrorCode.PRODUCT_BRAND_IS_REQUIRED);
        }

        if (name == null || name.isBlank()) {
            throw new CardBusinessException(CardErrorCode.PRODUCT_NAME_IS_REQUIRED_TO_CREATE);
        }

        if (productUrl == null || productUrl.isBlank()) {
            throw new CardBusinessException(CardErrorCode.PRODUCT_URL_IS_REQUIRED);
        }

        return new Product(null, brand, name, null, productUrl, true, resourceId);
    }
}
