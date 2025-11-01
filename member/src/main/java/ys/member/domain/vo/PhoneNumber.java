package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * 전화번호 Value Object
 * DB에는 숫자만 저장, 표시는 포맷팅
 */
@Embeddable
public class PhoneNumber {

    @Column(name = "phone_number", nullable = false, unique = true, length = 11)
    private String value;

    protected PhoneNumber() {}

    private PhoneNumber(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }

        String normalized = input.replaceAll("-", "").trim();

        if (!normalized.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("전화번호는 11자리 숫자여야 합니다. (예: 010-1234-5678 또는 01012345678)");
        }

        this.value = normalized;
    }

    public static PhoneNumber of(String phoneNumber) {
        return new PhoneNumber(phoneNumber);
    }

    public String getValue() {
        return value;
    }

    public String getFormatted() {
        if (value == null) {
            return "";
        }

        if (value.length() == 11) {
            // 휴대폰 번호 (010-1234-5678)
            return value.substring(0, 3) + "-"
                 + value.substring(3, 7) + "-"
                 + value.substring(7);
        }

        return value;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}