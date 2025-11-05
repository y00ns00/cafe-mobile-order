package ys.cafe.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.cafe.member.domain.vo.BirthDate;

public record BirthDateResponse(

        @Schema(description = "생년월일 (yyyy-MM-dd 형식)", example = "1990-01-01")
        String birthDate,

        @Schema(description = "나이", example = "35")
        Integer age
) {

    public static BirthDateResponse from(BirthDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return new BirthDateResponse(
                birthDate.getFormatted(),
                birthDate.getAge()
        );
    }
}
