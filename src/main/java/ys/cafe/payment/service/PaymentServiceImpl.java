package ys.cafe.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.common.vo.Won;
import ys.cafe.payment.adapter.PaymentClient;
import ys.cafe.payment.domain.PaymentStatus;
import ys.cafe.payment.domain.vo.PaymentKey;
import ys.cafe.payment.port.MemberPort;
import ys.cafe.payment.domain.Payment;
import ys.cafe.payment.repository.PaymentRepository;
import ys.cafe.payment.service.dto.MemberDTO;
import ys.cafe.payment.service.dto.PaymentInfoResponse;
import ys.cafe.payment.service.dto.PaymentResponse;

import java.util.List;
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
                won
        );

        PaymentResponse response = paymentClient.pay(
                member.getName(),
                member.getBirthDate(),
                member.getPhoneNumber(),
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
    public PaymentInfoResponse getPaymentInfo(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다: " + paymentKey));

        return PaymentInfoResponse.of(
                payment.getOrderId(),
                payment.getMemberId(),
                payment.getAmount().toPlainString(),
                payment.getStatus().name()
        );
    }

    /**
     * 사용자의 결제 목록 조회
     * @param memberId 회원 ID
     * @return 결제 정보 목록
     */
    @Override
    public List<PaymentInfoResponse> getUserPayments(Long memberId) {
        List<Payment> payments = paymentRepository.findByMemberId(memberId);

        return payments.stream()
                .map(payment -> PaymentInfoResponse.of(
                        payment.getOrderId(),
                        payment.getMemberId(),
                        payment.getAmount().toPlainString(),
                        payment.getStatus().name()
                ))
                .toList();
    }

    /**
     * 결제 취소
     */
    @Transactional
    @Override
    public PaymentInfoResponse cancelPayment(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다. orderId: " + orderId));

        payment.markAsCanceled();
        paymentRepository.save(payment);

        log.info("결제 취소 등록 완료 - orderId: {}, paymentKey: {}", orderId, payment.getPaymentKey());
        return PaymentInfoResponse.of(
                payment.getOrderId(),
                payment.getMemberId(),
                payment.getAmount().toPlainString(),
                payment.getStatus().name()
        );
    }


    /**
     * 단일 결제를 비동기로 외부 시스템에 취소 요청
     * AsyncConfig에 정의된 asyncExecutor 스레드풀 사용
     * 트랜잭션 처리는 PaymentTransactionHelper에 위임
     *
     * @param paymentKey 결제 키
     */
    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void processSingleCanceledPaymentAsync(String paymentKey) {
        try {
            Payment payment = paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))
                    .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다. paymentKey: " + paymentKey));

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
