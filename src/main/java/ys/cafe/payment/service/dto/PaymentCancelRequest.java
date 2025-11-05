package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 취소 요청")
public class PaymentCancelRequest {

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    public static PaymentCancelRequest of(Long orderId) {
        return new PaymentCancelRequest(orderId);
    }
}
