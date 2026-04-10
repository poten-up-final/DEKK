package com.dekk.app.card.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dekk.app.card.domain.exception.CardBusinessException;
import com.dekk.app.card.domain.exception.CardErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Product 도메인 테스트")
class ProductTest {

    @Nested
    class CreateByUserTest {

        @Test
        @DisplayName("정상적인 파라미터로 사용자 상품을 생성한다")
        void createByUser_Success() {
            // given
            Long resourceId = 1L;
            String brand = "무신사 스탠다드";
            String name = "릴렉스 핏 크루 넥 반팔 티셔츠";
            Integer price = 19900;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when
            Product product = Product.createByUser(resourceId, brand, name, price, productUrl, option);

            // then
            assertThat(product).isNotNull();
            assertThat(product.getResourceId()).isEqualTo(resourceId);
            assertThat(product.getBrand()).isEqualTo(brand);
            assertThat(product.getName()).isEqualTo(name);
            assertThat(product.getPrice()).isEqualTo(price);
            assertThat(product.getProductUrl()).isEqualTo(productUrl);
            assertThat(product.getOption()).isEqualTo(option);
            assertThat(product.isActive()).isTrue();
            assertThat(product.isSimilar()).isFalse();

            assertThat(product.getOriginId()).isNull();
            assertThat(product.getProductImage()).isNull();
        }

        @Test
        @DisplayName("option이 null이어도 상품을 생성한다")
        void createByUser_OptionNull_Success() {
            // given
            Long resourceId = 1L;
            String brand = "나이키";
            String name = "에어포스 1";
            Integer price = 129000;
            String productUrl = "https://www.nike.com/product/123";
            String option = null;

            // when
            Product product = Product.createByUser(resourceId, brand, name, price, productUrl, option);

            // then
            assertThat(product).isNotNull();
            assertThat(product.getOption()).isNull();
        }

        @Test
        @DisplayName("resourceId가 null이면 예외를 발생시킨다")
        void createByUser_ResourceIdNull_ThrowsException() {
            // given
            Long resourceId = null;
            String brand = "무신사 스탠다드";
            String name = "티셔츠";
            Integer price = 19900;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.RESOURCE_ID_IS_REQUIRED_FOR_USER_PRODUCT.message());
        }

        @Test
        @DisplayName("brand가 null이면 예외를 발생시킨다")
        void createByUser_BrandNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = null;
            String name = "티셔츠";
            Integer price = 19900;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_BRAND_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("brand가 빈 문자열이면 예외를 발생시킨다")
        void createByUser_BrandBlank_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = "   ";
            String name = "티셔츠";
            Integer price = 19900;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_BRAND_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("name이 null이면 예외를 발생시킨다")
        void createByUser_NameNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = "무신사 스탠다드";
            String name = null;
            Integer price = 19900;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_NAME_IS_REQUIRED_TO_CREATE.message());
        }

        @Test
        @DisplayName("name이 빈 문자열이면 예외를 발생시킨다")
        void createByUser_NameBlank_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = "무신사 스탠다드";
            String name = "   ";
            Integer price = 19900;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_NAME_IS_REQUIRED_TO_CREATE.message());
        }

        @Test
        @DisplayName("price가 null이면 예외를 발생시킨다")
        void createByUser_PriceNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = "무신사 스탠다드";
            String name = "티셔츠";
            Integer price = null;
            String productUrl = "https://www.musinsa.com/app/goods/123456";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_PRICE_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("productUrl이 null이면 예외를 발생시킨다")
        void createByUser_ProductUrlNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = "무신사 스탠다드";
            String name = "티셔츠";
            Integer price = 19900;
            String productUrl = null;
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_URL_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("productUrl이 빈 문자열이면 예외를 발생시킨다")
        void createByUser_ProductUrlBlank_ThrowsException() {
            // given
            Long resourceId = 1L;
            String brand = "무신사 스탠다드";
            String name = "티셔츠";
            Integer price = 19900;
            String productUrl = "   ";
            String option = "WHITE / L";

            // when & then
            assertThatThrownBy(
                            () -> Product.createByUser(resourceId, brand, name, price, productUrl, option))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.PRODUCT_URL_IS_REQUIRED.message());
        }
    }
}
