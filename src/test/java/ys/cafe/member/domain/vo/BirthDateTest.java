package ys.cafe.member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ys.cafe.member.exception.MemberValidationException;
import ys.cafe.member.exception.errorcode.MemberValidationErrorCode;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BirthDate Value Object 테스트")
class BirthDateTest {

    @Nested
    @DisplayName("BirthDate 생성 성공 테스트")
    class CreateBirthDateSuccessTest {

        @Test
        @DisplayName("String으로 BirthDate를 생성할 수 있다")
        void createBirthDateFromString() {
            // given
            String dateString = "1990-01-01";

            // when
            BirthDate birthDate = BirthDate.of(dateString);

            // then
            assertThat(birthDate).isNotNull();
            assertThat(birthDate.getValue()).isEqualTo(LocalDate.of(1990, 1, 1));
        }

        @Test
        @DisplayName("LocalDate로 BirthDate를 생성할 수 있다")
        void createBirthDateFromLocalDate() {
            // given
            LocalDate date = LocalDate.of(1990, 1, 1);

            // when
            BirthDate birthDate = BirthDate.of(date);

            // then
            assertThat(birthDate).isNotNull();
            assertThat(birthDate.getValue()).isEqualTo(date);
        }

        @Test
        @DisplayName("년, 월, 일로 BirthDate를 생성할 수 있다")
        void createBirthDateFromYearMonthDay() {
            // given
            int year = 1990;
            int month = 1;
            int day = 1;

            // when
            BirthDate birthDate = BirthDate.of(year, month, day);

            // then
            assertThat(birthDate).isNotNull();
            assertThat(birthDate.getValue()).isEqualTo(LocalDate.of(year, month, day));
        }

    }

