package ys.cafe.payment.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;

import java.util.Objects;

/**
 * 결제 키 Value Object
 * 결제를 고유하게 식별하는 키
 */
@Embeddable
public class PaymentKey {

    @Column(name = "payment_key", length = 100, nullable = false)
    private String value;

    protected PaymentKey() {
        // JPA 기본 생성자
    }

    private PaymentKey(String value) {
        if (value == null || value.isBlank()) {
            throw new CommonException(CommonErrorCode.BAD_REQUEST, "결제 키는 필수입니다.");
        }
        this.value = value.trim();
    }

    public static PaymentKey of(String value) {
        return new PaymentKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentKey)) return false;
        PaymentKey that = (PaymentKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
