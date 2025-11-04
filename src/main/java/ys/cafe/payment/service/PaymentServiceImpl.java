package ys.cafe.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.common.vo.Won;
import ys.cafe.payment.adapter.PaymentClient;
import ys.cafe.payment.domain.PaymentStatus;
import ys.cafe.payment.port.MemberPort;
import ys.cafe.payment.port.OrderPort;
import ys.cafe.payment.domain.Payment;
import ys.cafe.payment.repository.PaymentRepository;
import ys.cafe.payment.service.dto.MemberDTO;
import ys.cafe.payment.service.dto.PaymentInfoDTO;
import ys.cafe.payment.service.dto.PaymentResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final MemberPort memberPort;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    @Transactional(propagation = REQUIRES_NEW)
    @Override
    public PaymentResponse processPayment(
            Long orderId,
            Long memberId,
            Won won
    ) {
        MemberDTO member = memberPort.getMember(memberId);

        Payment payment = Payment.create(
                UUID.randomUUID().toString(),
                orderId,
                memberId,
                member.getName(),
                member.getBirthDate(),
                member.getPhoneNumber(),
                won.toPlainString()
        );

        PaymentResponse response = paymentClient.pay(
                payment.getName(),
                payment.getBirthDate(),
                payment.getPhone(),
                won.toPlainString()
        );

        if(response.isSuccess()) {
            payment.markAsSuccess();
        } else {
            payment.markAsFailed();
        }

        paymentRepository.save(payment);

        return response;
    }

    /**
     * 결제 정보 조회
     * Controller에서 결제 페이지 표시 시 사용
     */
    public PaymentInfoDTO getPaymentInfo(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다: " + paymentKey));

        return PaymentInfoDTO.of(
                payment.getOrderId(),
                payment.getName(),
                payment.getAmount()
        );
    }

    /**
     * 결제 취소
     * @param orderId 주문 ID
     * @return 결제 취소 성공 여부
     */
    @Transactional
    @Override
    public boolean cancelPayment(Long orderId) {
        try {
            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다. orderId: " + orderId));

            payment.markAsCanceled();
            paymentRepository.save(payment);

            log.info("결제 취소 완료 - orderId: {}, paymentKey: {}", orderId, payment.getPaymentKey());
            return true;
        } catch (Exception e) {
            log.error("결제 취소 실패 - orderId: {}, error: {}", orderId, e.getMessage(), e);
            return false;
        }
    }


    /**
     * 단일 결제를 비동기로 외부 시스템에 취소 요청
     * AsyncConfig에 정의된 asyncExecutor 스레드풀 사용
     * 트랜잭션 처리는 PaymentTransactionHelper에 위임
     *
     * @param paymentKey 결제 키
     */
    @Override
    public void processSingleCanceledPaymentAsync(String paymentKey) {
        try {
            Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                    .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND,
                            "결제 정보를 찾을 수 없습니다. paymentKey: " + paymentKey));

            // CANCELED 상태가 아니면 처리하지 않음
            if (payment.getStatus() != PaymentStatus.CANCELED) {
                log.warn("CANCELED 상태가 아닌 결제 - paymentKey: {}, currentStatus: {}",
                        paymentKey, payment.getStatus());
                return;
            }

            log.info("외부 결제 시스템 취소 요청 시작 - paymentKey: {}, orderId: {}",
                    paymentKey, payment.getOrderId());

            // 외부 결제 시스템에 취소 요청
            CompletableFuture<PaymentResponse> cancelResponseFuture = paymentClient.cancel(paymentKey);
            PaymentResponse cancelResponse = cancelResponseFuture.get();

            if (cancelResponse.isSuccess()) {
                // 취소 완료 상태로 변경
                payment.markAsCancelCompleted();
                paymentRepository.save(payment);

                log.info("외부 결제 시스템 취소 완료 - paymentKey: {}, orderId: {}",
                        paymentKey, payment.getOrderId());
            } else {
                log.error("외부 결제 시스템 취소 실패 - paymentKey: {}, orderId: {}, reason: {}",
                        paymentKey, payment.getOrderId(), cancelResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("외부 결제 시스템 취소 중 예외 발생 - paymentKey: {}, error: {}",
                    paymentKey, e.getMessage(), e);
        }
    }
}