    @Nested
    @DisplayName("BirthDate 생성 실패 테스트 - BIRTH_DATE_REQUIRED")
    class CreateBirthDateFailWithRequiredTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("null 또는 빈 문자열로 생성 시 BIRTH_DATE_REQUIRED 예외가 발생한다")
        void createBirthDateWithNullOrBlank(String input) {
            // when & then
            assertThatThrownBy(() -> BirthDate.of(input))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_REQUIRED);
        }

        @Test
        @DisplayName("null LocalDate로 생성 시 BIRTH_DATE_REQUIRED 예외가 발생한다")
        void createBirthDateWithNullLocalDate() {
            // given
            LocalDate nullDate = null;

            // when & then
            assertThatThrownBy(() -> BirthDate.of(nullDate))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_REQUIRED);
        }
    }

    @Nested
    @DisplayName("BirthDate 생성 실패 테스트 - BIRTH_DATE_INVALID_FORMAT")
    class CreateBirthDateFailWithInvalidFormatTest {

        @ParameterizedTest
        @ValueSource(strings = {
                "1990/01/01",      // 슬래시 구분자
                "1990.01.01",      // 점 구분자
                "19900101",        // 구분자 없음
                "90-01-01",        // 2자리 년도
                "1990-1-1",        // 월/일 한자리
                "abcd-ef-gh"       // 문자
        })
        @DisplayName("잘못된 날짜 형식으로 생성 시 BIRTH_DATE_INVALID_FORMAT 예외가 발생한다")
        void createBirthDateWithInvalidFormat(String invalidDate) {
            // when & then
            assertThatThrownBy(() -> BirthDate.of(invalidDate))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_INVALID_FORMAT);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1990-13-01",      // 잘못된 월
                "1990-01-32",      // 잘못된 일
                "2023-02-30"       // 존재하지 않는 날짜
        })
        @DisplayName("형식은 맞지만 유효하지 않은 날짜로 생성 시 BIRTH_DATE_INVALID_FORMAT 예외가 발생한다 - String")
        void createBirthDateWithInvalidDate(String invalidDate) {
            // when & then
            assertThatThrownBy(() -> BirthDate.of(invalidDate))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_INVALID_FORMAT);
        }

        @Test
        @DisplayName("유효하지 않은 날짜로 생성 시 BIRTH_DATE_INVALID_FORMAT 예외가 발생한다 - year, month, day")
        void createBirthDateWithInvalidYearMonthDay() {
            // when & then
            assertThatThrownBy(() -> BirthDate.of(2023, 2, 30))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_INVALID_FORMAT);
        }
    }

    @Nested
    @DisplayName("BirthDate 생성 실패 테스트 - BIRTH_DATE_FUTURE")
    class CreateBirthDateFailWithFutureDateTest {

        @Test
        @DisplayName("미래 날짜로 생성 시 BIRTH_DATE_FUTURE 예외가 발생한다 - String")
        void createBirthDateWithFutureDateString() {
            // given
            String futureDate = LocalDate.now().plusDays(1).toString();

            // when & then
            assertThatThrownBy(() -> BirthDate.of(futureDate))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 현재 날짜보다 이후일 수 없습니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_FUTURE);
        }

        @Test
        @DisplayName("미래 날짜로 생성 시 BIRTH_DATE_FUTURE 예외가 발생한다 - LocalDate")
        void createBirthDateWithFutureDateLocalDate() {
            // given
            LocalDate futureDate = LocalDate.now().plusYears(1);

            // when & then
            assertThatThrownBy(() -> BirthDate.of(futureDate))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 현재 날짜보다 이후일 수 없습니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_FUTURE);
        }

        @Test
        @DisplayName("미래 날짜로 생성 시 BIRTH_DATE_FUTURE 예외가 발생한다 - year, month, day")
        void createBirthDateWithFutureDateYearMonthDay() {
            // given
            LocalDate futureDate = LocalDate.now().plusYears(10);

            // when & then
            assertThatThrownBy(() -> BirthDate.of(
                    futureDate.getYear(),
                    futureDate.getMonthValue(),
                    futureDate.getDayOfMonth()
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 현재 날짜보다 이후일 수 없습니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_FUTURE);
        }
    }

    @Nested
    @DisplayName("BirthDate 메서드 테스트")
    class BirthDateMethodTest {

        @Test
        @DisplayName("나이를 정확하게 계산할 수 있다")
        void calculateAge() {
            // given
            LocalDate birthDate = LocalDate.now().minusYears(30);
            BirthDate birth = BirthDate.of(birthDate);

            // when
            int age = birth.getAge();

            // then
            assertThat(age).isEqualTo(30);
        }

        @Test
        @DisplayName("생일이 지나지 않은 경우 나이를 정확하게 계산할 수 있다")
        void calculateAgeBeforeBirthday() {
            // given - 내년의 오늘보다 하루 뒤 날짜 (아직 생일이 안 지남)
            LocalDate now = LocalDate.now();
            LocalDate birthDate = now.minusYears(25).plusDays(1);
            BirthDate birth = BirthDate.of(birthDate);

            // when
            int age = birth.getAge();

            // then
            assertThat(age).isEqualTo(24); // 생일이 지나지 않아서 24세
        }

        @Test
        @DisplayName("getValue()로 LocalDate를 가져올 수 있다")
        void getValue() {
            // given
            LocalDate date = LocalDate.of(1990, 5, 15);
            BirthDate birthDate = BirthDate.of(date);

            // when
            LocalDate value = birthDate.getValue();

            // then
            assertThat(value).isEqualTo(date);
        }

        @Test
        @DisplayName("toString()이 yyyy-MM-dd 형식으로 반환된다")
        void testToString() {
            // given
            BirthDate birthDate = BirthDate.of("1990-05-15");

            // when
            String result = birthDate.toString();

            // then
            assertThat(result).isEqualTo("1990-05-15");
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("매우 오래된 날짜로 생성할 수 있다 (1900년대)")
        void createBirthDateWithVeryOldDate() {
            // given
            String oldDate = "1900-01-01";

            // when
            BirthDate birthDate = BirthDate.of(oldDate);

            // then
            assertThat(birthDate).isNotNull();
            assertThat(birthDate.getValue()).isEqualTo(LocalDate.of(1900, 1, 1));
            assertThat(birthDate.getAge()).isGreaterThan(120);
        }

        @Test
        @DisplayName("윤년의 2월 29일로 생성할 수 있다")
        void createBirthDateWithLeapYear() {
            // given
            String leapDate = "2000-02-29";

            // when
            BirthDate birthDate = BirthDate.of(leapDate);

            // then
            assertThat(birthDate).isNotNull();
            assertThat(birthDate.getValue()).isEqualTo(LocalDate.of(2000, 2, 29));
        }

    }
}