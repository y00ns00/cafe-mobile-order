package ys.cafe.payment.out.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ys.cafe.payment.service.dto.response.PaymentResponse;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoCoaPaymentGateWay implements PaymentClient {

    private static final Random random = new Random();
    private static final int MIN_DELAY_MS = 100;
    private static final int MAX_DELAY_MS = 3000;
    private static final double FAILURE_RATE = 0.3; // 30% 실패 확률

    @Override
    public PaymentResponse pay(
            String name,
            String birthDate,
            String phone,
            String amount
    ) {
        String paymentKey = UUID.randomUUID().toString();
        // 랜덤 지연시간
        simulateNetworkDelay();

        // 일정 확률로 실패
        if (random.nextDouble() < FAILURE_RATE) {
            log.warn("결제 실패: paymentKey={}, name={}, amount={}", paymentKey, name, amount);

            return PaymentResponse.failure("외부 결제 게이트웨이 처리 실패");
        }

        return PaymentResponse.success(paymentKey);
    }


    private void simulateNetworkDelay() {
        try {
            int delay = MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("결제 처리 중 인터럽트 발생", e);
        }
    }


    @Override
    @Async("asyncExecutor")
    public CompletableFuture<PaymentResponse> cancel(String paymentKey) {
        simulateNetworkDelay();

        // 일정 확률로 실패
        if (random.nextDouble() < FAILURE_RATE) {
            log.warn("취소 실패: paymentKey={} ", paymentKey);

            return CompletableFuture.completedFuture(PaymentResponse.failure("외부 결제 게이트웨이 처리 실패"));
        }

        return CompletableFuture.completedFuture(PaymentResponse.success(paymentKey));
    }
}
