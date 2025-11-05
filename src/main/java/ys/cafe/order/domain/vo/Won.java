package ys.cafe.order.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import ys.cafe.common.exception.MoneyValidationException;
import ys.cafe.common.exception.errorcode.MoneyValidationErrorCode;


import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Won {

    @Column(name = "price", nullable = false, precision = 19, scale = 0)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    protected Won() {
        // JPA 기본 생성자
    }

    private Won(BigDecimal amount) {
        validate(amount);
        this.amount = amount;
        this.currency = Currency.KRW;
    }

    public static Won of(long amount) {
        return new Won(BigDecimal.valueOf(amount));
    }

    public static Won of(BigDecimal amount) {
        return new Won(amount);
    }

    public static Won of(String amount) {
        if (amount == null || amount.isBlank()) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_REQUIRED);
        }
        try {
            BigDecimal parsedAmount = new BigDecimal(amount);
            return new Won(parsedAmount);
        } catch (NumberFormatException e) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_INVALID);
        }
    }

    public static Won zero() {
        return new Won(BigDecimal.ZERO);
    }

    private void validate(BigDecimal amount) {
        if (amount == null) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_REQUIRED);
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_NEGATIVE);
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Won add(Won other) {
        return new Won(this.amount.add(other.amount));
    }

    public Won subtract(Won other) {
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_NEGATIVE);
        }
        return new Won(result);
    }

    public Won multiply(int factor) {
        if (factor < 0) {
            throw new MoneyValidationException(MoneyValidationErrorCode.MONEY_INVALID);
        }
        return new Won(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Won)) return false;
        Won won = (Won) o;
        return amount.compareTo(won.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return amount + "원";
    }

    public String toPlainString() {
        return amount.toPlainString();
    }
}
