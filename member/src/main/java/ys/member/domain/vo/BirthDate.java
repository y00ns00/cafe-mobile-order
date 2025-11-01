package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

/**
 * 생년월일 Value Object
 */
@Embeddable
@Getter
public class BirthDate {

    @Column(name = "birth_date", nullable = false)
    private LocalDate value;

    protected BirthDate() {}

    private BirthDate(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("생년월일은 필수입니다.");
        }
        if (isFutureDate(value)) {
            throw new IllegalArgumentException("생년월일은 현재 날짜보다 이후일 수 없습니다.");
        }
        this.value = value;
    }

    private BirthDate(String value) {
        if (isNullOrBlank(value)) {
            throw new IllegalArgumentException("생년월일은 필수입니다.");
        }
        if (isInvalidDateFormat(value)) {
            throw new IllegalArgumentException("생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)");
        }
        LocalDate localDate = LocalDate.parse(value);
        if (isFutureDate(localDate)) {
            throw new IllegalArgumentException("생년월일은 현재 날짜보다 이후일 수 없습니다.");
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
}