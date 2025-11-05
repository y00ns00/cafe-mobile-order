package ys.cafe.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ys.cafe.common.exception.CommonException;
import ys.cafe.common.vo.Won;
import ys.cafe.payment.adapter.PaymentClient;
import ys.cafe.payment.domain.Payment;
import ys.cafe.payment.domain.PaymentStatus;
import ys.cafe.payment.domain.vo.PaymentKey;
import ys.cafe.payment.port.MemberPort;
import ys.cafe.payment.repository.PaymentRepository;
import ys.cafe.payment.service.dto.MemberDTO;
import ys.cafe.payment.service.dto.PaymentInfoResponse;
import ys.cafe.payment.service.dto.PaymentResponse;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 테스트")
class PaymentServiceImplTest {

    @Mock
    private MemberPort memberPort;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Long orderId;
    private Long memberId;
    private Won amount;
    private MemberDTO memberDTO;
    private Payment payment;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        memberId = 1L;
        amount = Won.of(5000);

        memberDTO = MemberDTO.of(
                "홍길동",
                "010-1234-5678",
                "MALE",
                "19900101",
                "2025-11-01T12:00:00"
        );

        payment = Payment.create(
                "test-payment-key",
                orderId,
                memberId,
                Won.of(5000)
        );
    }

    @Test
    @DisplayName("결제 처리 성공")
    void processPayment_Success() {
        // given
        given(memberPort.getMember(memberId)).willReturn(memberDTO);
        given(paymentClient.pay(anyString(), anyString(), anyString(), anyString()))
                .willReturn(PaymentResponse.success("test-payment-key"));
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        PaymentResponse response = paymentService.processPayment(orderId, memberId, amount);

        // then
        assertThat(response.isSuccess()).isTrue();

        verify(memberPort).getMember(memberId);
        verify(paymentClient).pay(anyString(), anyString(), anyString(), anyString());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 처리 실패")
    void processPayment_Fail() {
        // given
        given(memberPort.getMember(memberId)).willReturn(memberDTO);
        given(paymentClient.pay(anyString(), anyString(), anyString(), anyString()))
                .willReturn(PaymentResponse.failure("결제 실패"));
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        PaymentResponse response = paymentService.processPayment(orderId, memberId, amount);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("결제 실패");

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 정보 조회 성공")
    void getPaymentInfo_Success() {
        // given
        String paymentKey = "test-payment-key";
        payment.markAsSuccess();
        given(paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))).willReturn(Optional.of(payment));

        // when
        PaymentInfoResponse paymentInfo = paymentService.getPaymentInfo(paymentKey);

        // then
        assertThat(paymentInfo).isNotNull();
        assertThat(paymentInfo.getOrderId()).isEqualTo(orderId);
        assertThat(paymentInfo.getMemberId()).isEqualTo(memberId);
        assertThat(paymentInfo.getAmount()).isEqualTo("5000");

        verify(paymentRepository).findByPaymentKey(PaymentKey.of(paymentKey));
    }

    @Test
    @DisplayName("결제 정보 조회 실패 - 결제 정보 없음")
    void getPaymentInfo_NotFound() {
        // given
        String paymentKey = "invalid-key";
        given(paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentInfo(paymentKey))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining("결제 정보를 찾을 수 없습니다");

        verify(paymentRepository).findByPaymentKey(PaymentKey.of(paymentKey));
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_Success() {
        // given
        payment.markAsSuccess();
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        PaymentInfoResponse result = paymentService.cancelPayment(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo("CANCELED");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);

        verify(paymentRepository).findByOrderId(orderId);
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("결제 취소 실패 - 결제 정보 없음")
    void cancelPayment_NotFound() {
        // given
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(orderId))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining("결제 정보를 찾을 수 없습니다");

        verify(paymentRepository).findByOrderId(orderId);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("비동기 결제 취소 처리 성공")
    void processSingleCanceledPaymentAsync_Success() {
        // given
        String paymentKey = "test-payment-key";
        payment.markAsCanceled();

        given(paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))).willReturn(Optional.of(payment));
        given(paymentClient.cancel(paymentKey))
                .willReturn(CompletableFuture.completedFuture(PaymentResponse.success(paymentKey)));
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        paymentService.processSingleCanceledPaymentAsync(paymentKey);

        // then
        verify(paymentRepository).findByPaymentKey(PaymentKey.of(paymentKey));
        verify(paymentClient).cancel(paymentKey);
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("비동기 결제 취소 처리 - CANCELED 상태가 아닌 경우 처리하지 않음")
    void processSingleCanceledPaymentAsync_NotCanceledStatus() {
        // given
        String paymentKey = "test-payment-key";
        payment.markAsSuccess(); // CANCELED가 아닌 SUCCESS 상태

        given(paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))).willReturn(Optional.of(payment));

        // when
        paymentService.processSingleCanceledPaymentAsync(paymentKey);

        // then
        verify(paymentRepository).findByPaymentKey(PaymentKey.of(paymentKey));
        verify(paymentClient, never()).cancel(anyString());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("비동기 결제 취소 처리 실패 - 결제 정보 없음")
    void processSingleCanceledPaymentAsync_NotFound() {
        // given
        String paymentKey = "invalid-key";
        given(paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))).willReturn(Optional.empty());

        // when
        paymentService.processSingleCanceledPaymentAsync(paymentKey);

        // then
        verify(paymentRepository).findByPaymentKey(PaymentKey.of(paymentKey));
        verify(paymentClient, never()).cancel(anyString());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("비동기 결제 취소 처리 - 외부 시스템 취소 실패")
    void processSingleCanceledPaymentAsync_ExternalCancelFail() {
        // given
        String paymentKey = "test-payment-key";
        payment.markAsCanceled();

        given(paymentRepository.findByPaymentKey(PaymentKey.of(paymentKey))).willReturn(Optional.of(payment));
        given(paymentClient.cancel(paymentKey))
                .willReturn(CompletableFuture.completedFuture(PaymentResponse.failure("외부 시스템 오류")));

        // when
        paymentService.processSingleCanceledPaymentAsync(paymentKey);

        // then
        verify(paymentRepository).findByPaymentKey(PaymentKey.of(paymentKey));
        verify(paymentClient).cancel(paymentKey);
        verify(paymentRepository, never()).save(any()); // 실패 시 저장하지 않음
    }

    @Test
    @DisplayName("사용자 결제 목록 조회 성공")
    void getUserPayments_Success() {
        // given
        Payment payment1 = Payment.create("key-1", 1L, memberId, Won.of(5000));
        payment1.markAsSuccess();
        Payment payment2 = Payment.create("key-2", 2L, memberId, Won.of(10000));
        payment2.markAsSuccess();
        Payment payment3 = Payment.create("key-3", 3L, memberId, Won.of(3000));
        payment3.markAsCanceled();

        given(paymentRepository.findByMemberId(memberId))
                .willReturn(List.of(payment1, payment2, payment3));

        // when
        List<PaymentInfoResponse> results = paymentService.getUserPayments(memberId);

        // then
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getOrderId()).isEqualTo(1L);
        assertThat(results.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(results.get(0).getAmount()).isEqualTo("5000");
        assertThat(results.get(0).getStatus()).isEqualTo("SUCCESS");
        assertThat(results.get(1).getAmount()).isEqualTo("10000");
        assertThat(results.get(2).getStatus()).isEqualTo("CANCELED");

        verify(paymentRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("사용자 결제 목록 조회 - 결제 내역 없음")
    void getUserPayments_EmptyList() {
        // given
        given(paymentRepository.findByMemberId(memberId)).willReturn(List.of());

        // when
        List<PaymentInfoResponse> results = paymentService.getUserPayments(memberId);

        // then
        assertThat(results).isEmpty();

        verify(paymentRepository).findByMemberId(memberId);
    }
}
