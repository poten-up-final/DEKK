package com.dekk.app.resource.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dekk.app.resource.domain.exception.ResourceBusinessException;
import com.dekk.app.resource.domain.exception.ResourceErrorCode;
import com.dekk.app.resource.domain.model.enums.ResourceStatus;
import com.dekk.app.resource.domain.model.enums.ResourceType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Resource 도메인 테스트")
class ResourceTest {

    @Nested
    class CreatePendingTest {

        @Test
        @DisplayName("정상적인 파라미터로 PENDING 상태의 CARD_IMAGE Resource를 생성한다")
        void createPending_CardImage_Success() {
            // given
            ResourceType resourceType = ResourceType.CARD_IMAGE;
            String originalKey = "original/card-images/test.jpg";
            String originalFileName = "my-photo.jpg";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when
            Resource resource = Resource.createPending(resourceType, originalKey, originalFileName, expiresAt);

            // then
            assertThat(resource).isNotNull();
            assertThat(resource.getPublicId()).isNotNull();
            assertThat(resource.getResourceType()).isEqualTo(ResourceType.CARD_IMAGE);
            assertThat(resource.getStatus()).isEqualTo(ResourceStatus.PENDING);
            assertThat(resource.getOriginalKey()).isEqualTo(originalKey);
            assertThat(resource.getOriginalFileName()).isEqualTo(originalFileName);
            assertThat(resource.getExpiresAt()).isEqualTo(expiresAt);
            assertThat(resource.getImageUrl()).isNull();
            assertThat(resource.getProcessedKey()).isNull();
            assertThat(resource.getContentType()).isNull();
            assertThat(resource.getFileSize()).isNull();
        }

        @Test
        @DisplayName("정상적인 파라미터로 PENDING 상태의 PRODUCT_IMAGE Resource를 생성한다")
        void createPending_ProductImage_Success() {
            // given
            ResourceType resourceType = ResourceType.PRODUCT_IMAGE;
            String originalKey = "original/product-images/test.jpg";
            String originalFileName = "product-photo.jpg";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when
            Resource resource = Resource.createPending(resourceType, originalKey, originalFileName, expiresAt);

            // then
            assertThat(resource).isNotNull();
            assertThat(resource.getResourceType()).isEqualTo(ResourceType.PRODUCT_IMAGE);
            assertThat(resource.getStatus()).isEqualTo(ResourceStatus.PENDING);
        }

