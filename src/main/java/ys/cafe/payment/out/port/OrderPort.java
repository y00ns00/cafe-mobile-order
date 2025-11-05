package ys.cafe.payment.out.port;

/**
 * Order 아웃바운드 포트
 * Payment 도메인에서 Order 도메인에 접근하기 위한 인터페이스
 */
public interface OrderPort {

    /**
     * 결제 대기 상태 검증
     * Order가 PAYMENT_WAITING 상태인지 확인
     */
    void validatePaymentWaiting(Long orderId);

    /**
     * 결제 완료 처리
     * Order 상태를 PAYMENT_WAITING → PREPARING으로 변경
     */
    void completePayment(Long orderId);

    /**
     * 결제 실패 처리
     * Order 상태를 PAYMENT_WAITING → CANCELED로 변경
     */
    void failPayment(Long orderId);
}
