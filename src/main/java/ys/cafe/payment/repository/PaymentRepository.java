package ys.cafe.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ys.cafe.payment.domain.Payment;
import ys.cafe.payment.domain.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * 주문 ID로 결제 조회
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * 결제 상태로 조회
     */
    Optional<Payment> findByPaymentKeyAndStatus(String paymentKey, PaymentStatus status);

    /**
     * 특정 상태의 모든 결제 조회
     */
    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByPaymentKey(String paymentKey);
}