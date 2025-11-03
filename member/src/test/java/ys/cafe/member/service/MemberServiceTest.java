package ys.cafe.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ys.cafe.member.common.CommonErrorCode;
import ys.cafe.member.common.CommonException;
import ys.cafe.member.domain.Member;
import ys.cafe.member.domain.vo.PhoneNumber;
import ys.cafe.member.persistence.MemberRepository;
import ys.cafe.member.service.dto.request.MemberSignUpRequest;
import ys.cafe.member.service.dto.response.MemberResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입 성공 테스트")
    class SignUpSuccessTest {

        @Test
        @DisplayName("유효한 정보로 회원가입을 할 수 있다")
        void signUpWithValidInfo() {
            // given
            String password = "Password123!";
            String lastName = "김";
            String firstName = "철수";
            String phoneNumber = "010-1234-5678";
            String gender = "MALE";
            String birthDate = "1990-01-01";

            MemberSignUpRequest request = new MemberSignUpRequest(
                    password,
                    lastName,
                    firstName,
                    phoneNumber,
                    gender,
                    birthDate
            );

            when(memberRepository.findOneByPhoneNumber(Mockito.any(PhoneNumber.class)))
                    .thenReturn(Optional.empty());


            when(passwordEncoder.encode(Mockito.anyString())).thenReturn(password+"encoded");

            // when
            MemberResponse response = memberService.signUp(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName().getFullName()).isEqualTo("김철수");
            assertThat(response.getName().getLastName()).isEqualTo("김");
            assertThat(response.getName().getFirstName()).isEqualTo("철수");
            assertThat(response.getPhoneNumber().getFormatted()).isEqualTo("010-1234-5678");
            assertThat(response.getPhoneNumber().getValue()).isEqualTo("01012345678");
            assertThat(response.getGender()).isEqualTo("MALE");
            assertThat(response.getBirthDate().getBirthDate()).isEqualTo("1990-01-01");
            assertThat(response.getRegistrationDateTime()).isNotNull();
        }

        @Test
        @DisplayName("여성 회원으로 가입할 수 있다")
        void signUpWithFemaleMember() {
            // given
            String password = "Password123!";
            String lastName = "김";
            String firstName = "영희";
            String phoneNumber = "010-1234-5678";
            String gender = "FEMALE";
            String birthDate = "1995-01-01";

            MemberSignUpRequest request = new MemberSignUpRequest(
                    password,
                    lastName,
                    firstName,
                    phoneNumber,
                    gender,
                    birthDate
            );

            when(memberRepository.findOneByPhoneNumber(Mockito.any(PhoneNumber.class)))
                    .thenReturn(Optional.empty());


            when(passwordEncoder.encode(Mockito.anyString())).thenReturn(password+"encoded");

            // when
            MemberResponse response = memberService.signUp(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName().getFullName()).isEqualTo("김영희");
            assertThat(response.getName().getLastName()).isEqualTo("김");
            assertThat(response.getName().getFirstName()).isEqualTo("영희");
            assertThat(response.getPhoneNumber().getFormatted()).isEqualTo("010-1234-5678");
            assertThat(response.getPhoneNumber().getValue()).isEqualTo("01012345678");
            assertThat(response.getGender()).isEqualTo("FEMALE");
            assertThat(response.getBirthDate().getBirthDate()).isEqualTo("1995-01-01");
            assertThat(response.getRegistrationDateTime()).isNotNull();
        }

        @Test
        @DisplayName("기타 성별로 가입할 수 있다")
        void signUpWithOtherGender() {
            // given
            MemberSignUpRequest request = new MemberSignUpRequest(
                    "Password123!",
                    "박",
                    "민수",
                    "010-1111-2222",
                    "OTHER",
                    "2000-12-31"
            );

            when(memberRepository.findOneByPhoneNumber(Mockito.any(PhoneNumber.class)))
                    .thenReturn(Optional.empty());

            // when
            MemberResponse response = memberService.signUp(request);


            // then
            assertThat(response.getGender()).isEqualTo("OTHER");
        }

        @Test
        @DisplayName("하이픈 없는 전화번호로 가입할 수 있다")
        void signUpWithPhoneNumberWithoutHyphens() {
            // given
            MemberSignUpRequest request = new MemberSignUpRequest(
                    "Password123!",
                    "최",
                    "지훈",
                    "01033334444",
                    "MALE",
                    "1985-03-20"
            );

            when(memberRepository.findOneByPhoneNumber(Mockito.any(PhoneNumber.class)))
                    .thenReturn(Optional.empty());

            // when
            MemberResponse response = memberService.signUp(request);

            // then
            assertThat(response.getPhoneNumber().getFormatted()).isEqualTo("010-3333-4444");
            assertThat(response.getPhoneNumber().getValue()).isEqualTo("01033334444");

        }


        @Test
        @DisplayName("윤년(2월 29일) 생년월일로 가입할 수 있다")
        void signUpWithLeapYearBirthDate() {
            // given
            MemberSignUpRequest request = new MemberSignUpRequest(
                    "Password123!",
                    "윤",
                    "년생",
                    "010-2222-2929",
                    "FEMALE",
                    "2000-02-29"
            );

            when(memberRepository.findOneByPhoneNumber(Mockito.any(PhoneNumber.class)))
                    .thenReturn(Optional.empty());

            // when
            MemberResponse response = memberService.signUp(request);

            // then
            assertThat(response.getBirthDate().getBirthDate()).isEqualTo("2000-02-29");
        }

    }

    @Nested
    @DisplayName("회원가입 실패 테스트")
    class SignUpFailTest {

        @Test
        @DisplayName("중복된 전화번호로 가입하면 AlreadyExists 예외가 발생한다")
        void signUpWithDuplicatePhoneNumber() {
            // given
            String password = "Password123!";
            String lastName = "김";
            String firstName = "영희";
            String phoneNumber = "010-1234-5678";
            String gender = "FEMALE";
            String birthDate = "1995-01-01";

            Member registerMember = Member.register(
                    password,
                    lastName,
                    firstName,
                    phoneNumber,
                    gender,
                    birthDate,
                    s -> s+"encoded"
            );


            when(memberRepository.findOneByPhoneNumber(Mockito.any(PhoneNumber.class)))
                    .thenReturn(Optional.of(registerMember));

            MemberSignUpRequest request = new MemberSignUpRequest(
                    password,
                    lastName,
                    firstName,
                    phoneNumber,
                    gender,
                    birthDate
            );


            // when & then
            assertThatThrownBy(() -> memberService.signUp(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage("이미 존재하는 회원입니다.")
                    .extracting("errorCode")
                    .isEqualTo(CommonErrorCode.ALREADY_EXISTS);
        }
    }

}
