package ys.cafe.payment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private final boolean success;
    private final String message;


    public static PaymentResponse success(String paymentKey) {
        return new PaymentResponse(true, "결제 성공");
    }

    public static PaymentResponse failure(String message) {
        return new PaymentResponse(false, message);
    }
}