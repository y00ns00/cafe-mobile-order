package ys.cafe.order.out.port;

import ys.cafe.order.domain.vo.Won;
import ys.cafe.payment.service.dto.response.PaymentInfoResponse;

/**
 * Payment 아웃바운드 포트
 * Order 도메인에서 Payment 기능을 사용하기 위한 인터페이스
 */
public interface PaymentPort {

    /**
     * 결제 처리
     * @param orderId 주문 ID
     * @param memberId 회원 ID
     * @param amount 결제 금액
     * @return 결제 성공 여부
     */
    boolean processPayment(Long orderId, Long memberId, Won amount);

    /**
     * 결제 취소
     * @param orderId 주문 ID
     * @return 결제 취소 성공 여부
     */
    PaymentInfoResponse cancelPayment(Long orderId);
}
