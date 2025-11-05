package ys.cafe.payment.domain;

import jakarta.persistence.*;
import ys.cafe.common.vo.Won;
import ys.cafe.payment.domain.vo.PaymentKey;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "payment_key", length = 100)
    private PaymentKey paymentKey;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Embedded
    private Won amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Payment() {}

    public static Payment create(
            String paymentKey,
            Long orderId,
            Long memberId,
            Won amount
    ) {
        Payment payment = new Payment();
        payment.paymentKey = PaymentKey.of(paymentKey);
        payment.orderId = orderId;
        payment.memberId = memberId;
        payment.amount = amount;
        payment.status = PaymentStatus.PENDING;
        payment.createdAt = LocalDateTime.now();
        return payment;
    }

    public void markAsSuccess() {
        this.status = PaymentStatus.SUCCESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCanceled() {
        this.status = PaymentStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCancelCompleted() {
        this.status = PaymentStatus.CANCEL_COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getPaymentKey() {
        return paymentKey.getValue();
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Won getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}