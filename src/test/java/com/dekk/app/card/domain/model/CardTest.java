package com.dekk.app.card.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dekk.app.card.domain.exception.CardBusinessException;
import com.dekk.app.card.domain.exception.CardErrorCode;
import com.dekk.app.card.domain.model.enums.CardStatus;
import com.dekk.app.card.domain.model.enums.TargetGender;
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
            Long resourceId = 1L;
            TargetGender targetGender = TargetGender.MEN;
            Integer height = 175;
            Integer weight = 70;
            String tags = "캐주얼,데일리";

            // when
            Card card = Card.createByUser(resourceId, targetGender, height, weight, tags);

            // then
            assertThat(card).isNotNull();
            assertThat(card.getPublicId()).isNotNull();
            assertThat(card.getResourceId()).isEqualTo(resourceId);
            assertThat(card.getTargetGender()).isEqualTo(targetGender);
            assertThat(card.getHeight()).isEqualTo(height);
            assertThat(card.getWeight()).isEqualTo(weight);
            assertThat(card.getTags()).isEqualTo(tags);
            assertThat(card.getStatus()).isEqualTo(CardStatus.PENDING);

            assertThat(card.getOriginId()).isNull();
            assertThat(card.getPlatform()).isNull();
            assertThat(card.getCardImage()).isNull();
        }

        @Test
        @DisplayName("tags가 null이어도 카드를 생성한다")
        void createByUser_TagsNull_Success() {
            // given
            Long resourceId = 1L;
            TargetGender targetGender = TargetGender.WOMEN;
            Integer height = 165;
            Integer weight = 55;
            String tags = null;

            // when
            Card card = Card.createByUser(resourceId, targetGender, height, weight, tags);

            // then
            assertThat(card).isNotNull();
            assertThat(card.getTags()).isNull();
        }

        @Test
        @DisplayName("resourceId가 null이면 예외를 발생시킨다")
        void createByUser_ResourceIdNull_ThrowsException() {
            // given
            Long resourceId = null;
            TargetGender targetGender = TargetGender.MEN;
            Integer height = 175;
            Integer weight = 70;
            String tags = "캐주얼";

            // when & then
            assertThatThrownBy(() -> Card.createByUser(resourceId, targetGender, height, weight, tags))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.RESOURCE_ID_IS_REQUIRED_FOR_USER_CARD.message());
        }

        @Test
        @DisplayName("targetGender가 null이면 예외를 발생시킨다")
        void createByUser_TargetGenderNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            TargetGender targetGender = null;
            Integer height = 175;
            Integer weight = 70;
            String tags = "캐주얼";

            // when & then
            assertThatThrownBy(() -> Card.createByUser(resourceId, targetGender, height, weight, tags))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.TARGET_GENDER_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("height가 null이면 예외를 발생시킨다")
        void createByUser_HeightNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            TargetGender targetGender = TargetGender.MEN;
            Integer height = null;
            Integer weight = 70;
            String tags = "캐주얼";

            // when & then
            assertThatThrownBy(() -> Card.createByUser(resourceId, targetGender, height, weight, tags))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.HEIGHT_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("weight가 null이면 예외를 발생시킨다")
        void createByUser_WeightNull_ThrowsException() {
            // given
            Long resourceId = 1L;
            TargetGender targetGender = TargetGender.MEN;
            Integer height = 175;
            Integer weight = null;
            String tags = "캐주얼";

            // when & then
            assertThatThrownBy(() -> Card.createByUser(resourceId, targetGender, height, weight, tags))
                    .isInstanceOf(CardBusinessException.class)
                    .hasMessageContaining(CardErrorCode.WEIGHT_IS_REQUIRED.message());
        }
    }
}
