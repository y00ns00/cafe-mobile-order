package ys.member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ys.member.exception.MemberValidationException;
import ys.member.exception.errorcode.MemberValidationErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PhoneNumber Value Object 테스트")
class PhoneNumberTest {

    @Nested
    @DisplayName("PhoneNumber 생성 성공 테스트")
    class CreatePhoneNumberSuccessTest {

        @Test
        @DisplayName("유효한 11자리 숫자로 전화번호를 생성할 수 있다")
        void createPhoneNumberWithValidInput() {
            // given
            String phoneNumber = "01012345678";

            // when
            PhoneNumber result = PhoneNumber.of(phoneNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("하이픈으로 구분된 전화번호로 생성할 수 있다")
        void createPhoneNumberWithHyphens() {
            // given
            String phoneNumber = "010-1234-5678";

            // when
            PhoneNumber result = PhoneNumber.of(phoneNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("앞뒤 공백이 있는 전화번호로 생성할 수 있다")
        void createPhoneNumberWithSpaces() {
            // given
            String phoneNumber = "  010-1234-5678  ";

            // when
            PhoneNumber result = PhoneNumber.of(phoneNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).isEqualTo("01012345678");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "01012345678",
                "010-1234-5678",
                "010-9999-9999",
                "01011111111"
        })
        @DisplayName("다양한 형식의 유효한 전화번호로 생성할 수 있다")
        void createPhoneNumberWithVariousFormats(String phoneNumber) {
            // when
            PhoneNumber result = PhoneNumber.of(phoneNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).hasSize(11);
        }
    }

    @Nested
    @DisplayName("PhoneNumber 생성 실패 테스트 - PHONE_NUMBER_REQUIRED")
    class CreatePhoneNumberFailWithRequiredTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n", "   "})
        @DisplayName("전화번호가 null 또는 빈 문자열인 경우 PHONE_NUMBER_REQUIRED 예외가 발생한다")
        void createPhoneNumberWithNullOrBlank(String phoneNumber) {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호는 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PHONE_NUMBER_REQUIRED);
        }
    }

    @Nested
    @DisplayName("PhoneNumber 생성 실패 테스트 - PHONE_NUMBER_INVALID_FORMAT")
    class CreatePhoneNumberFailWithInvalidFormatTest {

