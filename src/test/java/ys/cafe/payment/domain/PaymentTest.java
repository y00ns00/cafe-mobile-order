package ys.cafe.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ys.cafe.payment.domain.vo.Won;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("Payment를 생성할 수 있다")
    void createPayment() {
        // given
        String paymentKey = "test_payment_key_123";
        Long orderId = 1L;
        Long memberId = 100L;
        Won amount = Won.of(10000);

        // when
        Payment payment = Payment.create(paymentKey, orderId, memberId, amount);

        // then
        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getMemberId()).isEqualTo(memberId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Payment를 성공 상태로 변경할 수 있다")
    void markAsSuccess() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );

        // when
        payment.markAsSuccess();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Payment를 실패 상태로 변경할 수 있다")
    void markAsFailed() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );

        // when
        payment.markAsFailed();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Payment를 취소 상태로 변경할 수 있다")
    void markAsCanceled() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );

        // when
        payment.markAsCanceled();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Payment를 취소 완료 상태로 변경할 수 있다")
    void markAsCancelCompleted() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );

        // when
        payment.markAsCancelCompleted();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCEL_COMPLETED);
        assertThat(payment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Payment 생성 시 초기 상태는 PENDING이다")
    void initialStatusIsPending() {
        // when
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Payment의 모든 정보를 조회할 수 있다")
    void getPaymentInfo() {
        // given
        String paymentKey = "test_payment_key_123";
        Long orderId = 1L;
        Long memberId = 100L;
        Won amount = Won.of(10000);

        // when
        Payment payment = Payment.create(paymentKey, orderId, memberId, amount);

        // then
        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getMemberId()).isEqualTo(memberId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getTransactionId()).isNull();
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("PENDING 상태에서 SUCCESS로 상태를 변경할 수 있다")
    void changeStatusFromPendingToSuccess() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);

        // when
        payment.markAsSuccess();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("SUCCESS 상태에서 CANCELED로 상태를 변경할 수 있다")
    void changeStatusFromSuccessToCanceled() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );
        payment.markAsSuccess();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        // when
        payment.markAsCanceled();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

    @Test
    @DisplayName("CANCELED 상태에서 CANCEL_COMPLETED로 상태를 변경할 수 있다")
    void changeStatusFromCanceledToCancelCompleted() {
        // given
        Payment payment = Payment.create(
                "test_payment_key",
                1L,
                100L,
                Won.of(10000)
        );
        payment.markAsCanceled();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);

        // when
        payment.markAsCancelCompleted();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCEL_COMPLETED);
    }
}