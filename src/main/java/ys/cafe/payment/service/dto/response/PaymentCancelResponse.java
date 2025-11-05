package ys.cafe.payment.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 취소 응답")
public record PaymentCancelResponse(

        @Schema(description = "취소 성공 여부", example = "true")
        boolean success,

        @Schema(description = "응답 메시지", example = "결제가 취소되었습니다.")
        String message
) {
    public static PaymentCancelResponse of(boolean success, String message) {
        return new PaymentCancelResponse(success, message);
    }
}