        @ParameterizedTest
        @ValueSource(strings = {
                "0101234567",      // 10자리
                "010123456789",    // 12자리
                "1234567890",      // 11자리지만 010으로 시작하지 않음
                "010-1234-567",    // 하이픈 제거 시 10자리
                "010-1234-56789"   // 하이픈 제거 시 12자리
        })
        @DisplayName("11자리가 아닌 전화번호는 PHONE_NUMBER_INVALID_FORMAT 예외가 발생한다")
        void createPhoneNumberWithInvalidLength(String phoneNumber) {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PHONE_NUMBER_INVALID_FORMAT);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "010-abcd-5678",
                "010-1234-abcd",
                "abcdefghijk",
                "010123456ab",
                "010-1234-567a",
                "가나다라마바사아자차",
                "010-🔒🔒🔒🔒-5678"
        })
        @DisplayName("숫자가 아닌 문자가 포함된 경우 PHONE_NUMBER_INVALID_FORMAT 예외가 발생한다")
        void createPhoneNumberWithNonDigits(String phoneNumber) {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PHONE_NUMBER_INVALID_FORMAT);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "010 1234 5678",   // 공백으로 구분
                "010.1234.5678",   // 점으로 구분
                "010_1234_5678"    // 언더스코어로 구분
        })
        @DisplayName("하이픈이 아닌 구분자가 포함된 경우 PHONE_NUMBER_INVALID_FORMAT 예외가 발생한다")
        void createPhoneNumberWithInvalidSeparator(String phoneNumber) {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PHONE_NUMBER_INVALID_FORMAT);
        }
    }

    @Nested
    @DisplayName("PhoneNumber getValue() 테스트")
    class GetValueTest {

        @Test
        @DisplayName("getValue()는 하이픈이 제거된 11자리 숫자를 반환한다")
        void getValueReturnsNormalizedPhoneNumber() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            // when
            String value = phoneNumber.getValue();

            // then
            assertThat(value).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("getValue()는 원본 입력과 관계없이 정규화된 값을 반환한다")
        void getValueReturnsNormalizedValue() {
            // given
            PhoneNumber phoneNumber1 = PhoneNumber.of("010-1234-5678");
            PhoneNumber phoneNumber2 = PhoneNumber.of("01012345678");
            PhoneNumber phoneNumber3 = PhoneNumber.of("  010-1234-5678  ");

            // when & then
            assertThat(phoneNumber1.getValue()).isEqualTo("01012345678");
            assertThat(phoneNumber2.getValue()).isEqualTo("01012345678");
            assertThat(phoneNumber3.getValue()).isEqualTo("01012345678");
        }
    }

    @Nested
    @DisplayName("PhoneNumber getFormatted() 테스트")
    class GetFormattedTest {

        @Test
        @DisplayName("getFormatted()는 010-1234-5678 형식으로 반환한다")
        void getFormattedReturnsFormattedPhoneNumber() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("01012345678");

            // when
            String formatted = phoneNumber.getFormatted();

            // then
            assertThat(formatted).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("하이픈으로 입력해도 getFormatted()는 동일한 형식을 반환한다")
        void getFormattedReturnsConsistentFormat() {
            // given
            PhoneNumber phoneNumber1 = PhoneNumber.of("010-1234-5678");
            PhoneNumber phoneNumber2 = PhoneNumber.of("01012345678");

            // when & then
            assertThat(phoneNumber1.getFormatted()).isEqualTo("010-1234-5678");
            assertThat(phoneNumber2.getFormatted()).isEqualTo("010-1234-5678");
            assertThat(phoneNumber1.getFormatted()).isEqualTo(phoneNumber2.getFormatted());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "010-1111-1111",
                "010-9999-9999",
                "010-0000-0000"
        })
        @DisplayName("다양한 전화번호가 올바르게 포맷팅된다")
        void getFormattedWorksForVariousPhoneNumbers(String input) {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of(input);

            // when
            String formatted = phoneNumber.getFormatted();

            // then
            assertThat(formatted).matches("^010-\\d{4}-\\d{4}$");
        }
    }

    @Nested
    @DisplayName("PhoneNumber toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()은 포맷팅된 전화번호를 반환한다")
        void toStringReturnsFormattedPhoneNumber() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("01012345678");

            // when
            String result = phoneNumber.toString();

            // then
            assertThat(result).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("toString()과 getFormatted()는 동일한 결과를 반환한다")
        void toStringAndGetFormattedReturnSameResult() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("010-9876-5432");

            // when & then
            assertThat(phoneNumber.toString()).isEqualTo(phoneNumber.getFormatted());
        }
    }

    @Nested
    @DisplayName("PhoneNumber 통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("회원가입 시나리오: 전화번호 입력 및 저장")
        void signUpScenario() {
            // given - 사용자가 하이픈 포함하여 입력
            String userInput = "010-1234-5678";

            // when - 전화번호 생성
            PhoneNumber phoneNumber = PhoneNumber.of(userInput);

            // then - 저장 및 표시
            assertThat(phoneNumber.getValue()).isEqualTo("01012345678"); // DB 저장용
            assertThat(phoneNumber.getFormatted()).isEqualTo("010-1234-5678"); // 화면 표시용
        }

        @Test
        @DisplayName("전화번호 검증 시나리오: 다양한 입력 형식 처리")
        void phoneNumberValidationScenario() {
            // given - 다양한 형식의 입력
            String input1 = "01012345678";
            String input2 = "010-1234-5678";
            String input3 = "  010-1234-5678  ";

            // when - 전화번호 생성
            PhoneNumber phoneNumber1 = PhoneNumber.of(input1);
            PhoneNumber phoneNumber2 = PhoneNumber.of(input2);
            PhoneNumber phoneNumber3 = PhoneNumber.of(input3);

            // then - 모두 동일하게 정규화됨
            assertThat(phoneNumber1.getValue()).isEqualTo("01012345678");
            assertThat(phoneNumber2.getValue()).isEqualTo("01012345678");
            assertThat(phoneNumber3.getValue()).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("잘못된 전화번호 입력 시나리오")
        void invalidPhoneNumberScenario() {
            // given - 잘못된 형식의 전화번호들
            String tooShort = "0101234567";
            String tooLong = "010123456789";
            String withLetters = "010-abcd-5678";

            // when & then - 모두 예외 발생
            assertThatThrownBy(() -> PhoneNumber.of(tooShort))
                    .isInstanceOf(MemberValidationException.class);
            assertThatThrownBy(() -> PhoneNumber.of(tooLong))
                    .isInstanceOf(MemberValidationException.class);
            assertThatThrownBy(() -> PhoneNumber.of(withLetters))
                    .isInstanceOf(MemberValidationException.class);
        }
    }

    @Nested
    @DisplayName("경계값 및 특수 케이스 테스트")
    class BoundaryAndSpecialCaseTest {

        @Test
        @DisplayName("정확히 11자리 숫자는 생성할 수 있다")
        void canCreatePhoneNumberWithExactly11Digits() {
            // given
            String phoneNumber = "01012345678";

            // when
            PhoneNumber result = PhoneNumber.of(phoneNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).hasSize(11);
        }

        @Test
        @DisplayName("10자리 숫자는 생성할 수 없다")
        void cannotCreatePhoneNumberWith10Digits() {
            // given
            String phoneNumber = "0101234567";

            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)");
        }

        @Test
        @DisplayName("12자리 숫자는 생성할 수 없다")
        void cannotCreatePhoneNumberWith12Digits() {
            // given
            String phoneNumber = "010123456789";

            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)");
        }

        @Test
        @DisplayName("하이픈만 있는 경우 생성할 수 없다")
        void cannotCreatePhoneNumberWithOnlyHyphens() {
            // given
            String phoneNumber = "---";

            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(phoneNumber))
                    .isInstanceOf(MemberValidationException.class);
        }

        @Test
        @DisplayName("여러 개의 하이픈이 포함된 경우에도 정규화된다")
        void normalizePhoneNumberWithMultipleHyphens() {
            // given
            String phoneNumber = "0-1-0-1-2-3-4-5-6-7-8";

            // when
            PhoneNumber result = PhoneNumber.of(phoneNumber);

            // then
            assertThat(result.getValue()).isEqualTo("01012345678");
        }
    }
}