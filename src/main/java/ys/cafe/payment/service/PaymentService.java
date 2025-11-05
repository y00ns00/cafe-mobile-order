package ys.cafe.payment.service;

import org.springframework.scheduling.annotation.Async;
import ys.cafe.payment.service.dto.response.PaymentInfoResponse;
import ys.cafe.payment.service.dto.response.PaymentListResponse;
import ys.cafe.payment.service.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(
            Long orderId,
            Long memberId,
            String won
    );

    PaymentInfoResponse getPaymentInfo(String paymentKey);

    PaymentListResponse getUserPayments(Long memberId);

    PaymentInfoResponse cancelPayment(Long orderId);

    void processSingleCanceledPaymentAsync(String paymentKey);
}
