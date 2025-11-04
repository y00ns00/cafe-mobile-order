package ys.cafe.payment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentUrlResponse {

    private final String paymentUrl;
    private final String paymentKey;

    public static PaymentUrlResponse of(String paymentUrl, String paymentKey) {
        return new PaymentUrlResponse(paymentUrl, paymentKey);
    }
}