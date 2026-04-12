package com.dekk.app.card.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dekk.app.card.application.dto.command.CardCreateByUserCommand;
import com.dekk.app.card.application.dto.command.ProductCreateByUserCommand;
import com.dekk.app.card.domain.exception.CardBusinessException;
import com.dekk.app.card.domain.exception.CardErrorCode;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.model.enums.TargetGender;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Card 도메인 테스트")
class CardTest {

    @Nested
    class CreateByUserTest {

        @Test
        @DisplayName("정상적인 파라미터로 사용자 카드를 생성한다")
        void createByUser_Success() {
            // given
            CardCreateByUserCommand command = new CardCreateByUserCommand(
                    1L, // resourceId
                    TargetGender.MEN, // targetGender
                    175, // height
                    70, // weight
                    "캐주얼,데일리", // tags
                    Collections.emptyList() // products
                    );

            // when
            Card card = Card.createByUser(command);

            // then
            assertThat(card).isNotNull();
            assertThat(card.getPublicId()).isNotNull();
            assertThat(card.getResourceId()).isEqualTo(1L);
            assertThat(card.getTargetGender()).isEqualTo(TargetGender.MEN);
            assertThat(card.getHeight()).isEqualTo(175);
            assertThat(card.getWeight()).isEqualTo(70);
            assertThat(card.getTags()).isEqualTo("캐주얼,데일리");
            assertThat(card.getStatus()).isEqualTo(CardStatus.PENDING);
            assertThat(card.getCardProducts()).isEmpty();

            assertThat(card.getOriginId()).isNull();
            assertThat(card.getPlatform()).isNull();
            assertThat(card.getCardImage()).isNull();
        }

        @Test
        @DisplayName("tags가 null이어도 카드를 생성한다")
        void createByUser_TagsNull_Success() {
            // given
            CardCreateByUserCommand command =
                    new CardCreateByUserCommand(1L, TargetGender.WOMEN, 165, 55, null, null);

            // when
            Card card = Card.createByUser(command);

            // then
            assertThat(card).isNotNull();
            assertThat(card.getTags()).isNull();
        }

        @Test
        @DisplayName("Product 목록과 함께 사용자 카드를 생성한다")
        void createByUser_WithProducts_Success() {
            // given
            List<ProductCreateByUserCommand> products = List.of(
                    new ProductCreateByUserCommand(
                            2L, "무신사 스탠다드", "릴렉스 핏 티셔츠", "https://example.com/1"),
                    new ProductCreateByUserCommand(3L, "나이키", "에어포스 1", "https://example.com/2"));

            CardCreateByUserCommand command =
                    new CardCreateByUserCommand(1L, TargetGender.MEN, 175, 70, "캐주얼,데일리", products);

            // when
            Card card = Card.createByUser(command);

            // then
            assertThat(card).isNotNull();
            assertThat(card.getCardProducts()).hasSize(2);
            assertThat(card.getCardProducts().get(0).getProduct().getBrand()).isEqualTo("무신사 스탠다드");
            assertThat(card.getCardProducts().get(0).getProduct().getName()).isEqualTo("릴렉스 핏 티셔츠");
            assertThat(card.getCardProducts().get(0).getProduct().getResourceId()).isEqualTo(2L);
            assertThat(card.getCardProducts().get(1).getProduct().getBrand()).isEqualTo("나이키");
            assertThat(card.getCardProducts().get(1).getProduct().getResourceId()).isEqualTo(3L);
        }

        @Test
        @DisplayName("resourceId가 null이면 예외를 발생시킨다")
        void createByUser_ResourceIdNull_ThrowsException() {
            // given
            CardCreateByUserCommand command =
                    new CardCreateByUserCommand(null, TargetGender.MEN, 175, 70, "캐주얼", null);

            // when & then
            assertThatThrownBy(() -> Card.createByUser(command))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.RESOURCE_ID_IS_REQUIRED_FOR_USER_CARD.message());
        }

        @Test
        @DisplayName("targetGender가 null이면 예외를 발생시킨다")
        void createByUser_TargetGenderNull_ThrowsException() {
            // given
            CardCreateByUserCommand command = new CardCreateByUserCommand(1L, null, 175, 70, "캐주얼", null);

            // when & then
            assertThatThrownBy(() -> Card.createByUser(command))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.TARGET_GENDER_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("height가 null이면 예외를 발생시킨다")
        void createByUser_HeightNull_ThrowsException() {
            // given
            CardCreateByUserCommand command =
                    new CardCreateByUserCommand(1L, TargetGender.MEN, null, 70, "캐주얼", null);

            // when & then
            assertThatThrownBy(() -> Card.createByUser(command))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.HEIGHT_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("weight가 null이면 예외를 발생시킨다")
        void createByUser_WeightNull_ThrowsException() {
            // given
            CardCreateByUserCommand command =
                    new CardCreateByUserCommand(1L, TargetGender.MEN, 175, null, "캐주얼", null);

            // when & then
            assertThatThrownBy(() -> Card.createByUser(command))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.WEIGHT_IS_REQUIRED.message());
        }
    }
}
