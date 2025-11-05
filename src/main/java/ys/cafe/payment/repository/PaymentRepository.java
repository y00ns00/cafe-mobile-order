package ys.cafe.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ys.cafe.payment.domain.Payment;
import ys.cafe.payment.domain.PaymentStatus;
import ys.cafe.payment.domain.vo.PaymentKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * 주문 ID로 결제 조회
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * 회원 ID로 결제 목록 조회
     */
    List<Payment> findByMemberId(Long memberId);

    /**
     * 결제 상태로 조회
     */
    Optional<Payment> findByPaymentKeyAndStatus(String paymentKey, PaymentStatus status);

    /**
     * 특정 상태의 모든 결제 조회
     */
    @Query("""
            SELECT p.paymentKey
            FROM Payment p
            WHERE p.status = :status
            """)
    List<String> findPaymentKeyByStatus(PaymentStatus status);

    Optional<Payment> findByPaymentKey(PaymentKey paymentKey);
}