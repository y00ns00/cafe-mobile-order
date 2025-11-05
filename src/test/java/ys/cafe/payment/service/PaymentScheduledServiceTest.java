package ys.cafe.payment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ys.cafe.payment.domain.PaymentStatus;
import ys.cafe.payment.repository.PaymentRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentScheduledService 테스트")
class PaymentScheduledServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentScheduledService paymentScheduledService;

    @Test
    @DisplayName("CANCELED 상태 결제가 존재하면 비동기 취소 처리를 트리거한다")
    void processCanceledPayments_CanceledPaymentsExist() {
        // given
        List<String> canceledKeys = List.of("key-1", "key-2");
        when(paymentRepository.findPaymentKeyByStatus(PaymentStatus.CANCELED))
                .thenReturn(canceledKeys);

        // when
        paymentScheduledService.processCanceledPayments();

        // then
        verify(paymentRepository).findPaymentKeyByStatus(PaymentStatus.CANCELED);
        verify(paymentService).processSingleCanceledPaymentAsync("key-1");
        verify(paymentService).processSingleCanceledPaymentAsync("key-2");
    }

    @Test
    @DisplayName("CANCELED 상태 결제가 없으면 비동기 취소 처리를 호출하지 않는다")
    void processCanceledPayments_NoCanceledPayments() {
        // given
        when(paymentRepository.findPaymentKeyByStatus(PaymentStatus.CANCELED))
                .thenReturn(List.of());

        // when
        paymentScheduledService.processCanceledPayments();

        // then
        verify(paymentRepository).findPaymentKeyByStatus(PaymentStatus.CANCELED);
        verify(paymentService, never()).processSingleCanceledPaymentAsync(anyString());
    }
}
