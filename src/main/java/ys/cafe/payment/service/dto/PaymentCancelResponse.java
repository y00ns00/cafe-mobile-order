package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "결제 취소 응답")
public class PaymentCancelResponse {

    @Schema(description = "취소 성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "응답 메시지", example = "결제가 취소되었습니다.")
    private final String message;

    public static PaymentCancelResponse of(boolean success, String message) {
        return new PaymentCancelResponse(success, message);
    }
}
