package ys.cafe.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ys.cafe.member.exception.MemberValidationException;
import ys.cafe.member.exception.errorcode.MemberValidationErrorCode;

/**
 * 전화번호 Value Object
 * DB에는 숫자만 저장, 표시는 포맷팅
 */
@Embeddable
public class PhoneNumber {

    @Column(name = "phone_number", nullable = false, unique = true, length = 11)
    private String value;

    protected PhoneNumber() {
    }

    private PhoneNumber(String input) {
        if (input == null || input.isBlank()) {
            throw new MemberValidationException(MemberValidationErrorCode.PHONE_NUMBER_REQUIRED);
        }

        String normalized = input.replaceAll("-", "").trim();

        if (!normalized.matches("^\\d{11}$")) {
            throw new MemberValidationException(MemberValidationErrorCode.PHONE_NUMBER_INVALID_FORMAT);
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