package ys.cafe.payment.service.dto;

public record PaymentUrlResponse(String paymentUrl, String paymentKey) {

    public static PaymentUrlResponse of(String paymentUrl, String paymentKey) {
        return new PaymentUrlResponse(paymentUrl, paymentKey);
    }
}
