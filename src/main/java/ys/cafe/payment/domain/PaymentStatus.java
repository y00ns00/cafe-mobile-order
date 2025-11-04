package ys.cafe.payment.domain;

public enum PaymentStatus {
    PENDING,            // 결제 대기
    SUCCESS,            // 결제 성공
    FAILED,             // 결제 실패
    CANCELED,           // 결제 취소 (내부 취소 완료, 외부 취소 대기)
    CANCEL_COMPLETED    // 결제 취소 완료 (외부 결제 시스템 취소 완료)
}