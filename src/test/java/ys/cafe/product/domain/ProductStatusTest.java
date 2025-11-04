package ys.cafe.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ys.cafe.product.exception.ProductValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductStatusTest {

    @ParameterizedTest
    @CsvSource({
            "AVAILABLE, AVAILABLE",
            "SOLD_OUT, SOLD_OUT",
            "HIDDEN, HIDDEN",
            "DISCONTINUED, DISCONTINUED"
    })
    @DisplayName("유효한 문자열로 ProductStatus를 생성할 수 있다")
    void fromString(String input, ProductStatus expected) {
        // when
        ProductStatus status = ProductStatus.fromString(input);

        // then
        assertThat(status).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"available", "Available", "AVAILABLE", "aVaIlAbLe"})
    @DisplayName("대소문자 구분 없이 ProductStatus를 생성할 수 있다")
    void fromStringCaseInsensitive(String input) {
        // when
        ProductStatus status = ProductStatus.fromString(input);

        // then
        assertThat(status).isEqualTo(ProductStatus.AVAILABLE);
    }

    @Test
    @DisplayName("null로 ProductStatus를 생성하면 예외가 발생한다")
    void fromStringWithNull() {
        // when & then
        assertThatThrownBy(() -> ProductStatus.fromString(null))
                .isInstanceOf(ProductValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "UNKNOWN", "TEST", ""})
    @DisplayName("유효하지 않은 문자열로 ProductStatus를 생성하면 예외가 발생한다")
    void fromStringWithInvalid(String input) {
        // when & then
        assertThatThrownBy(() -> ProductStatus.fromString(input))
                .isInstanceOf(ProductValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"AVAILABLE", "SOLD_OUT", "HIDDEN", "DISCONTINUED"})
    @DisplayName("유효한 문자열이면 isValid가 true를 반환한다")
    void isValidWithValidStatus(String status) {
        // when
        boolean result = ProductStatus.isValid(status);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"available", "sold_out", "hidden", "discontinued"})
    @DisplayName("소문자로 된 유효한 문자열도 isValid가 true를 반환한다")
    void isValidCaseInsensitive(String status) {
        // when
        boolean result = ProductStatus.isValid(status);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "UNKNOWN", "TEST", ""})
    @DisplayName("유효하지 않은 문자열이면 isValid가 false를 반환한다")
    void isValidWithInvalidStatus(String status) {
        // when
        boolean result = ProductStatus.isValid(status);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("null이면 isValid가 false를 반환한다")
    void isValidWithNull() {
        // when
        boolean result = ProductStatus.isValid(null);

        // then
        assertThat(result).isFalse();
    }
}
