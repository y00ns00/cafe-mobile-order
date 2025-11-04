package ys.cafe.payment.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.vo.Won;
import ys.cafe.payment.service.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(
            Long orderId,
            Long memberId,
            Won won
    );

    /**
     * 결제 취소
     * @param orderId 주문 ID
     * @return 결제 취소 성공 여부
     */
    boolean cancelPayment(Long orderId);

    @Async("asyncExecutor")
    void processSingleCanceledPaymentAsync(String paymentKey);
}
