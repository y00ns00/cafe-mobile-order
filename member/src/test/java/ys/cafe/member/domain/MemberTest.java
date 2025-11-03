package ys.cafe.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ys.cafe.member.domain.vo.MemberStatus;
import ys.cafe.member.exception.MemberValidationException;
import ys.cafe.member.exception.errorcode.MemberValidationErrorCode;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 엔티티 테스트")
class MemberTest {

    @Nested
    @DisplayName("회원 등록 성공 테스트")
    class RegisterSuccessTest {

        @Test
        @DisplayName("유효한 정보로 회원을 등록할 수 있다")
        void registerMemberWithValidInfo() {
            // given
            String rawPassword = "Password123!";
            String lastName = "김";
            String firstName = "철수";
            String phoneNumber = "010-1234-5678";
            String gender = "MALE";
            String birthDate = "1990-01-01";

            // when
            Member member = Member.register(
                    rawPassword,
                    lastName,
                    firstName,
                    phoneNumber,
                    gender,
                    birthDate,
                    raw -> "$2a$10$encoded_" + raw
            );

            // then
            assertThat(member).isNotNull();
            assertThat(member.getName().getLastName()).isEqualTo("김");
            assertThat(member.getName().getFirstName()).isEqualTo("철수");
            assertThat(member.getPhoneNumber().getValue()).isEqualTo("01012345678");
            assertThat(member.getGender().name()).isEqualTo("MALE");
            assertThat(member.getBirthDate().getValue().toString()).isEqualTo("1990-01-01");
            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
            assertThat(member.getRegistrationDateTime()).isNotNull();
        }

        @Test
        @DisplayName("여성 회원을 등록할 수 있다")
        void registerFemaleMember() {
            // given
            String gender = "FEMALE";

            // when
            Member member = Member.register(
                    "Password123!",
                    "이",
                    "영희",
                    "010-9876-5432",
                    gender,
                    "1995-05-15",
                    raw -> "$2a$10$encoded_" + raw
            );

            // then
            assertThat(member.getGender().name()).isEqualTo("FEMALE");
        }

        @Test
        @DisplayName("기타 성별로 회원을 등록할 수 있다")
        void registerOtherGenderMember() {
            // given
            String gender = "OTHER";

            // when
            Member member = Member.register(
                    "Password123!",
                    "박",
                    "민수",
                    "010-1111-2222",
                    gender,
                    "2000-12-31",
                    raw -> "$2a$10$encoded_" + raw
            );

            // then
            assertThat(member.getGender().name()).isEqualTo("OTHER");
        }

        @Test
        @DisplayName("등록 시 상태는 ACTIVE로 설정된다")
        void registerMemberWithActiveStatus() {
            // when
            Member member = Member.register(
                    "Password123!",
                    "최",
                    "지훈",
                    "010-3333-4444",
                    "MALE",
                    "1985-03-20",
                    raw -> "$2a$10$encoded_" + raw
            );

            // then
            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
            assertThat(member.getWithdrawRequestedAt()).isNull();
        }

        @Test
        @DisplayName("등록 시 등록일시가 자동으로 설정된다")
        void registerMemberWithRegistrationDateTime() {
            // given
            LocalDateTime beforeRegistration = LocalDateTime.now();

            // when
            Member member = Member.register(
                    "Password123!",
                    "정",
                    "수민",
                    "010-5555-6666",
                    "FEMALE",
                    "1992-07-07",
                    raw -> "$2a$10$encoded_" + raw
            );

            // then
            LocalDateTime afterRegistration = LocalDateTime.now();
            assertThat(member.getRegistrationDateTime()).isNotNull();
            assertThat(member.getRegistrationDateTime())
                    .isAfterOrEqualTo(beforeRegistration)
                    .isBeforeOrEqualTo(afterRegistration);
        }
    }

    @Nested
    @DisplayName("회원 등록 실패 테스트 - 비밀번호 검증")
    class RegisterFailWithPasswordTest {

