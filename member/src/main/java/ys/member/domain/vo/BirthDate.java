package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import ys.member.exception.MemberValidationException;
import ys.member.exception.errorcode.MemberValidationErrorCode;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 생년월일 Value Object
 */
@Embeddable
@Getter
public class BirthDate {

    @Column(name = "birth_date", nullable = false)
    private LocalDate value;

    protected BirthDate() {
    }

    private BirthDate(LocalDate value) {
        if (value == null) {
            throw new MemberValidationException(MemberValidationErrorCode.BIRTH_DATE_REQUIRED);
        }
        if (isFutureDate(value)) {
            throw new MemberValidationException(MemberValidationErrorCode.BIRTH_DATE_FUTURE);
        }
        this.value = value;
    }

    private BirthDate(String value) {
        if (isNullOrBlank(value)) {
            throw new MemberValidationException(MemberValidationErrorCode.BIRTH_DATE_REQUIRED);
        }
        if (isInvalidDateFormat(value)) {
            throw new MemberValidationException(MemberValidationErrorCode.BIRTH_DATE_INVALID_FORMAT);
        }
        LocalDate localDate = LocalDate.parse(value);
        if (isFutureDate(localDate)) {
            throw new MemberValidationException(MemberValidationErrorCode.BIRTH_DATE_FUTURE);
        }

        this.value = localDate;
    }

    private static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isFutureDate(LocalDate value) {
        return value.isAfter(LocalDate.now());
    }

    private static boolean isInvalidDateFormat(String value) {
        return !value.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    public static BirthDate of(String value) {
        return new BirthDate(value);
    }

    public static BirthDate of(LocalDate date) {
        return new BirthDate(date);
    }

    public static BirthDate of(int year, int month, int dayOfMonth) {
        return new BirthDate(LocalDate.of(year, month, dayOfMonth));
    }

    public int getAge() {
        return Period.between(value, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String getFormatted() {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}