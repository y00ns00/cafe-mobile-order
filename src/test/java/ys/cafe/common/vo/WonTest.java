package ys.cafe.common.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ys.cafe.common.exception.MoneyValidationException;
import ys.cafe.payment.domain.vo.Won;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WonTest {

    @Test
    @DisplayName("long 타입으로 Won을 생성할 수 있다")
    void createWonWithLong() {
        // given
        long amount = 5000L;

        // when
        Won won = Won.of(amount);

        // then
        assertThat(won.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    @DisplayName("BigDecimal 타입으로 Won을 생성할 수 있다")
    void createWonWithBigDecimal() {
        // given
        BigDecimal amount = new BigDecimal("10000");

        // when
        Won won = Won.of(amount);

        // then
        assertThat(won.getAmount()).isEqualByComparingTo(amount);
    }

    @Test
    @DisplayName("0원으로 Won을 생성할 수 있다")
    void createZeroWon() {
        // when
        Won won = Won.zero();

        // then
        assertThat(won.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("null로 Won을 생성하면 예외가 발생한다")
    void createWonWithNull() {
        // when & then
        assertThatThrownBy(() -> Won.of((BigDecimal) null))
                .isInstanceOf(MoneyValidationException.class);
    }

    @Test
    @DisplayName("음수로 Won을 생성하면 예외가 발생한다")
    void createWonWithNegative() {
        // given
        BigDecimal amount = new BigDecimal("-1000");

        // when & then
        assertThatThrownBy(() -> Won.of(amount))
                .isInstanceOf(MoneyValidationException.class);
    }

    @Test
    @DisplayName("Won끼리 더할 수 있다")
    void add() {
        // given
        Won won1 = Won.of(5000);
        Won won2 = Won.of(3000);

        // when
        Won result = won1.add(won2);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(8000));
    }

    @Test
    @DisplayName("Won끼리 뺄 수 있다")
    void subtract() {
        // given
        Won won1 = Won.of(5000);
        Won won2 = Won.of(3000);

        // when
        Won result = won1.subtract(won2);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    @DisplayName("뺄셈 결과가 음수이면 예외가 발생한다")
    void subtractWithNegativeResult() {
        // given
        Won won1 = Won.of(3000);
        Won won2 = Won.of(5000);

        // when & then
        assertThatThrownBy(() -> won1.subtract(won2))
                .isInstanceOf(MoneyValidationException.class);
    }

    @Test
    @DisplayName("Won에 정수를 곱할 수 있다")
    void multiply() {
        // given
        Won won = Won.of(1000);

        // when
        Won result = won.multiply(5);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    @DisplayName("Won에 0을 곱하면 0원이 된다")
    void multiplyByZero() {
        // given
        Won won = Won.of(1000);

        // when
        Won result = won.multiply(0);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Won에 음수를 곱하면 예외가 발생한다")
    void multiplyByNegative() {
        // given
        Won won = Won.of(1000);

        // when & then
        assertThatThrownBy(() -> won.multiply(-1))
                .isInstanceOf(MoneyValidationException.class);
    }

    @Test
    @DisplayName("같은 금액을 가진 Won은 동등하다")
    void equals() {
        // given
        Won won1 = Won.of(5000);
        Won won2 = Won.of(5000);

        // when & then
        assertThat(won1).isEqualTo(won2);
        assertThat(won1.hashCode()).isEqualTo(won2.hashCode());
    }

    @Test
    @DisplayName("BigDecimal의 스케일이 다르더라도 값이 같으면 동등하다")
    void equalsWithDifferentScale() {
        // given
        Won won1 = Won.of(new BigDecimal("5000.00"));
        Won won2 = Won.of(new BigDecimal("5000"));

        // when & then
        assertThat(won1).isEqualTo(won2);
    }

    @Test
    @DisplayName("다른 금액을 가진 Won은 동등하지 않다")
    void notEquals() {
        // given
        Won won1 = Won.of(5000);
        Won won2 = Won.of(3000);

        // when & then
        assertThat(won1).isNotEqualTo(won2);
    }

    @Test
    @DisplayName("toString은 금액과 '원'을 반환한다")
    void toStringTest() {
        // given
        Won won = Won.of(5000);

        // when
        String result = won.toString();

        // then
        assertThat(result).isEqualTo("5000원");
    }
}
