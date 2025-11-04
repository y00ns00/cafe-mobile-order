package ys.cafe.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ys.cafe.payment.domain.Payment;
import ys.cafe.payment.domain.PaymentStatus;
import ys.cafe.payment.repository.PaymentRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentScheduledService {

    private final PaymentRepository paymentRepository;

    private final PaymentService paymentService;

    /**
     * CANCELED 상태의 결제들을 외부 결제 시스템에 취소 요청
     * 매 5분마다 실행
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void processCanceledPayments() {
        log.info("CANCELED 상태 결제 처리 시작");

        List<Payment> canceledPayments = paymentRepository.findByStatus(PaymentStatus.CANCELED);

        if (canceledPayments.isEmpty()) {
            log.info("처리할 CANCELED 상태 결제 없음");
            return;
        }

        log.info("CANCELED 상태 결제 {}건 발견 - 비동기 처리 시작", canceledPayments.size());

        // 각 결제를 비동기로 처리 (paymentKey만 전달하여 영속성 컨텍스트 문제 회피)
        for (Payment payment : canceledPayments) {
            paymentService.processSingleCanceledPaymentAsync(payment.getPaymentKey());
        }
    }
}
