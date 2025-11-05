package ys.cafe.payment.service.dto.response;

public record PaymentInfoResponse(Long orderId, Long memberId, String amount, String status) {

    public static PaymentInfoResponse of(Long orderId, Long memberId, String amount, String status) {
        return new PaymentInfoResponse(orderId, memberId, amount, status);
    }
}
