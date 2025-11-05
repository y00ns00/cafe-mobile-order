package ys.cafe.payment.service.dto.response;

public record PaymentResponse(boolean success, String message) {

    public static PaymentResponse success(String paymentKey) {
        return new PaymentResponse(true, "결제 성공");
    }

    public static PaymentResponse failure(String message) {
        return new PaymentResponse(false, message);
    }
}
