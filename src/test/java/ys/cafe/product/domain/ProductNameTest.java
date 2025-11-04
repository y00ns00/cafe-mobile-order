package ys.cafe.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ys.cafe.product.exception.ProductValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductNameTest {

    @Test
    @DisplayName("정상적인 상품명으로 ProductName을 생성할 수 있다")
    void createProductName() {
        // given
        String value = "아메리카노";

        // when
        ProductName productName = ProductName.of(value);

        // then
        assertThat(productName.getValue()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("앞뒤 공백이 있는 상품명은 trim되어 저장된다")
    void createProductNameWithWhitespace() {
        // given
        String value = "  아메리카노  ";

        // when
        ProductName productName = ProductName.of(value);

        // then
        assertThat(productName.getValue()).isEqualTo("아메리카노");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null이거나 빈 문자열로 ProductName을 생성하면 예외가 발생한다")
    void createProductNameWithNullOrEmpty(String value) {
        // when & then
        assertThatThrownBy(() -> ProductName.of(value))
                .isInstanceOf(ProductValidationException.class);
    }

    @Test
    @DisplayName("공백만 있는 문자열로 ProductName을 생성하면 예외가 발생한다")
    void createProductNameWithBlank() {
        // given
        String value = "   ";

        // when & then
        assertThatThrownBy(() -> ProductName.of(value))
                .isInstanceOf(ProductValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("1자 미만의 상품명은 예외가 발생한다")
    void createProductNameWithTooShort(String value) {
        // when & then
        assertThatThrownBy(() -> ProductName.of(value))
                .isInstanceOf(ProductValidationException.class);
    }

    @Test
    @DisplayName("100자를 초과하는 상품명은 예외가 발생한다")
    void createProductNameWithTooLong() {
        // given
        String value = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> ProductName.of(value))
                .isInstanceOf(ProductValidationException.class);
    }


    @Test
    @DisplayName("같은 값을 가진 ProductName은 동등하다")
    void equals() {
        // given
        ProductName name1 = ProductName.of("아메리카노");
        ProductName name2 = ProductName.of("아메리카노");

        // when & then
        assertThat(name1).isEqualTo(name2);
        assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 ProductName은 동등하지 않다")
    void notEquals() {
        // given
        ProductName name1 = ProductName.of("아메리카노");
        ProductName name2 = ProductName.of("카페라떼");

        // when & then
        assertThat(name1).isNotEqualTo(name2);
    }

    @Test
    @DisplayName("toString은 상품명 값을 반환한다")
    void toStringTest() {
        // given
        ProductName productName = ProductName.of("아메리카노");

        // when
        String result = productName.toString();

        // then
        assertThat(result).isEqualTo("아메리카노");
    }
}
