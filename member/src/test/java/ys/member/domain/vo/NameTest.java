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

@DisplayName("Name Value Object 테스트")
class NameTest {

    @Nested
    @DisplayName("Name 생성 성공 테스트")
    class CreateNameSuccessTest {

        @Test
        @DisplayName("성과 이름으로 Name을 생성할 수 있다")
        void createNameWithValidInput() {
            // given
            String lastName = "김";
            String firstName = "철수";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name).isNotNull();
            assertThat(name.getLastName()).isEqualTo("김");
            assertThat(name.getFirstName()).isEqualTo("철수");
        }

        @Test
        @DisplayName("공백이 포함된 이름은 trim 처리된다")
        void createNameWithWhitespace() {
            // given
            String lastName = "  이  ";
            String firstName = "  영희  ";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name.getLastName()).isEqualTo("이");
            assertThat(name.getFirstName()).isEqualTo("영희");
        }

        @Test
        @DisplayName("한 글자 성과 이름으로 Name을 생성할 수 있다")
        void createNameWithSingleCharacter() {
            // given
            String lastName = "박";
            String firstName = "민";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name).isNotNull();
            assertThat(name.getLastName()).isEqualTo("박");
            assertThat(name.getFirstName()).isEqualTo("민");
        }

        @Test
        @DisplayName("긴 이름으로 Name을 생성할 수 있다")
        void createNameWithLongName() {
            // given
            String lastName = "선우";
            String firstName = "용녀";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name).isNotNull();
            assertThat(name.getLastName()).isEqualTo("선우");
            assertThat(name.getFirstName()).isEqualTo("용녀");
        }

        @Test
        @DisplayName("영문 이름으로 Name을 생성할 수 있다")
        void createNameWithEnglishName() {
            // given
            String lastName = "Kim";
            String firstName = "James";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name).isNotNull();
            assertThat(name.getLastName()).isEqualTo("Kim");
            assertThat(name.getFirstName()).isEqualTo("James");
        }
    }

    @Nested
    @DisplayName("Name 생성 실패 테스트 - LAST_NAME_REQUIRED")
    class CreateNameFailWithLastNameRequiredTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("성이 null 또는 빈 문자열인 경우 LAST_NAME_REQUIRED 예외가 발생한다")
        void createNameWithNullOrBlankLastName(String lastName) {
            // given
            String firstName = "철수";

            // when & then
            assertThatThrownBy(() -> Name.of(lastName, firstName))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("성은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.LAST_NAME_REQUIRED);
        }
    }

    @Nested
    @DisplayName("Name 생성 실패 테스트 - FIRST_NAME_REQUIRED")
    class CreateNameFailWithFirstNameRequiredTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("이름이 null 또는 빈 문자열인 경우 FIRST_NAME_REQUIRED 예외가 발생한다")
        void createNameWithNullOrBlankFirstName(String firstName) {
            // given
            String lastName = "김";

            // when & then
            assertThatThrownBy(() -> Name.of(lastName, firstName))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("이름은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.FIRST_NAME_REQUIRED);
        }
    }

    @Nested
    @DisplayName("Name 메서드 테스트")
    class NameMethodTest {

        @Test
        @DisplayName("getFullName()은 성과 이름을 합쳐서 반환한다")
        void getFullName() {
            // given
            Name name = Name.of("김", "철수");

            // when
            String fullName = name.getFullName();

            // then
            assertThat(fullName).isEqualTo("김철수");
        }

        @Test
        @DisplayName("toString()은 getFullName()과 동일한 결과를 반환한다")
        void testToString() {
            // given
            Name name = Name.of("이", "영희");

            // when
            String result = name.toString();

            // then
            assertThat(result).isEqualTo("이영희");
            assertThat(result).isEqualTo(name.getFullName());
        }

        @Test
        @DisplayName("getLastName()은 성을 반환한다")
        void getLastName() {
            // given
            Name name = Name.of("박", "민수");

            // when
            String lastName = name.getLastName();

            // then
            assertThat(lastName).isEqualTo("박");
        }

        @Test
        @DisplayName("getFirstName()은 이름을 반환한다")
        void getFirstName() {
            // given
            Name name = Name.of("최", "서연");

            // when
            String firstName = name.getFirstName();

            // then
            assertThat(firstName).isEqualTo("서연");
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("한 글자 성과 세 글자 이름으로 생성할 수 있다")
        void createNameWithOneCharLastNameAndThreeCharFirstName() {
            // given
            String lastName = "김";
            String firstName = "하늘별";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name.getFullName()).isEqualTo("김하늘별");
        }

        @Test
        @DisplayName("두 글자 성과 한 글자 이름으로 생성할 수 있다")
        void createNameWithTwoCharLastNameAndOneCharFirstName() {
            // given
            String lastName = "남궁";
            String firstName = "민";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name.getFullName()).isEqualTo("남궁민");
        }

        @Test
        @DisplayName("특수문자가 포함된 이름도 생성할 수 있다")
        void createNameWithSpecialCharacters() {
            // given
            String lastName = "O'Brien";
            String firstName = "Mary-Jane";

            // when
            Name name = Name.of(lastName, firstName);

            // then
            assertThat(name.getFullName()).isEqualTo("O'BrienMary-Jane");
        }

        @Test
        @DisplayName("trim 후 빈 문자열이 되면 예외가 발생한다 - lastName")
        void createNameWithOnlyWhitespaceLastName() {
            // given
            String lastName = "   ";
            String firstName = "철수";

            // when & then
            assertThatThrownBy(() -> Name.of(lastName, firstName))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("성은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.LAST_NAME_REQUIRED);
        }

        @Test
        @DisplayName("trim 후 빈 문자열이 되면 예외가 발생한다 - firstName")
        void createNameWithOnlyWhitespaceFirstName() {
            // given
            String lastName = "김";
            String firstName = "   ";

            // when & then
            assertThatThrownBy(() -> Name.of(lastName, firstName))
                    .isInstanceOf(MemberValidationException.class)
                    .hasMessage("이름은 필수입니다.")
                    .extracting("errorCode")
                    .isEqualTo(MemberValidationErrorCode.FIRST_NAME_REQUIRED);
        }
    }

}