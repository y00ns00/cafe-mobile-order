package ys.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ys.member.domain.Member;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "회원 이름")
    private NameResponse name;

    @Schema(description = "회원 전화번호")
    private PhoneNumberResponse phoneNumber;

    @Schema(description = "성별", example = "MALE")
    private String gender;

    @Schema(description = "생년월일")
    private BirthDateResponse birthDate;

    @Schema(description = "등록일시 (ISO-8601 형식)", example = "2025-11-01T12:34:56.789")
    private String registrationDateTime;

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getMemberId(),
            NameResponse.from(member.getName()),
            PhoneNumberResponse.from(member.getPhoneNumber()),
            member.getGender().name(),
            BirthDateResponse.from(member.getBirthDate()),
            member.getRegistrationDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
}