        @Test
        @DisplayName("resourceType이 null이면 예외를 발생시킨다")
        void createPending_ResourceTypeNull_ThrowsException() {
            // given
            ResourceType resourceType = null;
            String originalKey = "original/card-images/test.jpg";
            String originalFileName = "my-photo.jpg";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when & then
            assertThatThrownBy(() -> Resource.createPending(resourceType, originalKey, originalFileName, expiresAt))
                    .isInstanceOf(ResourceBusinessException.class)
                    .hasMessageContaining(ResourceErrorCode.RESOURCE_TYPE_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("originalKey가 null이면 예외를 발생시킨다")
        void createPending_OriginalKeyNull_ThrowsException() {
            // given
            ResourceType resourceType = ResourceType.CARD_IMAGE;
            String originalKey = null;
            String originalFileName = "my-photo.jpg";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when & then
            assertThatThrownBy(() -> Resource.createPending(resourceType, originalKey, originalFileName, expiresAt))
                    .isInstanceOf(ResourceBusinessException.class)
                    .hasMessageContaining(ResourceErrorCode.ORIGINAL_KEY_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("originalKey가 빈 문자열이면 예외를 발생시킨다")
        void createPending_OriginalKeyBlank_ThrowsException() {
            // given
            ResourceType resourceType = ResourceType.CARD_IMAGE;
            String originalKey = "   ";
            String originalFileName = "my-photo.jpg";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when & then
            assertThatThrownBy(() -> Resource.createPending(resourceType, originalKey, originalFileName, expiresAt))
                    .isInstanceOf(ResourceBusinessException.class)
                    .hasMessageContaining(ResourceErrorCode.ORIGINAL_KEY_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("originalFileName이 null이면 예외를 발생시킨다")
        void createPending_OriginalFileNameNull_ThrowsException() {
            // given
            ResourceType resourceType = ResourceType.CARD_IMAGE;
            String originalKey = "original/card-images/test.jpg";
            String originalFileName = null;
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when & then
            assertThatThrownBy(() -> Resource.createPending(resourceType, originalKey, originalFileName, expiresAt))
                    .isInstanceOf(ResourceBusinessException.class)
                    .hasMessageContaining(ResourceErrorCode.ORIGINAL_FILE_NAME_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("originalFileName이 빈 문자열이면 예외를 발생시킨다")
        void createPending_OriginalFileNameBlank_ThrowsException() {
            // given
            ResourceType resourceType = ResourceType.CARD_IMAGE;
            String originalKey = "original/card-images/test.jpg";
            String originalFileName = "";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

            // when & then
            assertThatThrownBy(() -> Resource.createPending(resourceType, originalKey, originalFileName, expiresAt))
                    .isInstanceOf(ResourceBusinessException.class)
                    .hasMessageContaining(ResourceErrorCode.ORIGINAL_FILE_NAME_IS_REQUIRED.message());
        }

        @Test
        @DisplayName("expiresAt이 null이면 예외를 발생시킨다")
        void createPending_ExpiresAtNull_ThrowsException() {
            // given
            ResourceType resourceType = ResourceType.CARD_IMAGE;
            String originalKey = "original/card-images/test.jpg";
            String originalFileName = "my-photo.jpg";
            LocalDateTime expiresAt = null;

            // when & then
            assertThatThrownBy(() -> Resource.createPending(resourceType, originalKey, originalFileName, expiresAt))
                    .isInstanceOf(ResourceBusinessException.class)
                    .hasMessageContaining(ResourceErrorCode.EXPIRES_AT_IS_REQUIRED.message());
        }
    }

    @Nested
    class MarkAsProcessedTest {

        @Test
        @DisplayName("리소스를 PROCESSED 상태로 변경하고 이미지 정보를 저장한다")
        void markAsProcessed_Success() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));
            String processedKey = "card-images/550e8400.webp";
            String imageUrl = "https://cdn.dekk.com/card-images/550e8400.webp";
            Long fileSize = 102400L;
            String contentType = "image/webp";

            // when
            resource.markAsProcessed(processedKey, imageUrl, fileSize, contentType);

            // then
            assertThat(resource.getStatus()).isEqualTo(ResourceStatus.PROCESSED);
            assertThat(resource.getProcessedKey()).isEqualTo(processedKey);
            assertThat(resource.getImageUrl()).isEqualTo(imageUrl);
            assertThat(resource.getFileSize()).isEqualTo(fileSize);
            assertThat(resource.getContentType()).isEqualTo(contentType);
        }
    }

    @Nested
    class MarkAsFailedTest {

        @Test
        @DisplayName("리소스를 FAILED 상태로 변경한다")
        void markAsFailed_Success() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));

            // when
            resource.markAsFailed();

            // then
            assertThat(resource.getStatus()).isEqualTo(ResourceStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("markAsExpired 메서드는")
    class MarkAsExpiredTest {

        @Test
        @DisplayName("리소스를 EXPIRED 상태로 변경한다")
        void markAsExpired_Success() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));

            // when
            resource.markAsExpired();

            // then
            assertThat(resource.getStatus()).isEqualTo(ResourceStatus.EXPIRED);
        }
    }

    @Nested
    class IsProcessedTest {

        @Test
        @DisplayName("PROCESSED 상태일 때 true를 반환한다")
        void isProcessed_WhenProcessed_ReturnsTrue() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));
            resource.markAsProcessed("card-images/test.webp", "https://cdn.dekk.com/test.webp", 100L, "image/webp");

            // when & then
            assertThat(resource.isProcessed()).isTrue();
        }

        @Test
        @DisplayName("PENDING 상태일 때 false를 반환한다")
        void isProcessed_WhenPending_ReturnsFalse() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));

            // when & then
            assertThat(resource.isProcessed()).isFalse();
        }
    }

    @Nested
    class CanBeUsedTest {

        @Test
        @DisplayName("PROCESSED 상태일 때 true를 반환한다")
        void canBeUsed_WhenProcessed_ReturnsTrue() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));
            resource.markAsProcessed("card-images/test.webp", "https://cdn.dekk.com/test.webp", 100L, "image/webp");

            // when & then
            assertThat(resource.canBeUsed()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태일 때 false를 반환한다")
        void canBeUsed_WhenFailed_ReturnsFalse() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));
            resource.markAsFailed();

            // when & then
            assertThat(resource.canBeUsed()).isFalse();
        }
    }

    @Nested
    @DisplayName("타입 확인 메서드는")
    class TypeCheckTest {

        @Test
        @DisplayName("CARD_IMAGE 타입일 때 isCardImage()가 true를 반환한다")
        void isCardImage_WhenCardImage_ReturnsTrue() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.CARD_IMAGE,
                    "original/card-images/test.jpg",
                    "my-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));

            // when & then
            assertThat(resource.isCardImage()).isTrue();
            assertThat(resource.isProductImage()).isFalse();
        }

        @Test
        @DisplayName("PRODUCT_IMAGE 타입일 때 isProductImage()가 true를 반환한다")
        void isProductImage_WhenProductImage_ReturnsTrue() {
            // given
            Resource resource = Resource.createPending(
                    ResourceType.PRODUCT_IMAGE,
                    "original/product-images/test.jpg",
                    "product-photo.jpg",
                    LocalDateTime.now().plusMinutes(15));

            // when & then
            assertThat(resource.isProductImage()).isTrue();
            assertThat(resource.isCardImage()).isFalse();
        }
    }
}
