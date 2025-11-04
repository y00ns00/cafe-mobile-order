package ys.cafe.payment.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "payment_key", length = 100)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date", length = 20)
    private String birthDate;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "amount", nullable = false, length = 50)
    private String amount;

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
            String name,
            String birthDate,
            String phone,
            String amount
    ) {
        Payment payment = new Payment();
        payment.paymentKey = paymentKey;
        payment.orderId = orderId;
        payment.memberId = memberId;
        payment.name = name;
        payment.birthDate = birthDate;
        payment.phone = phone;
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
        return paymentKey;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getAmount() {
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