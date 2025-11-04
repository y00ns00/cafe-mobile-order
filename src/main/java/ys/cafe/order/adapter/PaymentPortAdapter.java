package ys.cafe.order.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ys.cafe.common.vo.Won;
import ys.cafe.order.port.PaymentPort;
import ys.cafe.payment.service.PaymentService;
import ys.cafe.payment.service.dto.PaymentResponse;

/**
 * Payment 아웃바운드 어댑터
 * PaymentPort를 구현하여 Payment 서비스와 통신
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentPortAdapter implements PaymentPort {

    private final PaymentService paymentService;

    @Override
    public boolean processPayment(Long orderId, Long memberId, Won amount) {
        PaymentResponse paymentResponse = paymentService.processPayment(orderId, memberId, amount);
        return paymentResponse.isSuccess();
    }

    @Override
    public boolean cancelPayment(Long orderId) {
        boolean result = paymentService.cancelPayment(orderId);
        if (result) {
            log.info("결제 취소 성공 - orderId: {}", orderId);
        } else {
            log.error("결제 취소 실패 - orderId: {}", orderId);
        }
        return result;
    }
}
