package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 모듈에서 사용하는 회원 요약 정보")
public record MemberDTO(

        @Schema(description = "회원 이름")
        String name,

        @Schema(description = "회원 전화번호")
        String phoneNumber,

        @Schema(description = "성별", example = "MALE")
        String gender,

        @Schema(description = "생년월일")
        String birthDate,

        @Schema(description = "등록일시 (ISO-8601 형식)", example = "2025-11-01T12:34:56.789")
        String registrationDateTime
) {
    public static MemberDTO of(
            String name,
            String phoneNumber,
            String gender,
            String birthDate,
            String registrationDateTime
    ) {
        return new MemberDTO(name, phoneNumber, gender, birthDate, registrationDateTime);
    }
}