        @Test
        @DisplayName("비밀번호가 null이면 PASSWORD_REQUIRED 예외가 발생한다")
        void registerWithNullPassword() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    null,
                    "김",
                    "철수",
                    "010-1234-5678",
                    "MALE",
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_REQUIRED);
        }

        @Test
        @DisplayName("비밀번호가 짧으면 PASSWORD_TOO_SHORT 예외가 발생한다")
        void registerWithShortPassword() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Pass1!",
                    "김",
                    "철수",
                    "010-1234-5678",
                    "MALE",
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("비밀번호는 최소 8자 이상이어야 합니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PASSWORD_TOO_SHORT);
        }
    }

    @Nested
    @DisplayName("회원 등록 실패 테스트 - 이름 검증")
    class RegisterFailWithNameTest {

        @Test
        @DisplayName("성이 null이면 LAST_NAME_REQUIRED 예외가 발생한다")
        void registerWithNullLastName() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    null,
                    "철수",
                    "010-1234-5678",
                    "MALE",
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("성은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.LAST_NAME_REQUIRED);
        }

        @Test
        @DisplayName("이름이 null이면 FIRST_NAME_REQUIRED 예외가 발생한다")
        void registerWithNullFirstName() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    null,
                    "010-1234-5678",
                    "MALE",
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("이름은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.FIRST_NAME_REQUIRED);
        }
    }

    @Nested
    @DisplayName("회원 등록 실패 테스트 - 전화번호 검증")
    class RegisterFailWithPhoneNumberTest {

        @Test
        @DisplayName("전화번호가 null이면 PHONE_NUMBER_REQUIRED 예외가 발생한다")
        void registerWithNullPhoneNumber() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    "철수",
                    null,
                    "MALE",
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호는 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PHONE_NUMBER_REQUIRED);
        }

        @Test
        @DisplayName("전화번호 형식이 잘못되면 PHONE_NUMBER_INVALID_FORMAT 예외가 발생한다")
        void registerWithInvalidPhoneNumber() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    "철수",
                    "123-4567",
                    "MALE",
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.PHONE_NUMBER_INVALID_FORMAT);
        }
    }

    @Nested
    @DisplayName("회원 등록 실패 테스트 - 성별 검증")
    class RegisterFailWithGenderTest {

        @ParameterizedTest
        @ValueSource(strings = {"male", "INVALID", "M", "F", ""})
        @DisplayName("잘못된 성별 값이면 GENDER_INVALID 예외가 발생한다")
        void registerWithInvalidGender(String invalidGender) {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    "철수",
                    "010-1234-5678",
                    invalidGender,
                    "1990-01-01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("성별은 MALE, FEMALE, OTHER 중 하나여야 합니다. ")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.GENDER_INVALID);
        }
    }

    @Nested
    @DisplayName("회원 등록 실패 테스트 - 생년월일 검증")
    class RegisterFailWithBirthDateTest {

        @Test
        @DisplayName("생년월일이 null이면 BIRTH_DATE_REQUIRED 예외가 발생한다")
        void registerWithNullBirthDate() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    "철수",
                    "010-1234-5678",
                    "MALE",
                    null,
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_REQUIRED);
        }

        @Test
        @DisplayName("생년월일 형식이 잘못되면 BIRTH_DATE_INVALID_FORMAT 예외가 발생한다")
        void registerWithInvalidBirthDate() {
            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    "철수",
                    "010-1234-5678",
                    "MALE",
                    "1990/01/01",
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_INVALID_FORMAT);
        }

        @Test
        @DisplayName("미래 날짜로 생년월일을 입력하면 BIRTH_DATE_FUTURE 예외가 발생한다")
        void registerWithFutureBirthDate() {
            // given
            String futureBirthDate = "2999-12-31";

            // when & then
            assertThatThrownBy(() -> Member.register(
                    "Password123!",
                    "김",
                    "철수",
                    "010-1234-5678",
                    "MALE",
                    futureBirthDate,
                    raw -> "$2a$10$encoded_" + raw
            ))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("생년월일은 현재 날짜보다 이후일 수 없습니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.BIRTH_DATE_FUTURE);
        }
    }

}