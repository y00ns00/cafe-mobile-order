package ys.cafe.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.cafe.member.domain.vo.PhoneNumber;

public record PhoneNumberResponse(

        @Schema(description = "전화번호 (숫자만)", example = "01012345678")
        String value,

        @Schema(description = "포맷팅된 전화번호", example = "010-1234-5678")
        String formatted
) {

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
