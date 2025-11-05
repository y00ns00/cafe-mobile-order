package ys.cafe.order.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ys.cafe.common.exception.MoneyValidationException;
import ys.cafe.common.exception.errorcode.MoneyValidationErrorCode;

import java.util.Objects;

/**
 * 수량을 표현하는 Value Object
 * 불변 객체로 설계되어 있으며, 1개 이상의 양수만 허용
 */
@Embeddable
public class Quantity {

    @Column(name = "quantity", nullable = false)
    private Integer value;

    protected Quantity() {
        // JPA 기본 생성자
    }

    private Quantity(Integer value) {
        validate(value);
        this.value = value;
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public static Quantity one() {
        return new Quantity(1);
    }

    private void validate(Integer value) {
        if (value == null) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_REQUIRED);
        }
        if (value < 1) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_INVALID);
        }
    }

    public Integer getValue() {
        return value;
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    public Quantity subtract(Quantity other) {
        int result = this.value - other.value;
        if (result < 1) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_INVALID);
        }
        return new Quantity(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity)) return false;
        Quantity quantity = (Quantity) o;
        return Objects.equals(value, quantity.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value + "개";
    }
}
