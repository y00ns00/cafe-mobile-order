package ys.cafe.member.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ys.cafe.member.common.ErrorCodeHttpStatusMapper;
import ys.cafe.member.common.GlobalExceptionHandler;
import ys.cafe.member.objectmother.MemberMother;
import ys.cafe.member.service.MemberService;
import ys.cafe.member.service.dto.request.MemberSignUpRequest;
import ys.cafe.member.service.dto.response.MemberResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class MemberControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private MemberController memberController;

    @Mock
    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        ErrorCodeHttpStatusMapper errorCodeHttpStatusMapper = new ErrorCodeHttpStatusMapper();
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(errorCodeHttpStatusMapper);
        memberController = new MemberController(memberService);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(memberController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
        ;

    }

    @Nested
    @DisplayName("회원가입 api 호출 성공 테스트")
    class SignUpRequestSuccessTest {

        @Test
        @DisplayName("멤버 생성")
        public void signUpMember() throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName("김")
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber("01012345678")
                    .gender("MALE")
                    .build();

            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(jsonPath("$.name.lastName").value("김"))
                    .andExpect(jsonPath("$.name.firstName").value("철수"))
                    .andExpect(jsonPath("$.name.fullName").value("김철수"))
                    .andExpect(jsonPath("$.phoneNumber.value").value("01012345678"))
                    .andExpect(jsonPath("$.phoneNumber.formatted").value("010-1234-5678"))
                    .andExpect(jsonPath("$.gender").value("MALE"))
                    .andExpect(jsonPath("$.birthDate.birthDate").value("1990-01-01"))
                    .andExpect(jsonPath("$.birthDate.age").value("35"))
                    .andExpect(jsonPath("$.registrationDateTime").isNotEmpty())
            ;
        }

    }


    @Nested
    @DisplayName("회원가입 api 호출 실패 테스트")
    class SignUpRequestFailTest {

        @ParameterizedTest
        @ValueSource(strings = {"가나다", "ab", "abc1!", "Pass1!", "1234567"})
        @DisplayName("유효하지 않은 패스워드 입력시 예외를 발생한다.")
        public void signUpMemberWithInvalidPassword(String password) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password(password)
                    .lastName("김")
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber("01012345678")
                    .gender("MALE")
                    .build();

            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))

            ;
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("비어있거나 Null 패스워드 입력시 예외를 발생한다.")
        public void signUpMemberWithPasswordNullOrBlank(String password) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password(password)
                    .lastName("김")
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber("01012345678")
                    .gender("MALE")
                    .build();

            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))

            ;
        }


        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("비어있거나 Null lastName 입력시 예외를 발생한다.")
        public void signUpMemberWithLastNameNullOrBlank(String lastName) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName(lastName)
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber("01012345678")
                    .gender("MALE")
                    .build();

            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
            ;
        }


        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("비어있거나 Null firstName 입력시 예외를 발생한다.")
        public void signUpMemberWithFirstNameNullOrBlank(String firstName) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName("김")
                    .firstName(firstName)
                    .birthDate("1995-03-25")
                    .phoneNumber("01012345678")
                    .gender("MALE")
                    .build();
            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
            ;
        }


        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("비어있거나 Null phone 입력시 예외를 발생한다.")
        public void signUpMemberWithPhoneNullOrBlank(String phone) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName("김")
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber(phone)
                    .gender("MALE")
                    .build();
            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
            ;
        }


        @ParameterizedTest
        @ValueSource(strings = {"010-1234-586", "01012345", "010-1234-5667-1", "010123456789"})
        @DisplayName("유효하지 않은 phone 입력시 예외를 발생한다.")
        public void signUpMemberWithInvalidPhone(String phone) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName("김")
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber(phone)
                    .gender("MALE")
                    .build();
            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
            ;
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n","BOY", "GIRL", "MAN", "WOMAN"})
        @DisplayName("유효하지 않은 Gender 입력시 예외를 발생한다.")
        public void signUpMemberWithInvalidGender(String gender) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName("김")
                    .firstName("철수")
                    .birthDate("1995-03-25")
                    .phoneNumber("01012345678")
                    .gender(gender)
                    .build();
            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
            ;
        }


        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n","1995-01-40", "19950123",})
        @DisplayName("유효하지 않은 BirthDate 입력시 예외를 발생한다.")
        public void signUpMemberWithInvalidBirthDate(String birthDate) throws Exception {
            // given
            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .password("password123!@")
                    .lastName("김")
                    .firstName("철수")
                    .birthDate(birthDate)
                    .phoneNumber("01012345678")
                    .gender("MALE")
                    .build();
            MemberResponse response = MemberResponse.from(MemberMother.getRegisterMember());

            Mockito.when(memberService.signUp(Mockito.any(MemberSignUpRequest.class))).thenReturn(response);

            // when&then
            mockMvc.perform(
                            post("/members/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
            ;
        }






    }


}
