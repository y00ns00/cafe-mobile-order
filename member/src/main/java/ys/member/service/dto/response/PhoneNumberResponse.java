package ys.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ys.member.domain.vo.PhoneNumber;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneNumberResponse {

    @Schema(description = "전화번호 (숫자만)", example = "01012345678")
    private String value;

    @Schema(description = "포맷팅된 전화번호", example = "010-1234-5678")
    private String formatted;

    public static PhoneNumberResponse from(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return new PhoneNumberResponse(
            phoneNumber.getValue(),
            phoneNumber.getFormatted()
        );
    }
}
