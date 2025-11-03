package ys.cafe.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ys.cafe.member.domain.vo.BirthDate;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BirthDateResponse {

    @Schema(description = "생년월일 (yyyy-MM-dd 형식)", example = "1990-01-01")
    private String birthDate;

    @Schema(description = "나이", example = "35")
    private Integer age;

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
