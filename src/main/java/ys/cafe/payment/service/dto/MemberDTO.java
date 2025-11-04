package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ys.cafe.member.domain.Member;
import ys.cafe.member.service.dto.response.BirthDateResponse;
import ys.cafe.member.service.dto.response.NameResponse;
import ys.cafe.member.service.dto.response.PhoneNumberResponse;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    @Schema(description = "회원 이름")
    private String name;

    @Schema(description = "회원 전화번호")
    private String phoneNumber;

    @Schema(description = "성별", example = "MALE")
    private String gender;

    @Schema(description = "생년월일")
    private String birthDate;

    @Schema(description = "등록일시 (ISO-8601 형식)", example = "2025-11-01T12:34:56.789")
    private String registrationDateTime;


    public static MemberDTO of(
            String name,
            String phoneNumber,
            String gender,
            String birthDate,
            String registrationDateTime
    ) {
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.name = name;
        memberDTO.phoneNumber = phoneNumber;
        memberDTO.gender = gender;
        memberDTO.birthDate = birthDate;
        memberDTO.registrationDateTime = registrationDateTime;

        return memberDTO;
    }
}