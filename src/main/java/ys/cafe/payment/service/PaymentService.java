package ys.cafe.payment.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.vo.Won;
import ys.cafe.payment.service.dto.PaymentInfoResponse;
import ys.cafe.payment.service.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(
            Long orderId,
            Long memberId,
            Won won
    );

    PaymentInfoResponse getPaymentInfo(String paymentKey);

    List<PaymentInfoResponse> getUserPayments(Long memberId);

    PaymentInfoResponse cancelPayment(Long orderId);

    @Async("asyncExecutor")
    void processSingleCanceledPaymentAsync(String paymentKey);
}
