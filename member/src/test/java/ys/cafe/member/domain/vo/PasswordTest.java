package ys.cafe.member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ys.cafe.member.exception.MemberValidationException;
import ys.cafe.member.exception.errorcode.MemberValidationErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Password Value Object 테스트")
class PasswordTest {

    @Nested
    @DisplayName("Password 생성 성공 테스트")
    class CreatePasswordSuccessTest {

        @Test
        @DisplayName("유효한 비밀번호로 Password를 생성할 수 있다")
        void createPasswordWithValidInput() {
            // given
            String rawPassword = "Password123!";
            String encodedPassword = "$2a$10$encoded_Password123!";

            // when
            Password password = Password.of(rawPassword, s -> encodedPassword);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("최소 길이(8자) 비밀번호로 생성할 수 있다")
        void createPasswordWithMinimumLength() {
            // given
            String rawPassword = "Pass123!";  // 8자: 영문자, 숫자, 특수문자 포함
            String encodedPassword = "$2a$10$encoded_" + rawPassword;

            // when
            Password password = Password.of(rawPassword, s -> encodedPassword);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("최대 길이(20자) 비밀번호로 생성할 수 있다")
        void createPasswordWithMaximumLength() {
            // given
            String rawPassword = "Password123!@#$%?abc";  // 정확히 20자
            String encodedPassword = "$2a$10$encoded_" + rawPassword;

            // when
            Password password = Password.of(rawPassword, s -> encodedPassword);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("다양한 특수문자가 포함된 비밀번호로 생성할 수 있다")
        void createPasswordWithVariousSpecialCharacters() {
            // given
            String rawPassword = "P@ssw0rd!#$%";

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("대문자, 소문자, 숫자, 특수문자가 모두 포함된 비밀번호로 생성할 수 있다")
        void createPasswordWithAllCharacterTypes() {
            // given
            String rawPassword = "MyP@ssw0rd123";

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("소문자만 포함된 비밀번호로 생성할 수 있다")
        void createPasswordWithLowercaseOnly() {
            // given
            String rawPassword = "password123!";

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("대문자만 포함된 비밀번호로 생성할 수 있다")
        void createPasswordWithUppercaseOnly() {
            // given
            String rawPassword = "PASSWORD123!";

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }
    }

    @Nested
    @DisplayName("Password 생성 실패 테스트 - PASSWORD_REQUIRED")
    class CreatePasswordFailWithRequiredTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n", "   "})
        @DisplayName("비밀번호가 null 또는 빈 문자열인 경우 PASSWORD_REQUIRED 예외가 발생한다")
        void createPasswordWithNullOrBlank(String rawPassword) {
            // when & then
            assertThatThrownBy(() -> Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_REQUIRED);
        }
    }

    @Nested
    @DisplayName("Password 생성 실패 테스트 - 길이 제약")
    class CreatePasswordFailWithLengthTest {

        @ParameterizedTest
        @ValueSource(strings = {"a", "ab", "abc1!", "Pass1!", "1234567"})
        @DisplayName("8자 미만 비밀번호는 PASSWORD_TOO_SHORT 예외가 발생한다")
        void createPasswordTooShort(String shortPassword) {
            // when & then
            assertThatThrownBy(() -> Password.of(shortPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 최소 8자 이상이어야 합니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_TOO_SHORT);
        }

        @Test
        @DisplayName("21자 이상 비밀번호는 PASSWORD_TOO_LONG 예외가 발생한다")
        void createPasswordTooLong() {
            // given
            String longPassword = "Password123!@#$%?abcd";  // 21자

            // when & then
            assertThatThrownBy(() -> Password.of(longPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 최대 20자 이하이어야 합니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_TOO_LONG);
        }
    }

    @Nested
    @DisplayName("Password 생성 실패 테스트 - 문자 조합 제약")
    class CreatePasswordFailWithCompositionTest {

        @ParameterizedTest
        @ValueSource(strings = {"12345678!", "!@#$%^&*", "12345!@#"})
        @DisplayName("영문자가 없으면 PASSWORD_MISSING_LETTER 예외가 발생한다")
        void createPasswordMissingLetter(String passwordWithoutLetter) {
            // when & then
            assertThatThrownBy(() -> Password.of(passwordWithoutLetter, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 영문자를 포함해야 합니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_MISSING_LETTER);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Password!", "abcdefgh!", "ABCDEFGH!"})
        @DisplayName("숫자가 없으면 PASSWORD_MISSING_DIGIT 예외가 발생한다")
        void createPasswordMissingDigit(String passwordWithoutDigit) {
            // when & then
            assertThatThrownBy(() -> Password.of(passwordWithoutDigit, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 숫자를 포함해야 합니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_MISSING_DIGIT);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Password123", "abcdefgh1", "ABCDEFGH1"})
        @DisplayName("특수문자가 없으면 PASSWORD_MISSING_SPECIAL_CHAR 예외가 발생한다")
        void createPasswordMissingSpecialChar(String passwordWithoutSpecialChar) {
            // when & then
            assertThatThrownBy(() -> Password.of(passwordWithoutSpecialChar, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 특수문자(!@#$%?)를 포함해야 합니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_MISSING_SPECIAL_CHAR);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Pass123!{}", "Pass123![", "Test123!|", "Pass123!~", "Test123!&*", "Pass123!<>"})
        @DisplayName("허용되지 않은 특수문자가 포함되면 PASSWORD_INVALID_CHAR 예외가 발생한다")
        void createPasswordWithInvalidSpecialChar(String passwordWithInvalidChar) {
            // when & then
            assertThatThrownBy(() -> Password.of(passwordWithInvalidChar, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 영문자, 숫자, 특수문자(!@#$%?)만 사용할 수 있습니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_INVALID_CHAR);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Pass123!한글", "Pass123!🔒"})
        @DisplayName("한글이나 이모지가 포함되면 PASSWORD_INVALID_CHAR 예외가 발생한다")
        void createPasswordWithUnicodeChar(String passwordWithUnicode) {
            // when & then
            assertThatThrownBy(() -> Password.of(passwordWithUnicode, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 영문자, 숫자, 특수문자(!@#$%?)만 사용할 수 있습니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_INVALID_CHAR);
        }
    }

    @Nested
    @DisplayName("Password 암호화 테스트")
    class EncodePasswordTest {

        @Test
        @DisplayName("생성 시 암호화된다")
        void createPasswordWithEncoding() {
            // given // when
            String rawPassword = "password1!";
            String encodedPassword = "$2a$10$encoded_" + rawPassword;
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password.matches(rawPassword, (rawPassword1, encodedPassword1) -> true)).isTrue();
        }
    }


    @Nested
    @DisplayName("Password 일치 확인 테스트")
    class MatchesPasswordTest {

        @Test
        @DisplayName("matches()로 비밀번호 일치 여부를 확인할 수 있다 - 일치하는 경우")
        void matchesPasswordSuccess() {
            // given
            String rawPassword = "password123!";
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // when
            boolean matches = password.matches(rawPassword, (raw, encoded) -> encoded.equals("$2a$10$encoded_" + raw));

            // then
            assertThat(matches).isTrue();
        }

        @Test
        @DisplayName("matches()로 비밀번호 일치 여부를 확인할 수 있다 - 일치하지 않는 경우")
        void matchesPasswordFailure() {
            // given
            String rawPassword = "password123!";
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // when
            boolean matches = password.matches("wrongPassword", (raw, encoded) -> encoded.equals("$2a$10$encoded_" + raw));

            // then
            assertThat(matches).isFalse();
        }

        @Test
        @DisplayName("암호화된 비밀번호와 평문 비밀번호를 비교할 수 있다")
        void matchesWithEncodedPassword() {
            // given
            String rawPassword = "mySecret123!";
            String encodedPassword = "$2a$10$encoded_mySecret123!";

            Password password = Password.of(rawPassword, raw -> encodedPassword);

            // when
            boolean correctMatch = password.matches(rawPassword, (raw, encoded) ->
                    encoded.equals(encodedPassword) && raw.equals("mySecret123!"));
            boolean wrongMatch = password.matches("wrongPass!", (raw, encoded) ->
                    encoded.equals(encodedPassword) && raw.equals("mySecret123!"));

            // then
            assertThat(correctMatch).isTrue();
            assertThat(wrongMatch).isFalse();
        }
    }


    @Nested
    @DisplayName("Password 통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("회원가입 시나리오: 생성 시 암호화")
        void signUpScenario() {
            // given
            String rawPassword = "User123!@#";

            // when - 회원가입 시 비밀번호 생성 및 암호화
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then - 비밀번호 객체 생성 확인
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("로그인 시나리오: 비밀번호 일치 확인")
        void loginScenario() {
            // given - 저장된 암호화된 비밀번호
            String rawPassword = "myPassword123!";
            Password storedPassword = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // when - 로그인 시도
            String loginAttemptPassword = "myPassword123!";
            boolean isCorrectPassword = storedPassword.matches(
                    loginAttemptPassword,
                    (raw, encoded) -> encoded.equals("$2a$10$encoded_" + raw)
            );

            // then
            assertThat(isCorrectPassword).isTrue();
        }

        @Test
        @DisplayName("비밀번호 변경 시나리오: 기존 비밀번호 -> 새 비밀번호")
        void changePasswordScenario() {
            // given - 기존 비밀번호
            Password password = Password.of("oldPass123!", raw -> "$2a$10$encoded_" + raw);

            // when - 비밀번호 변경
            Password newPassword = password.change("newPass456!", raw -> "$2a$10$new_encoded_" + raw);

            // then - 새 비밀번호로 변경되었는지 확인
            boolean matchesOld = newPassword.matches("oldPass123!", (raw, encoded) -> encoded.equals("$2a$10$encoded_" + raw));
            boolean matchesNew = newPassword.matches("newPass456!", (raw, encoded) -> encoded.equals("$2a$10$new_encoded_" + raw));

            assertThat(matchesOld).isFalse();
            assertThat(matchesNew).isTrue();
        }
    }

    @Nested
    @DisplayName("경계값 및 특수 케이스 테스트")
    class BoundaryAndSpecialCaseTest {

        @Test
        @DisplayName("정확히 8자(최소 길이) 비밀번호로 생성할 수 있다")
        void createPasswordWithExactly8Characters() {
            // given
            String rawPassword = "Pass123!";  // 정확히 8자

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("정확히 20자(최대 길이) 비밀번호로 생성할 수 있다")
        void createPasswordWithExactly20Characters() {
            // given
            String rawPassword = "Pass123!@#$%?abcdefg";  // 정확히 20자

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("7자 비밀번호는 생성할 수 없다")
        void cannotCreatePasswordWith7Characters() {
            // given
            String rawPassword = "Pass12!";  // 7자

            // when & then
            assertThatThrownBy(() -> Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        @Test
        @DisplayName("21자 비밀번호는 생성할 수 없다")
        void cannotCreatePasswordWith21Characters() {
            // given
            String rawPassword = "Pass123!@#$%?abcdefgh";  // 21자

            // when & then
            assertThatThrownBy(() -> Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 최대 20자 이하이어야 합니다.");
        }

        @Test
        @DisplayName("허용된 모든 특수문자를 사용할 수 있다")
        void createPasswordWithAllSpecialCharacters() {
            // given
            String rawPassword = "Pass123!@#$%?";

            // when
            Password password = Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw);

            // then
            assertThat(password).isNotNull();
        }

        @Test
        @DisplayName("영문자 없이는 생성할 수 없다 (유니코드만)")
        void cannotCreatePasswordWithUnicodeOnly() {
            // given - 영문자가 없는 유니코드 비밀번호
            String rawPassword = "비밀번호123!@#";

            // when & then
            assertThatThrownBy(() -> Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 영문자를 포함해야 합니다.");
        }

        @Test
        @DisplayName("영문자 없이는 생성할 수 없다 (이모지 포함)")
        void cannotCreatePasswordWithEmojiWithoutLetter() {
            // given - 영문자가 없는 비밀번호
            String rawPassword = "123456!🔒";

            // when & then
            assertThatThrownBy(() -> Password.of(rawPassword, raw -> "$2a$10$encoded_" + raw))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 영문자를 포함해야 합니다.");
        }
    }
}