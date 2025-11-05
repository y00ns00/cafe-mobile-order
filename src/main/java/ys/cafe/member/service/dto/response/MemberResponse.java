package ys.cafe.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.cafe.member.domain.Member;

import java.time.format.DateTimeFormatter;

@Schema(description = "회원 응답")
public record MemberResponse(

        @Schema(description = "회원 id")
        Long memberId,

        @Schema(description = "회원 이름")
        NameResponse name,

        @Schema(description = "회원 전화번호")
        PhoneNumberResponse phoneNumber,

        @Schema(description = "성별", example = "MALE")
        String gender,

        @Schema(description = "생년월일")
        BirthDateResponse birthDate,

        @Schema(description = "등록일시 (ISO-8601 형식)", example = "2025-11-01T12:34:56.789")
        String registrationDateTime
) {

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
