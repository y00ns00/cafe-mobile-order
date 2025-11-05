package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 취소 요청")
public record PaymentCancelRequest(

        @Schema(description = "주문 ID", example = "1")
        Long orderId
) {
    public static PaymentCancelRequest of(Long orderId) {
        return new PaymentCancelRequest(orderId);
    }
}
