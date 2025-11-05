package ys.cafe.payment.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.payment.domain.vo.Won;
import ys.cafe.payment.service.dto.PaymentInfoResponse;
import ys.cafe.payment.service.dto.PaymentListResponse;
import ys.cafe.payment.service.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(
            Long orderId,
            Long memberId,
            String won
    );

    PaymentInfoResponse getPaymentInfo(String paymentKey);

    PaymentListResponse getUserPayments(Long memberId);

    PaymentInfoResponse cancelPayment(Long orderId);

    @Async("asyncExecutor")
    void processSingleCanceledPaymentAsync(String paymentKey);
}
