package ys.cafe.payment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoResponse {

    private Long orderId;
    private Long memberId;
    private String amount;
    private String status;

    public static PaymentInfoResponse of(Long orderId, Long memberId, String amount, String status) {
        return new PaymentInfoResponse(orderId, memberId, amount, status);
    }
}