package ys.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ys.product.exception.ProductValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageUrlTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com/image.jpg",
            "https://example.com/image.png",
            "https://cdn.example.com/images/product/1234.jpg",
            "http://localhost:8080/images/test.png"
    })
    @DisplayName("정상적인 URL로 ImageUrl을 생성할 수 있다")
    void createImageUrl(String url) {
        // when
        ImageUrl imageUrl = ImageUrl.of(url);

        // then
        assertThat(imageUrl.getUrl()).isEqualTo(url);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null이거나 빈 문자열로 ImageUrl을 생성하면 예외가 발생한다")
    void createImageUrlWithNullOrEmpty(String url) {
        // when & then
        assertThatThrownBy(() -> ImageUrl.of(url))
                .isInstanceOf(ProductValidationException.class);
    }

    @Test
    @DisplayName("공백만 있는 문자열로 ImageUrl을 생성하면 예외가 발생한다")
    void createImageUrlWithBlank() {
        // given
        String url = "   ";

        // when & then
        assertThatThrownBy(() -> ImageUrl.of(url))
                .isInstanceOf(ProductValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-url",
            "not a url",
            "htp://wrong-protocol.com",
            "://missing-protocol.com",
            "example.com/image.jpg"  // 프로토콜 없음
    })
    @DisplayName("올바르지 않은 URL 형식은 예외가 발생한다")
    void createImageUrlWithInvalidFormat(String url) {
        // when & then
        assertThatThrownBy(() -> ImageUrl.of(url))
                .isInstanceOf(ProductValidationException.class);
    }

    @Test
    @DisplayName("같은 URL을 가진 ImageUrl은 동등하다")
    void equals() {
        // given
        ImageUrl url1 = ImageUrl.of("http://example.com/image.jpg");
        ImageUrl url2 = ImageUrl.of("http://example.com/image.jpg");

        // when & then
        assertThat(url1).isEqualTo(url2);
        assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
    }

    @Test
    @DisplayName("다른 URL을 가진 ImageUrl은 동등하지 않다")
    void notEquals() {
        // given
        ImageUrl url1 = ImageUrl.of("http://example.com/image1.jpg");
        ImageUrl url2 = ImageUrl.of("http://example.com/image2.jpg");

        // when & then
        assertThat(url1).isNotEqualTo(url2);
    }

    @Test
    @DisplayName("toString은 URL 값을 반환한다")
    void toStringTest() {
        // given
        String url = "http://example.com/image.jpg";
        ImageUrl imageUrl = ImageUrl.of(url);

        // when
        String result = imageUrl.toString();

        // then
        assertThat(result).isEqualTo(url);
    }
}
